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

package org.apache.ignite.yardstick.thin.cache;

import java.util.Map;
import org.apache.ignite.client.ClientCache;
import org.apache.ignite.yardstick.cache.model.SampleValue;

/**
 * Thin client benchmark that performs put and get operations.
 */
public class IgniteThinPutGetBenchmark extends IgniteThinCacheAbstractBenchmark<Integer, Object> {
    /** {@inheritDoc} */
    @Override public boolean test(Map<Object, Object> ctx) throws Exception {
        int key = nextRandom(args.range());

        Object val = cache().get(key);

        if (val != null)
            key = nextRandom(args.range());

        cache().put(key, new SampleValue(key));

        return true;
    }

    /** {@inheritDoc} */
    @Override protected ClientCache<Integer, Object> cache() {
        return client().cache("atomic");
    }
}
