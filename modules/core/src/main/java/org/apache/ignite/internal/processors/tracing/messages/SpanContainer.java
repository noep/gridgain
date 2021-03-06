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

package org.apache.ignite.internal.processors.tracing.messages;

import java.io.Serializable;
import org.apache.ignite.internal.processors.tracing.NoopSpan;
import org.apache.ignite.internal.processors.tracing.Span;
import org.jetbrains.annotations.NotNull;

/**
 * Container to store serialized span context and span that is created from this context.
 */
public class SpanContainer implements Serializable {
    /** Serial version uid. */
    private static final long serialVersionUID = 0L;

    /** Serialized span bytes. */
    private byte[] serializedSpanBytes;

    /** Span. */
    private transient Span span = NoopSpan.INSTANCE;

    /**
     * @return Serialized span.
     */
    public byte[] serializedSpanBytes() {
        return serializedSpanBytes;
    }

    /**
     * @param serializedSpan Serialized span.
     */
    public void serializedSpanBytes(byte[] serializedSpan) {
        this.serializedSpanBytes = serializedSpan.clone();
    }

    /**
     * @return Span span.
     */
    public @NotNull Span span() {
        return span;
    }

    /**
     * @param span Span.
     */
    public void span(@NotNull Span span) {
        this.span = span;
    }

    /**
     * Restores span field to default value after deserialization.
     */
    public Object readResolve() {
        span = NoopSpan.INSTANCE;

        return this;
    }

    /** {@inheritDoc} */
    @Override public String toString() {
        return "SpanContainer{" +
            "serializedSpanBytes=" + serializedSpanBytes +
            ", span=" + span +
            '}';
    }
}
