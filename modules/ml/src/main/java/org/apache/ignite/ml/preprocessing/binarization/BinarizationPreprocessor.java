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

package org.apache.ignite.ml.preprocessing.binarization;

import java.util.Collections;
import java.util.List;
import org.apache.ignite.ml.environment.deploy.DeployableObject;
import org.apache.ignite.ml.preprocessing.Preprocessor;
import org.apache.ignite.ml.structures.LabeledVector;

/**
 * Preprocessing function that makes binarization.
 *
 * Feature values greater than the threshold are binarized to 1.0;
 * values equal to or less than the threshold are binarized to 0.0.
 *
 * @param <K> Type of a key in {@code upstream} data.
 * @param <V> Type of a value in {@code upstream} data.
 */
public final class BinarizationPreprocessor<K, V> implements Preprocessor<K, V>, DeployableObject {
    /** */
    private static final long serialVersionUID = 6877811577892621239L;

    /** Threshold. */
    private final double threshold;

    /** Base preprocessor. */
    private final Preprocessor<K, V> basePreprocessor;

    /**
     * Constructs a new instance of Binarization preprocessor.
     *
     * @param threshold Threshold value.
     * @param basePreprocessor Base preprocessor.
     */
    public BinarizationPreprocessor(double threshold, Preprocessor<K, V> basePreprocessor) {
        this.threshold = threshold;
        this.basePreprocessor = basePreprocessor;
    }

    /**
     * Applies this preprocessor.
     *
     * @param k Key.
     * @param v Value.
     * @return Preprocessed row.
     */
    @Override public LabeledVector apply(K k, V v) {
        LabeledVector res = basePreprocessor.apply(k, v);

        for (int i = 0; i < res.size(); i++) {
            if (res.get(i) > threshold) res.set(i, 1.0);
            else res.set(i, 0.0);
        }

        return res;
    }

    /** Get the threshold parameter. */
    public double getThreshold() {
        return threshold;
    }

    /** {@inheritDoc} */
    @Override public List<Object> getDependencies() {
        return Collections.singletonList(basePreprocessor);
    }
}
