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

package org.apache.ignite.internal.util.lang;

import java.io.Externalizable;
import org.apache.ignite.lang.IgniteBiTuple;
import org.jetbrains.annotations.Nullable;

/**
 * Simple extension over {@link org.apache.ignite.lang.IgniteBiTuple} for pair of objects of the same type.
 */
public class IgnitePair<T> extends IgniteBiTuple<T, T> {
    /** */
    private static final long serialVersionUID = 0L;

    /**
     * Empty constructor required by {@link Externalizable}.
     */
    public IgnitePair() {
        // No-op.
    }

    /**
     * Creates pair with given objects.
     *
     * @param t1 First object in pair.
     * @param t2 Second object in pair.
     */
    public IgnitePair(@Nullable T t1, @Nullable T t2) {
        super(t1, t2);
    }

    /** {@inheritDoc} */
    @Override public Object clone() {
        return super.clone();
    }
}