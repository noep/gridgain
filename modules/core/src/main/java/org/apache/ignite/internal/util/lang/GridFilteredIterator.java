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

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Filtered iterator.
 */
public abstract class GridFilteredIterator<T> implements Iterator<T> {
    /** */
    protected final Iterator<? extends T> it;

    /** */
    private boolean hasNext;

    /** */
    private T next;

    /**
     * @param it Iterator.
     */
    protected GridFilteredIterator(Iterator<? extends T> it) {
        assert it != null;

        this.it = it;
    }

    /**
     * @param t The object.
     * @return {@code true} If the object is accepted by the filter.
     */
    protected abstract boolean accept(T t);

    /** {@inheritDoc} */
    @Override public boolean hasNext() {
        if (hasNext)
            return true;

        while (it.hasNext()) {
            T t = it.next();

            if (accept(t)) {
                next = t;
                hasNext = true;

                return true;
            }
        }

        return false;
    }

    /** {@inheritDoc} */
    @Override public T next() {
        if (!hasNext())
            throw new NoSuchElementException();

        T res = next;

        next = null;
        hasNext = false;

        return res;
    }


    /** {@inheritDoc} */
    @Override public void remove() {
        throw new UnsupportedOperationException();
    }
}