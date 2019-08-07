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

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteException;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.internal.util.IgniteUtils;
import org.apache.ignite.lang.IgniteFuture;
import org.apache.ignite.lang.IgniteRunnable;
import org.apache.ignite.spi.collision.fifoqueue.FifoQueueCollisionSpi;
import org.apache.ignite.testframework.junits.common.GridCommonAbstractTest;
import org.junit.Test;

/**
 * Test of absence of gaps between jobs in compute
 */
public class IgniteComputeJobOneThreadTest extends GridCommonAbstractTest {
    @Override protected IgniteConfiguration getConfiguration(String name) throws Exception {
        FifoQueueCollisionSpi colSpi = new FifoQueueCollisionSpi();
        colSpi.setParallelJobsNumber(1);

        return super.getConfiguration(name)
            .setMetricsUpdateFrequency(10000)
            .setCollisionSpi(colSpi);
    }

    /** {@inheritDoc} */
    @Override protected void beforeTest() throws Exception {
        startGrid(0);
        startGrid(1);
    }

    /** {@inheritDoc} */
    @Override protected void afterTest() throws Exception {
        stopAllGrids();
    }

    /** {@inheritDoc} */
    @Override protected long getTestTimeout() {
        return 10000;
    }

    /**
     * @throws Exception If failed.
     */
    @Test
    public void testNoTimeout() throws Exception {
        Thread t = new Thread(() -> {
            while(true) {
                try {
                    Thread.sleep(1000);
                }
                catch (InterruptedException e) {
                    e.printStackTrace();
                }

                IgniteUtils.dumpThreads(log);
            }
        });

        t.start();

        Ignite ignite = ignite(0);

        Ignite runner = ignite(1);

        IgniteFuture fut = null;

        for (int i = 0; i < 10000; i++) {
            fut =  ignite.compute(ignite.cluster().forNodeId(runner.cluster().localNode().id()))
                .runAsync(new IgniteRunnable() {
                @Override public void run() {
                    try {
                        Thread.sleep(100);
                        throw new IgniteException();
                    }
                    catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
        }

        fut.get();

        assertTrue(true);
    }
}
