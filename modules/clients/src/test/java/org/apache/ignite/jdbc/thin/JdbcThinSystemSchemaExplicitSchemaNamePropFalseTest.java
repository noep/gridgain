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

package org.apache.ignite.jdbc.thin;

import org.apache.ignite.IgniteSystemProperties;
import org.apache.ignite.testframework.junits.WithSystemProperty;

/** */
@WithSystemProperty(key = IgniteSystemProperties.IGNITE_SQL_SYSTEM_SCHEMA_NAME_IGNITE, value = "false")
public class JdbcThinSystemSchemaExplicitSchemaNamePropFalseTest extends JdbcThinSystemSchemaAbstractTest {
    /** {@inheritDoc} */
    @Override protected String expectedSysSchemaName() {
        return "SYS";
    }

    /** {@inheritDoc} */
    @Override protected String getUrl() {
        return BASE_URL + "SYS";
    }
}
