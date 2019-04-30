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

package org.apache.ignite.internal.processors.cache;

import java.io.Serializable;
import org.apache.ignite.cache.query.annotations.QuerySqlField;
import org.apache.ignite.internal.util.typedef.internal.S;

/**
 * Test value.
 */
public class GridCacheTestValue implements Serializable, Cloneable {
    /** */
    @QuerySqlField(index = true)
    private String val;

    /**
     *
     */
    public GridCacheTestValue() {
        /* No-op. */
    }

    /**
     *
     * @param val Value.
     */
    public GridCacheTestValue(String val) {
        this.val = val;
    }

    /**
     * @return Value.
     */
    public String getValue() {
        return val;
    }

    /**
     *
     * @param val Value.
     */
    public void setValue(String val) {
        this.val = val;
    }

    /** {@inheritDoc} */
    @Override protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    /** {@inheritDoc} */
    @Override public boolean equals(Object o) {
        return this == o || !(o == null || getClass() != o.getClass())
            && val != null && val.equals(((GridCacheTestValue)o).val);
    }

    /** {@inheritDoc} */
    @Override public String toString() {
        return S.toString(GridCacheTestValue.class, this);
    }
}