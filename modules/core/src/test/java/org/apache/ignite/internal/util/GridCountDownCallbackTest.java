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
package org.apache.ignite.internal.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 *
 */
public class GridCountDownCallbackTest {
    /** */
    private void testCountDownCallback() throws InterruptedException {
        AtomicInteger cntr = new AtomicInteger(0);
        AtomicInteger performedCntr = new AtomicInteger(0);

        AtomicBoolean res = new AtomicBoolean();

        final int countsTillCb = 30;

        GridCountDownCallback cb = new GridCountDownCallback(
            countsTillCb,
            () -> res.set(cntr.get() == countsTillCb && performedCntr.get() == countsTillCb / 5),
            countsTillCb / 5
        );

        ExecutorService es = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

        for (int i = 0; i < countsTillCb; i++) {
            final int fi = i;

            es.submit(() -> {
                synchronized (es) {
                    cntr.incrementAndGet();

                    if (fi % 5 == 0)
                        performedCntr.incrementAndGet();

                    cb.countDown(fi % 5 == 0);
                }
            });
        }

        es.shutdown();

        es.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);

        assertTrue(res.get());
    }

    /** */
    @Test
    public void testStability() throws InterruptedException {
        for (int i = 0; i < 1000; i++)
            testCountDownCallback();
    }
}
