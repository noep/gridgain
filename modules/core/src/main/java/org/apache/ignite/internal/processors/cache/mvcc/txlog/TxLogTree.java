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

package org.apache.ignite.internal.processors.cache.mvcc.txlog;

import java.util.concurrent.atomic.AtomicLong;
import org.apache.ignite.IgniteCheckedException;
import org.apache.ignite.internal.pagemem.PageMemory;
import org.apache.ignite.internal.pagemem.wal.IgniteWriteAheadLogManager;
import org.apache.ignite.internal.processors.cache.persistence.tree.BPlusTree;
import org.apache.ignite.internal.processors.cache.persistence.tree.io.BPlusIO;
import org.apache.ignite.internal.processors.cache.persistence.tree.reuse.ReuseList;
import org.apache.ignite.internal.processors.cache.persistence.tree.util.PageLockListener;
import org.apache.ignite.internal.processors.failure.FailureProcessor;

/**
 *
 */
public class TxLogTree extends BPlusTree<TxKey, TxRow> {
    /**
     * @param pageMem Page memory.
     * @param wal Write ahead log manager
     * @param metaPageId Tree metapage id.
     * @param reuseList Reuse list
     * @param failureProcessor Failure processor.
     * @param initNew {@code True} if new tree should be created.
     * @throws IgniteCheckedException If fails.
     */
    public TxLogTree(
        String name,
        PageMemory pageMem,
        IgniteWriteAheadLogManager wal,
        long metaPageId,
        ReuseList reuseList,
        FailureProcessor failureProcessor,
        boolean initNew,
        PageLockListener lockLsnr
    ) throws IgniteCheckedException {
        super(
            TxLog.TX_LOG_CACHE_NAME,
            TxLog.TX_LOG_CACHE_ID,
            pageMem,
            wal,
            new AtomicLong(),
            metaPageId,
            reuseList,
            TxLogInnerIO.VERSIONS,
            TxLogLeafIO.VERSIONS,
            failureProcessor,
            lockLsnr
        );

        initTree(initNew);
    }

    /** {@inheritDoc} */
    @Override protected int compare(BPlusIO<TxKey> io, long pageAddr, int idx, TxKey row) {
        return ((TxLogIO)io).compare(pageAddr, io.offset(idx), row);
    }

    /** {@inheritDoc} */
    @Override public TxRow getRow(BPlusIO<TxKey> io, long pageAddr,
                                  int idx, Object ignored) throws IgniteCheckedException {
        return (TxRow) io.getLookupRow(this, pageAddr, idx);
    }
}
