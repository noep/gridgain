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

package org.apache.ignite.ml.preprocessing;

import java.util.Map;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.ml.dataset.DatasetBuilder;
import org.apache.ignite.ml.dataset.impl.cache.CacheBasedDatasetBuilder;
import org.apache.ignite.ml.dataset.impl.local.LocalDatasetBuilder;
import org.apache.ignite.ml.environment.LearningEnvironment;
import org.apache.ignite.ml.environment.LearningEnvironmentBuilder;

/**
 * Trainer for preprocessor.
 *
 * @param <K> Type of a key in {@code upstream} data.
 * @param <V> Type of a value in {@code upstream} data.
 */
public abstract class PreprocessingTrainer<K, V> {
    /** Local environment. */
    private LearningEnvironment localEnvironment;

    /**
     * Fits preprocessor.
     *
     * @param envBuilder Learning environment builder.
     * @param datasetBuilder Dataset builder.
     * @param basePreprocessor Base preprocessor.
     * @return Preprocessor.
     */
    public abstract Preprocessor<K, V> fit(
        LearningEnvironmentBuilder envBuilder,
        DatasetBuilder<K, V> datasetBuilder,
        Preprocessor<K, V> basePreprocessor);

    /**
     * Fits preprocessor.
     *
     * @param datasetBuilder Dataset builder.
     * @param basePreprocessor Base preprocessor.
     * @return Preprocessor.
     */
    public Preprocessor<K, V> fit(
        DatasetBuilder<K, V> datasetBuilder,
        Preprocessor<K, V> basePreprocessor) {
        return fit(LearningEnvironmentBuilder.defaultBuilder(), datasetBuilder, basePreprocessor);
    }

    /**
     * Fits preprocessor.
     *
     * @param ignite Ignite instance.
     * @param cache Ignite cache.
     * @param basePreprocessor Base preprocessor.
     * @return Preprocessor.
     */
    public Preprocessor<K, V> fit(
        Ignite ignite, IgniteCache<K, V> cache,
        Preprocessor<K, V> basePreprocessor) {
        return fit(
            new CacheBasedDatasetBuilder<>(ignite, cache),
            basePreprocessor
        );
    }

    /**
     * Fits preprocessor.
     *
     * @param envBuilder Learning environment builder.
     * @param ignite Ignite instance.
     * @param cache Ignite cache.
     * @param basePreprocessor Base preprocessor.
     * @return Preprocessor.
     */
    public Preprocessor<K, V> fit(
        LearningEnvironmentBuilder envBuilder,
        Ignite ignite, IgniteCache<K, V> cache,
        Preprocessor<K, V> basePreprocessor) {
        return fit(
            envBuilder,
            new CacheBasedDatasetBuilder<>(ignite, cache),
            basePreprocessor
        );
    }

    /**
     * Fits preprocessor.
     *
     * @param data Data.
     * @param parts Number of partitions.
     * @param basePreprocessor Base preprocessor.
     * @return Preprocessor.
     */
    public Preprocessor<K, V> fit(
        LearningEnvironmentBuilder envBuilder,
        Map<K, V> data,
        int parts,
        Preprocessor<K, V> basePreprocessor) {
        return fit(
            envBuilder,
            new LocalDatasetBuilder<>(data, parts),
            basePreprocessor
        );
    }

    /**
     * Fits preprocessor.
     *
     * @param data Data.
     * @param parts Number of partitions.
     * @param basePreprocessor Base preprocessor.
     * @return Preprocessor.
     */
    public Preprocessor<K, V> fit(
        Map<K, V> data,
        int parts,
        Preprocessor<K, V> basePreprocessor) {
        return fit(
            new LocalDatasetBuilder<>(data, parts),
            basePreprocessor
        );
    }

    /**
     * Sets learning environment for preprocessor trainer.
     * @param environment Learning environment.
     */
    public void setLearningEnvironment(LearningEnvironment environment) {
        this.localEnvironment = environment;
    }

    /**
     * Returns local learning environment if it set or create temp environment
     * with deploy context defined by other preprocessor.
     *
     * @param basePreprocessor Preprocessor.
     * @param <K> Type of key.
     * @param <V> Type of value.
     * @return Learning environment.
     */
    protected <K,V> LearningEnvironment learningEnvironment(Preprocessor<K,V> basePreprocessor) {
        if(localEnvironment != null)
            return localEnvironment;

        LearningEnvironment env = LearningEnvironmentBuilder.defaultBuilder().buildForTrainer();
        env.deployContext().init(basePreprocessor);
        return env;
    }
}
