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

package org.apache.ignite.internal.processors.platform.client.cache;

import org.apache.ignite.configuration.CacheConfiguration;
import org.apache.ignite.internal.binary.BinaryRawWriterEx;
import org.apache.ignite.internal.processors.platform.client.ClientConnectionContext;
import org.apache.ignite.internal.processors.platform.client.ClientProtocolContext;
import org.apache.ignite.internal.processors.platform.client.ClientResponse;

/**
 * Cache configuration response.
 */
public class ClientCacheGetConfigurationResponse extends ClientResponse {
    /** Cache configuration. */
    private final CacheConfiguration cfg;

    /** Client protocol context. */
    private final ClientProtocolContext protocolCtx;

    /**
     * Constructor.
     *
     * @param reqId Request id.
     * @param cfg Cache configuration.
     * @param protocolCtx Client protocol context.
     */
    ClientCacheGetConfigurationResponse(long reqId, CacheConfiguration cfg, ClientProtocolContext protocolCtx) {
        super(reqId);

        assert cfg != null;
        assert protocolCtx != null;

        this.cfg = cfg;
        this.protocolCtx = protocolCtx;
    }

    /** {@inheritDoc} */
    @Override public void encode(ClientConnectionContext ctx, BinaryRawWriterEx writer) {
        super.encode(ctx, writer);

        ClientCacheConfigurationSerializer.write(writer, cfg, protocolCtx);
    }
}
