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

package org.apache.ignite.internal.processors.platform;

import org.apache.ignite.internal.util.typedef.internal.S;
import org.apache.ignite.platform.PlatformJavaObjectFactory;

/**
 * Singleton factory.
 */
public class PlatformJavaObjectSingletonFactory<T> implements PlatformJavaObjectFactory<T> {
    /** Instance. */
    private final T instance;

    /**
     * Constructor.
     *
     * @param instance Instance.
     */
    public PlatformJavaObjectSingletonFactory(T instance) {
        this.instance = instance;
    }

    /** {@inheritDoc} */
    @Override public T create() {
        return instance;
    }

    /** {@inheritDoc} */
    @Override public String toString() {
        return S.toString(PlatformJavaObjectSingletonFactory.class, this);
    }
}
