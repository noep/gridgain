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

package org.apache.ignite.spi;

import java.util.concurrent.ThreadFactory;
import org.apache.ignite.IgniteLogger;

/**
 * This class provides implementation of {@link ThreadFactory}  factory
 * for creating grid SPI threads.
 */
public class IgniteSpiThreadFactory implements ThreadFactory {
    /** */
    private final IgniteLogger log;

    /** */
    private final String igniteInstanceName;

    /** */
    private final String threadName;

    /**
     * @param igniteInstanceName Ignite instance name, possibly {@code null} for default Ignite instance.
     * @param threadName Name for threads created by this factory.
     * @param log Grid logger.
     */
    public IgniteSpiThreadFactory(String igniteInstanceName, String threadName, IgniteLogger log) {
        assert log != null;
        assert threadName != null;

        this.igniteInstanceName = igniteInstanceName;
        this.threadName = threadName;
        this.log = log;
    }

    /** {@inheritDoc} */
    @Override public Thread newThread(final Runnable r) {
        return new IgniteSpiThread(igniteInstanceName, threadName, log) {
            /** {@inheritDoc} */
            @Override protected void body() {
                r.run();
            }
        };
    }
}