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

package org.apache.ignite;

/**
 * Exception is thrown when timeout of some {@link IgniteDataStreamer} operations occurs.
 */
public class IgniteDataStreamerTimeoutException extends IgniteException {
    /** */
    private static final long serialVersionUID = 0L;

    /**
     * Creates new timeout exception with given error message.
     *
     * @param msg Error message.
     */
    public IgniteDataStreamerTimeoutException(String msg) {
        super(msg);
    }

    /**
     * Creates new timeout exception with given error message and optional nested exception.
     *
     * @param msg Error message.
     * @param cause Optional nested exception (can be {@code null}).
     */
    public IgniteDataStreamerTimeoutException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
