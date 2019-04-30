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

package org.apache.ignite.tests.p2p;

import java.util.UUID;
import org.apache.ignite.cluster.ClusterNode;
import org.apache.ignite.internal.util.typedef.internal.S;
import org.apache.ignite.lang.IgnitePredicate;

/**
 * This node filter excludes the node with the given UUID
 * from the topology.
 */
public class ExcludeNodeFilter implements IgnitePredicate<ClusterNode> {
    /** Node ID to exclude. */
    private final UUID excludeId;

    /**
     * @param excludeId Excluded node UUID.
     */
    public ExcludeNodeFilter(UUID excludeId) {
        assert excludeId != null;

        this.excludeId = excludeId;
    }

    /** {@inheritDoc} */
    @Override public boolean apply(ClusterNode e) {
        return !excludeId.equals(e.id());
    }

    /** {@inheritDoc} */
    @Override public String toString() {
        return S.toString(ExcludeNodeFilter.class, this);
    }
}