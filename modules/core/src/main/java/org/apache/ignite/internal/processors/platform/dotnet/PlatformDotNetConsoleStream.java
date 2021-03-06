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

package org.apache.ignite.internal.processors.platform.dotnet;

import org.apache.ignite.internal.processors.platform.callback.PlatformCallbackGateway;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Stream that writes to the .NET console.
 */
public class PlatformDotNetConsoleStream extends OutputStream {
    /** Indicates whether this is an error stream. */
    private final boolean isErr;

    /**
     * Ctor.
     *
     * @param err Error stream flag.
     */
    public PlatformDotNetConsoleStream(boolean err) {
        isErr = err;
    }

    /** {@inheritDoc} */
    @Override public void write(byte[] b, int off, int len) throws IOException {
        String s = new String(b, off, len);

        PlatformCallbackGateway.consoleWrite(s, isErr);
    }

    /** {@inheritDoc} */
    @Override public void write(int b) throws IOException {
        String s = String.valueOf((char) b);

        PlatformCallbackGateway.consoleWrite(s, isErr);
    }
}
