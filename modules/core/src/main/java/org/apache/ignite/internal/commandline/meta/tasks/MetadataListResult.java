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

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.Collection;
import org.apache.ignite.internal.binary.BinaryMetadata;
import org.apache.ignite.internal.dto.IgniteDataTransferObject;
import org.apache.ignite.internal.processors.task.GridInternal;
import org.apache.ignite.internal.util.typedef.internal.U;

/**
 * Represents information about cluster metadata.
 */
@GridInternal
public class MetadataListResult extends IgniteDataTransferObject {
    /** */
    private static final long serialVersionUID = 0L;

    /** Cluster metadata. */
    private Collection<BinaryMetadata> meta = new ArrayList<>();

    /**
     * Constructor for optimized marshaller.
     */
    public MetadataListResult() {
        // No-op.
    }

    /**
     * @param meta Meta.
     */
    public MetadataListResult(Collection<BinaryMetadata> meta) {
        this.meta = meta;
    }

    /** {@inheritDoc} */
    @Override protected void writeExternalData(ObjectOutput out) throws IOException {
        U.writeCollection(out, meta);
    }

    /** {@inheritDoc} */
    @Override protected void readExternalData(
        byte protoVer,
        ObjectInput in
    ) throws IOException, ClassNotFoundException {
        meta = U.readCollection(in);
    }

    /**
     * @return Cluster binary metadata.
     */
    public Collection<BinaryMetadata> metadata() {
        return meta;
    }
}