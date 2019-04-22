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

package org.apache.ignite.internal.util.nio.ssl;

import java.nio.ByteBuffer;
import javax.net.ssl.SSLEngine;

/**
 *
 */
public class GridSslMeta {
    /** GridNioSslHandler. */
    private GridNioSslHandler hnd;

    /** Data already decoded by blocking ssl handler. */
    private ByteBuffer decodedBuf;

    /** Data read but not decoded by blocking ssl handler. */
    private ByteBuffer encodedBuf;

    /** Ssl engine. */
    private SSLEngine sslEngine;

    /**
     *
     */
    public SSLEngine sslEngine() {
        return sslEngine;
    }

    /**
     * @param sslEngine Ssl engine.
     */
    public void sslEngine(SSLEngine sslEngine) {
        this.sslEngine = sslEngine;
    }

    /**
     *
     */
    public GridNioSslHandler handler() {
        return hnd;
    }

    /**
     * @param hnd Handler.
     */
    public void handler(GridNioSslHandler hnd) {
        this.hnd = hnd;
    }

    /**
     *
     */
    public ByteBuffer decodedBuffer() {
        return decodedBuf;
    }

    /**
     * @param decodedBuf Decoded buffer.
     */
    public void decodedBuffer(ByteBuffer decodedBuf) {
        this.decodedBuf = decodedBuf;
    }

    /**
     *
     */
    public ByteBuffer encodedBuffer() {
        return encodedBuf;
    }

    /**
     * @param encodedBuf Encoded buffer.
     */
    public void encodedBuffer(ByteBuffer encodedBuf) {
        this.encodedBuf = encodedBuf;
    }
}
