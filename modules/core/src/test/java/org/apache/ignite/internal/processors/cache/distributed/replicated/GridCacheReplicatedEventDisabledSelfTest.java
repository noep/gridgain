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

package org.apache.ignite.internal.processors.cache.distributed.replicated;

import org.apache.ignite.IgniteCheckedException;
import org.apache.ignite.configuration.CacheConfiguration;
import org.apache.ignite.events.Event;
import org.apache.ignite.lang.IgniteBiTuple;

/**
 * Tests events.
 */
public class GridCacheReplicatedEventDisabledSelfTest extends GridCacheReplicatedEventSelfTest {
    /** {@inheritDoc} */
    @Override protected CacheConfiguration cacheConfiguration(String igniteInstanceName) throws Exception {
        return super.cacheConfiguration(igniteInstanceName).setEventsDisabled(true);
    }

    /** {@inheritDoc} */
    @Override protected EventListener createEventListener() {
        return new TestEventListener();
    }

    /**
     * Test event listener.
     */
    private static class TestEventListener implements EventListener {
        /** {@inheritDoc} */
        @Override public void listen() {
            /* No-op. */
        }

        /** {@inheritDoc} */
        @Override public void stopListen() {
            /* No-op. */
        }

        /** {@inheritDoc} */
        @Override public int eventCount(int type) {
            return 0;
        }

        /** {@inheritDoc} */
        @Override public void reset() {
            /* No-op. */
        }

        /** {@inheritDoc} */
        @Override public void waitForEventCount(IgniteBiTuple<Integer, Integer>... evtCnts) throws IgniteCheckedException {
            /* No-op. */
        }

        /** {@inheritDoc} */
        @Override public boolean apply(Event evt) {
            fail("Cache events are disabled");

            return false;
        }
    }
}
