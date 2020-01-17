/*
* Licensed to the Apache Software Foundation (ASF) under one or more
* contributor license agreements.  See the NOTICE file distributed with
* this work for additional information regarding copyright ownership.
* The ASF licenses this file to You under the Apache License, Version 2.0
* (the "License"); you may not use this file except in compliance with
* the License.  You may obtain a copy of the License at
*
*      http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package org.apache.ignite.internal.processors.cache.checker.processor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.cache.CacheAtomicityMode;
import org.apache.ignite.cache.CacheWriteSynchronizationMode;
import org.apache.ignite.cache.affinity.rendezvous.RendezvousAffinityFunction;
import org.apache.ignite.configuration.CacheConfiguration;
import org.apache.ignite.configuration.DataRegionConfiguration;
import org.apache.ignite.configuration.DataStorageConfiguration;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.internal.IgniteEx;
import org.apache.ignite.internal.IgniteInternalFuture;
import org.apache.ignite.internal.processors.cache.CacheObject;
import org.apache.ignite.internal.processors.cache.GridCacheContext;
import org.apache.ignite.internal.processors.cache.checker.objects.ReconciliationResult;
import org.apache.ignite.testframework.GridTestUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

/**
 *
 */
@RunWith(Parameterized.class)
public class PartitionReconciliationStressTest extends PartitionReconciliationAbstractTest {
    /** Nodes. */
    private static final int NODES_CNT = 4;

    /** Keys count. */
    private static final int KEYS_CNT = 2000;

    /** Corrupted keys count. */
    private static final int BROKEN_KEYS_CNT = 500;

    /** Cache atomicity mode. */
    @Parameterized.Parameter(0)
    public CacheAtomicityMode cacheAtomicityMode;

    /** Parts. */
    @Parameterized.Parameter(1)
    public int parts;

    /** Fix mode. */
    @Parameterized.Parameter(2)
    public boolean fixMode;

    /** Crd server node. */
    private IgniteEx ig;

    /** Client. */
    private IgniteEx client;

    /** {@inheritDoc} */
    @Override protected IgniteConfiguration getConfiguration(String name) throws Exception {
        IgniteConfiguration cfg = super.getConfiguration(name);

        cfg.setDataStorageConfiguration(new DataStorageConfiguration()
            .setDefaultDataRegionConfiguration(new DataRegionConfiguration()
                .setPersistenceEnabled(true)
                .setMaxSize(300L * 1024 * 1024))
        );

        CacheConfiguration ccfg = new CacheConfiguration();

        ccfg.setName(DEFAULT_CACHE_NAME);
        ccfg.setAtomicityMode(cacheAtomicityMode);
        ccfg.setWriteSynchronizationMode(CacheWriteSynchronizationMode.FULL_SYNC);
        ccfg.setAffinity(new RendezvousAffinityFunction(false, parts));
        ccfg.setBackups(NODES_CNT - 1);

        cfg.setCacheConfiguration(ccfg);
        cfg.setConsistentId(name);

        return cfg;
    }

    /** {@inheritDoc} */
    @Override protected void beforeTest() throws Exception {
        stopAllGrids();

        cleanPersistenceDir();

        ig = startGrids(NODES_CNT);

        client = startClientGrid(NODES_CNT);

        ig.cluster().active(true);
    }

    /** {@inheritDoc} */
    @Override protected void afterTest() throws Exception {
        stopAllGrids();

        cleanPersistenceDir();
    }

    /** */
    @Parameterized.Parameters(name = "atomicity = {0}, partitions = {1}, fixModeEnabled = {2}")
    public static List<Object[]> parameters() {
        ArrayList<Object[]> params = new ArrayList<>();

        CacheAtomicityMode[] atomicityModes = new CacheAtomicityMode[]{
            CacheAtomicityMode.ATOMIC, CacheAtomicityMode.TRANSACTIONAL};

        int[] partitions = {1, 32};

        for (CacheAtomicityMode atomicityMode : atomicityModes) {
            for (int parts : partitions)
                params.add(new Object[] {atomicityMode, parts, false});
        }

        return params;
    }

    /**
     * Test #37 Stress test for reconciliation under load
     *
     * @throws Exception If failed.
     */
    @Test
    public void testReconciliationOfColdKeysUnderLoad() throws Exception {
        IgniteCache<Integer, String> clientCache = client.cache(DEFAULT_CACHE_NAME);

        for (int i = 0; i < KEYS_CNT; i++)
            clientCache.put(i, String.valueOf(i));

        log.info(">>>> Initial data loading finished");

        int firstBrokenKey = KEYS_CNT / 2;

        GridCacheContext[] nodeCacheCtxs = new GridCacheContext[NODES_CNT];

        for (int i = 0; i < NODES_CNT; i++)
            nodeCacheCtxs[i] = grid(i).cachex(DEFAULT_CACHE_NAME).context();

        Set<Integer> corruptedColdKeys = new HashSet<>();
        Set<Integer> corruptedHotKeys = new HashSet<>();

        for (int i = firstBrokenKey; i < firstBrokenKey + BROKEN_KEYS_CNT; i++) {
            if (isHotKey(i))
                corruptedHotKeys.add(i);
            else
                corruptedColdKeys.add(i);

            if (i % 3 == 0)
                simulateMissingEntryCorruption(nodeCacheCtxs[i % NODES_CNT], i);
            else
                simulateOutdatedVersionCorruption(nodeCacheCtxs[i % NODES_CNT], i);
        }

        log.info(">>>> Simulating data corruption finished");

        AtomicBoolean stopRandomLoad = new AtomicBoolean(false);

        IgniteInternalFuture<Long> randLoadFut = GridTestUtils.runMultiThreadedAsync(new Runnable() {
            @Override public void run() {
                while (!stopRandomLoad.get()) {
                    int i = ThreadLocalRandom.current().nextInt();

                    if (isHotKey(i))
                        clientCache.put(i, String.valueOf(2 * i));
                }
            }
        }, 6, "rand-loader");

        ReconciliationResult res = partitionReconciliation(ig, fixMode, DEFAULT_CACHE_NAME);

        log.info(">>>> Partition reconciliation finished");

        stopRandomLoad.set(true);

        randLoadFut.get();

        assertResultContainsConflictKeys(ig, res, DEFAULT_CACHE_NAME, corruptedColdKeys, CacheObject.TYPE_REGULAR);

        Set<Integer> conflictKeys = conflictKeys(ig, res, DEFAULT_CACHE_NAME, CacheObject.TYPE_REGULAR);

        assertTrue("Too many broken keys: " + conflictKeys.size(), conflictKeys.size() <= BROKEN_KEYS_CNT);

        for (Integer i : conflictKeys)
            assertTrue("Unexpected corrupt report: " + i, i >= firstBrokenKey && i < firstBrokenKey + BROKEN_KEYS_CNT);
    }

    /**
     * @param key Key.
     */
    private static boolean isHotKey(int key) {
        return key % 13 == 5 || key % 13 == 7 || key % 13 == 11;
    }
}
