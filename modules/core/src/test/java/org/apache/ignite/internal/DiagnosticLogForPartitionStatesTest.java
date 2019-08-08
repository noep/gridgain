/*
 * Copyright 2019 GridGain Systems, Inc. and Contributors.
 *
 * Licensed under the GridGain Community Edition License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.gridgain.com/products/software/community-edition/gridgain-community-edition-license
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.ignite.internal;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import javax.cache.expiry.AccessedExpiryPolicy;
import javax.cache.expiry.Duration;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.configuration.CacheConfiguration;
import org.apache.ignite.configuration.DataRegionConfiguration;
import org.apache.ignite.configuration.DataStorageConfiguration;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.testframework.ListeningTestLogger;
import org.apache.ignite.testframework.LogListener;
import org.apache.ignite.testframework.junits.GridAbstractTest;
import org.apache.ignite.testframework.junits.common.GridCommonAbstractTest;
import org.junit.After;
import org.junit.Test;

/**
 * Checks diagnostic messages at PME.
 */
public class DiagnosticLogForPartitionStatesTest extends GridCommonAbstractTest {
    /** Cache 1. */
    private static final String CACHE_1 = "cache-1";

    /** Test logger. */
    private final ListeningTestLogger log = new ListeningTestLogger(false, GridAbstractTest.log);

    /** {@inheritDoc} */
    @Override protected IgniteConfiguration getConfiguration(String igniteInstanceName) throws Exception {
        return super.getConfiguration(igniteInstanceName)
            .setGridLogger(log)
            .setConsistentId(igniteInstanceName)
            .setDataStorageConfiguration(new DataStorageConfiguration()
                .setDefaultDataRegionConfiguration(new DataRegionConfiguration()
                    .setPersistenceEnabled(true)
                    .setMaxSize(200L * 1024 * 1024)));
    }

    /**
     * @throws Exception if stopAllGrid fails.
     */
    @After
    public void tearDown() throws Exception {
        stopAllGrids();
        cleanPersistenceDir();
    }

    /**
     *
     */
    @Test
    public void shouldPrintMessageIfPartitionHasOtherCounter() throws Exception {
        doTest(new CacheConfiguration<>(CACHE_1).setBackups(1), true);
    }

    /**
     *
     */
    @Test
    public void shouldNotPrintMessageIfPartitionHasOtherCounterButHasCustomExpiryPolicy() throws Exception {
        doTest(
            new CacheConfiguration<>(CACHE_1)
                .setBackups(1)
                .setExpiryPolicyFactory(AccessedExpiryPolicy.factoryOf(new Duration(TimeUnit.SECONDS, 1))),
            false
        );
    }

    /**
     * @param cacheCfg Cache config.
     * @param msgExp Message expected.
     */
    private void doTest(CacheConfiguration<Object, Object> cacheCfg, boolean msgExp) throws Exception {
        LogListener lsnr = LogListener.matches(s -> s.startsWith("Partition states validation has failed for group: " + CACHE_1 + "."))
            .times(msgExp ? 1 : 0).build();

        log.registerListener(lsnr);

        IgniteEx node1 = startGrid(0);

        IgniteEx node2 = startGrid(1);

        node1.cluster().active(true);

        IgniteCache<Object, Object> cache = node1.getOrCreateCache(cacheCfg);

        Set<Integer> clearKeys = new HashSet<>();

        for (int i = 0; i < 100; i++) {
            cache.put(i, i);

            if (node2.affinity(CACHE_1).isPrimary(node2.localNode(), i))
                clearKeys.add(i);
        }

        node2.context().cache().cache(CACHE_1).clearLocallyAll(clearKeys, true, true, true);

        //do exchange for validation trigger
        startGrid(2);

        awaitPartitionMapExchange();

        assertTrue(lsnr.check());
    }
}
