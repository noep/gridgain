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

package org.apache.ignite.testsuites;

import java.util.ArrayList;
import java.util.List;
import org.apache.ignite.internal.processors.cache.persistence.db.wal.HistoricalRebalanceWithWalPageCompressionTest;
import org.apache.ignite.internal.processors.cache.persistence.db.wal.IgnitePdsCheckpointSimulationWithRealCpDisabledAndWalCompressionTest;
import org.apache.ignite.internal.processors.cache.persistence.db.wal.WalCompactionAndPageCompressionTest;
import org.apache.ignite.internal.processors.cache.persistence.db.wal.WalRecoveryWithPageCompressionTest;
import org.apache.ignite.internal.processors.compress.WalPageCompressionIntegrationTest;
import org.apache.ignite.testframework.junits.DynamicSuite;
import org.junit.runner.RunWith;

import static org.apache.ignite.IgniteSystemProperties.IGNITE_DEFAULT_DATA_STORAGE_PAGE_SIZE;
import static org.apache.ignite.IgniteSystemProperties.IGNITE_DEFAULT_DISK_PAGE_COMPRESSION;
import static org.apache.ignite.configuration.DiskPageCompression.ZSTD;

/** */
@RunWith(DynamicSuite.class)
public class IgnitePdsCompressionTestSuite {
    /**
     * @return Suite.
     */
    public static List<Class<?>> suite() {
        List<Class<?>> suite = new ArrayList<>();
        // WAL page records compression.
        suite.add(WalPageCompressionIntegrationTest.class);
        suite.add(HistoricalRebalanceWithWalPageCompressionTest.class);
        suite.add(WalRecoveryWithPageCompressionTest.class);
        suite.add(WalCompactionAndPageCompressionTest.class);
        suite.add(IgnitePdsCheckpointSimulationWithRealCpDisabledAndWalCompressionTest.class);
        suite.add(HistoricalRebalanceWithWalPageCompressionTest.class);

        enableCompressionByDefault();
        IgnitePdsTestSuite.addRealPageStoreTests(suite, null);

        return suite;
    }

    /**
     */
    static void enableCompressionByDefault() {
        System.setProperty(IGNITE_DEFAULT_DISK_PAGE_COMPRESSION, ZSTD.name());
        System.setProperty(IGNITE_DEFAULT_DATA_STORAGE_PAGE_SIZE, String.valueOf(8 * 1024));
    }
}