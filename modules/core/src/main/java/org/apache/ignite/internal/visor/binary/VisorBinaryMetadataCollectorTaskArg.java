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

package org.apache.ignite.internal.visor.binary;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import org.apache.ignite.internal.util.typedef.internal.S;
import org.apache.ignite.internal.visor.VisorDataTransferObject;

/**
 * Arguments for {@link VisorBinaryMetadataCollectorTask}.
 */
public class VisorBinaryMetadataCollectorTaskArg extends VisorDataTransferObject {
    /** */
    private static final long serialVersionUID = 0L;

    /** Time data was collected last time. */
    private long lastUpdate;

    /**
     * Default constructor.
     */
    public VisorBinaryMetadataCollectorTaskArg() {
        // No-op.
    }

    /**
     * @param lastUpdate Time data was collected last time.
     */
    public VisorBinaryMetadataCollectorTaskArg(long lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    /**
     * @return Time data was collected last time.
     */
    public long getMessage() {
        return lastUpdate;
    }

    /** {@inheritDoc} */
    @Override protected void writeExternalData(ObjectOutput out) throws IOException {
        out.writeLong(lastUpdate);
    }

    /** {@inheritDoc} */
    @Override protected void readExternalData(byte protoVer, ObjectInput in) throws IOException, ClassNotFoundException {
        lastUpdate = in.readLong();
    }

    /** {@inheritDoc} */
    @Override public String toString() {
        return S.toString(VisorBinaryMetadataCollectorTaskArg.class, this);
    }
}
