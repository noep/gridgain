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

package org.apache.ignite.internal.processors.tracing;

import org.apache.ignite.spi.tracing.TracingConfigurationManager;
import org.apache.ignite.internal.processors.tracing.messages.TraceableMessagesHandler;
import org.jetbrains.annotations.NotNull;

/**
 * Tracing sub-system interface.
 */
public interface Tracing extends SpanManager {
    /**
     * @return Helper to handle traceable messages.
     */
    public TraceableMessagesHandler messages();

    /**
     * Returns the {@link TracingConfigurationManager} instance that allows to
     * <ul>
     *     <li>Configure tracing parameters such as sampling rate for the specific tracing coordinates
     *          such as scope and label.</li>
     *     <li>Retrieve the most specific tracing parameters for the specified tracing coordinates (scope and label)</li>
     *     <li>Restore the tracing parameters for the specified tracing coordinates to the default.</li>
     *     <li>List all pairs of tracing configuration coordinates and tracing configuration parameters.</li>
     * </ul>
     * @return {@link TracingConfigurationManager} instance.
     */
    public @NotNull TracingConfigurationManager configuration();
}
