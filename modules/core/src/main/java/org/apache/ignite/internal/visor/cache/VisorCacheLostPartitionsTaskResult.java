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

package org.apache.ignite.internal.visor.cache;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.List;
import java.util.Map;
import org.apache.ignite.internal.util.typedef.internal.S;
import org.apache.ignite.internal.util.typedef.internal.U;
import org.apache.ignite.internal.visor.VisorDataTransferObject;

/**
 * Result for {@link VisorCacheLostPartitionsTask}.
 */
public class VisorCacheLostPartitionsTaskResult extends VisorDataTransferObject {
    /** */
    private static final long serialVersionUID = 0L;

    /** List of lost partitions by caches. */
    private Map<String, List<Integer>> lostPartitions;

    /**
     * Default constructor.
     */
    public VisorCacheLostPartitionsTaskResult() {
        // No-op.
    }

    /**
     * @param lostPartitions List of lost partitions by caches.
     */
    public VisorCacheLostPartitionsTaskResult(Map<String, List<Integer>> lostPartitions) {
        this.lostPartitions = lostPartitions;
    }

    /**
     * @return List of lost partitions by caches.
     */
    public Map<String, List<Integer>> getLostPartitions() {
        return lostPartitions;
    }

    /** {@inheritDoc} */
    @Override protected void writeExternalData(ObjectOutput out) throws IOException {
        U.writeMap(out, lostPartitions);
    }

    /** {@inheritDoc} */
    @Override protected void readExternalData(byte protoVer, ObjectInput in) throws IOException, ClassNotFoundException {
        lostPartitions = U.readMap(in);
    }

    /** {@inheritDoc} */
    @Override public String toString() {
        return S.toString(VisorCacheLostPartitionsTaskResult.class, this);
    }
}
