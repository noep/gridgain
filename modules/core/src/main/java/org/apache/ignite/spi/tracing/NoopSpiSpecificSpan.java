/*
 * Copyright 2020 GridGain Systems, Inc. and Contributors.
 *
 * Licensed under the GridGain Community Edition License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.gridgain.com/products/software/community-edition/gridgain-community-edition-license
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.ignite.spi.tracing;

import java.util.Map;

/**
 * Noop and null-safe implementation of {@link SpiSpecificSpan}.
 */
public class NoopSpiSpecificSpan implements SpiSpecificSpan {
    /** Instance. */
    public static final SpiSpecificSpan INSTANCE = new NoopSpiSpecificSpan();

    /**
     * Constructor.
     */
    private NoopSpiSpecificSpan(){

    }

    /** {@inheritDoc} */
    @Override public SpiSpecificSpan addTag(String tagName, String tagVal) {
        return this;
    }

    /** {@inheritDoc} */
    @Override public SpiSpecificSpan addTag(String tagName, long tagVal) {
        return this;
    }

    /** {@inheritDoc} */
    @Override public SpiSpecificSpan addLog(String logDesc) {
        return this;
    }

    /** {@inheritDoc} */
    @Override public SpiSpecificSpan addLog(String logDesc, Map<String, String> attrs) {
        return this;
    }

    /** {@inheritDoc} */
    @Override public SpiSpecificSpan setStatus(SpanStatus spanStatus) {
        return this;
    }

    /** {@inheritDoc} */
    @Override public SpiSpecificSpan end() {
        return this;
    }

    /** {@inheritDoc} */
    @Override public boolean isEnded() {
        return true;
    }
}
