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

package org.apache.ignite.ml.util.generators.primitives.scalar;

import java.util.Random;

/**
 * Base class for generators based on basic java Random.
 */
abstract class RandomProducerWithGenerator implements RandomProducer {
    /** Rnd. */
    private final Random rnd;

    /**
     * Creates an instance of RandomProducerWithGenerator.
     */
    protected RandomProducerWithGenerator() {
        this(System.currentTimeMillis());
    }

    /**
     * Creates an instance of RandomProducerWithGenerator.
     *
     * @param seed Seed.
     */
    protected RandomProducerWithGenerator(long seed) {
        this.rnd = new Random(seed);
    }

    /**
     * @return Java preudorandom values generator.
     */
    protected Random generator() {
        return rnd;
    }
}
