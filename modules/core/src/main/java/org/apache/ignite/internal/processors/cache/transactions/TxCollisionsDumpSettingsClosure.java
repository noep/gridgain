/*
 * Copyright 2020 GridGain Systems, Inc. and Contributors.
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

package org.apache.ignite.internal.processors.cache.transactions;

import org.apache.ignite.internal.IgniteEx;
import org.apache.ignite.lang.IgniteRunnable;
import org.apache.ignite.resources.IgniteInstanceResource;

/**
 * Change tx collisions interval, -1 for disabling.
 */
public class TxCollisionsDumpSettingsClosure implements IgniteRunnable {
    /** Serialization ID. */
    private static final long serialVersionUID = 0L;

    /** Auto-inject Ignite instance. */
    @IgniteInstanceResource
    private IgniteEx ignite;

    /** Collision dump interval. */
    private final int interval;

    /** Constructor. */
    TxCollisionsDumpSettingsClosure(int timeout) {
        interval = timeout;
    }

    /** {@inheritDoc} */
    @Override public void run() {
        ignite.context().cache().context().tm().txCollisionsDumpInterval(interval);
    }
}