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

package org.apache.ignite.internal.processors.query;

import java.io.Externalizable;

/**
 * Query field descriptor. This descriptor is used to provide metadata
 * about fields returned in query result.
 */
public interface GridQueryFieldMetadata extends Externalizable {
    /**
     * Gets schema name.
     *
     * @return Schema name.
     */
    public String schemaName();

    /**
     * Gets name of type to which this field belongs.
     *
     * @return Type name.
     */
    public String typeName();

    /**
     * Gets field name.
     *
     * @return Field name.
     */
    public String fieldName();

    /**
     * Gets field type name.
     *
     * @return Field type name.
     */
    public String fieldTypeName();

    /**
     * Gets field precision.
     *
     * @return Field precision.
     */
    public int precision();

    /**
     * Gets field scale.
     *
     * @return Field scale.
     */
    public int scale();
}
