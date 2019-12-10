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
package org.apache.ignite.internal.processors.query.h2;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.ignite.IgniteCheckedException;
import org.apache.ignite.IgniteException;
import org.apache.ignite.internal.GridKernalContext;
import org.apache.ignite.internal.metric.IoStatisticsHolderIndex;
import org.apache.ignite.internal.processors.cache.GridCacheContext;
import org.apache.ignite.internal.processors.cache.persistence.metastorage.pendingtask.ContinuousTask;
import org.apache.ignite.internal.processors.cache.persistence.tree.BPlusTree;
import org.apache.ignite.internal.processors.query.h2.database.H2Tree;
import org.apache.ignite.internal.processors.query.h2.database.H2TreeIndex;
import org.apache.ignite.internal.util.typedef.internal.CU;
import org.apache.ignite.internal.util.typedef.internal.S;

import static org.apache.ignite.internal.metric.IoStatisticsType.SORTED_INDEX;

/**
 * Tasks that cleans up index tree.
 */
public class ContinuousCleanupIndexTreeTask implements ContinuousTask {
    /** */
    private static final long serialVersionUID = 0L;

    /** */
    private List<Long> rootPages;

    /** */
    private String cacheGrpName;

    /** */
    private String cacheName;

    /** */
    private String schemaName;

    /** */
    private String idxName;

    /** */
    public ContinuousCleanupIndexTreeTask(
        List<Long> rootPages,
        String cacheGrpName,
        String cacheName,
        String schemaName,
        String idxName
    ) {
        this.rootPages = rootPages;
        this.cacheGrpName = cacheGrpName;
        this.cacheName = cacheName;
        this.schemaName = schemaName;
        this.idxName = idxName;
    }

    /** {@inheritDoc} */
    @Override public String shortName() {
        return "DROP_SQL_INDEX-" + schemaName + "." + idxName;
    }

    /** {@inheritDoc} */
    @Override public void execute(GridKernalContext ctx) {
        GridCacheContext cctx = ctx.cache().context().cacheContext(CU.cacheId(cacheName));

        List<BPlusTree> trees = new LinkedList<>();

        IoStatisticsHolderIndex stats = new IoStatisticsHolderIndex(
            SORTED_INDEX,
            cctx.name(),
            idxName,
            cctx.kernalContext().metric(),
            cctx.group().statisticsHolderData()
        );

        for (int i = 0; i < rootPages.size(); i++) {
            Long rootPage = rootPages.get(i);

            assert rootPage != null;

            // Below we create a fake index tree using it's root page, stubbing some parameters,
            // because we just going to free memory pages that are occupied by tree structure.
            try {
                String treeName = "deletedTree_" + i + "_" + shortName();

                H2TreeIndex.IndexColumnsInfo unwrappedColsInfo =
                    new H2TreeIndex.IndexColumnsInfo(H2Utils.EMPTY_COLUMNS, new ArrayList<>(), 0, 0);

                H2TreeIndex.IndexColumnsInfo wrappedColsInfo =
                    new H2TreeIndex.IndexColumnsInfo(H2Utils.EMPTY_COLUMNS, new ArrayList<>(), 0, 0);

                BPlusTree tree = new H2Tree(
                    cctx,
                    null,
                    treeName,
                    idxName,
                    cacheName,
                    null,
                    cctx.offheap().reuseListForIndex(treeName),
                    CU.cacheGroupId(cacheName, cacheGrpName),
                    cacheGrpName,
                    cctx.dataRegion().pageMemory(),
                    ctx.cache().context().wal(),
                    cctx.offheap().globalRemoveId(),
                    rootPage,
                    false,
                    unwrappedColsInfo,
                    wrappedColsInfo,
                    new AtomicInteger(0),
                    false,
                    false,
                    false,
                    null,
                    ctx.failure(),
                    null,
                    stats
                );

                trees.add(tree);
            }
            catch (IgniteCheckedException e) {
                throw new IgniteException(e);
            }
        }

        ctx.cache().context().database().checkpointReadLock();

        try {
            for (int i = 0; i < trees.size(); i++) {
                BPlusTree tree = trees.get(i);

                try {
                    tree.destroy();
                }
                catch (IgniteCheckedException e) {
                    throw new IgniteException(e);
                }
            }
        }
        finally {
            ctx.cache().context().database().checkpointReadUnlock();
        }
    }

    /** {@inheritDoc} */
    @Override public String toString() {
        return S.toString(ContinuousCleanupIndexTreeTask.class, this);
    }
}
