/*
 * Copyright 2020 GridGain Systems, Inc. and Contributors.
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

package org.apache.ignite.internal.commandline.meta.tasks;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;
import org.apache.ignite.IgniteCheckedException;
import org.apache.ignite.IgniteException;
import org.apache.ignite.cluster.ClusterNode;
import org.apache.ignite.compute.ComputeJobContext;
import org.apache.ignite.compute.ComputeJobResult;
import org.apache.ignite.internal.IgniteEx;
import org.apache.ignite.internal.IgniteInternalFuture;
import org.apache.ignite.internal.binary.BinaryMetadata;
import org.apache.ignite.internal.commandline.cache.CheckIndexInlineSizes;
import org.apache.ignite.internal.processors.affinity.AffinityTopologyVersion;
import org.apache.ignite.internal.processors.cache.binary.CacheObjectBinaryProcessorImpl;
import org.apache.ignite.internal.processors.cache.query.GridCacheQueryManager;
import org.apache.ignite.internal.processors.cache.query.GridCacheSqlMetadata;
import org.apache.ignite.internal.processors.task.GridInternal;
import org.apache.ignite.internal.util.future.GridFinishedFuture;
import org.apache.ignite.internal.util.lang.IgniteClosureX;
import org.apache.ignite.internal.util.typedef.internal.CU;
import org.apache.ignite.internal.util.typedef.internal.U;
import org.apache.ignite.internal.visor.VisorJob;
import org.apache.ignite.internal.visor.VisorMultiNodeTask;
import org.apache.ignite.lang.IgniteCallable;
import org.apache.ignite.lang.IgniteFuture;
import org.apache.ignite.lang.IgniteInClosure;
import org.apache.ignite.lang.IgniteRunnable;
import org.apache.ignite.plugin.security.SecurityPermission;
import org.apache.ignite.resources.IgniteInstanceResource;
import org.apache.ignite.resources.JobContextResource;
import org.jetbrains.annotations.Nullable;

import static org.apache.ignite.internal.GridClosureCallMode.BROADCAST;

/**
 * Task for {@link MetadataRemoveTask} command.
 */
@GridInternal
public class MetadataRemoveTask extends VisorMultiNodeTask<MetadataTypeArgs, MetadataMarshalled, MetadataMarshalled> {
    /**
     *
     */
    private static final long serialVersionUID = 0L;

    /** {@inheritDoc} */
    @Override protected VisorJob<MetadataTypeArgs, MetadataMarshalled> job(MetadataTypeArgs arg) {
        return new MetadataRemoveJob(arg, debug);
    }

    /** {@inheritDoc} */
    @Nullable @Override protected MetadataMarshalled reduce0(List<ComputeJobResult> results) {
        if (results.get(0).getException() != null)
            throw results.get(0).getException();
        else
            return results.get(0).getData();
    }

    /**
     * Job for {@link CheckIndexInlineSizes} command.
     */
    private static class MetadataRemoveJob extends VisorJob<MetadataTypeArgs, MetadataMarshalled> {
        /** */
        private static final long serialVersionUID = 0L;

        /** Auto-inject job context. */
        @JobContextResource
        private transient ComputeJobContext jobCtx;

        /** Metadata future. */
        private transient IgniteFuture<Void> future;

        private transient MetadataMarshalled res;

        /**
         * @param arg Argument.
         * @param debug Debug.
         */
        protected MetadataRemoveJob(@Nullable MetadataTypeArgs arg, boolean debug) {
            super(arg, debug);
        }

        /** {@inheritDoc} */
        @Override protected MetadataMarshalled run(@Nullable MetadataTypeArgs arg) throws IgniteException {
            try {
                if (future == null) {
                    ignite.context().security().authorize(null, SecurityPermission.ADMIN_METADATA_OPS);

                    assert Objects.nonNull(arg);

                    int typeId = arg.typeId(ignite.context());

                    BinaryMetadata meta = ((CacheObjectBinaryProcessorImpl)ignite.context().cacheObjects())
                        .binaryMetadata(typeId);

                    byte[] marshalled = U.marshal(ignite.context(), meta);

                    res = new MetadataMarshalled(marshalled, meta);

                    ignite.context().cacheObjects().removeType(typeId);

                    future = ignite.compute().broadcastAsync(new DropAllThinSessionsJob());;// Drop all connection

                    jobCtx.holdcc();

                    future.listen(new IgniteInClosure<IgniteFuture<Void>>() {
                        @Override public void apply(IgniteFuture<Void> f) {
                            if (f.isDone())
                                jobCtx.callcc();
                        }
                    });

                    return null;
                }

                return res;
            }
            catch (IgniteCheckedException e) {
                throw new IgniteException(e);
            }
        }
    }

    /**
     * Job to drop all thin session.
     */
    @GridInternal
    private static class DropAllThinSessionsJob implements IgniteRunnable {
        /** */
        private static final long serialVersionUID = 0L;

        /** Grid */
        @IgniteInstanceResource
        private IgniteEx ignite;

        /** {@inheritDoc} */
        @Override public void run()
            throws IgniteException {
            ignite.context().security().authorize(null, SecurityPermission.ADMIN_METADATA_OPS);

            ignite.context().sqlListener().closeAllSessions();
        }
    }
}
