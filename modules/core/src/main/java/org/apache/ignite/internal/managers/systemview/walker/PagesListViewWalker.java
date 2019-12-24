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

package org.apache.ignite.internal.managers.systemview.walker;

import java.util.Collections;
import java.util.List;
import org.apache.ignite.internal.util.typedef.F;
import org.apache.ignite.spi.systemview.view.PagesListView;
import org.apache.ignite.spi.systemview.view.SystemViewRowAttributeWalker;

/**
 * Generated by {@code org.apache.ignite.codegen.SystemViewRowAttributeWalkerGenerator}.
 * {@link PagesListView} attributes walker.
 * 
 * @see PagesListView
 */
public class PagesListViewWalker implements SystemViewRowAttributeWalker<PagesListView> {
    /** Filter key for attribute "bucketNumber" */
    public static final String BUCKET_NUMBER_FILTER = "bucketNumber";

    /** List of filtrable attributes. */
    private static final List<String> FILTRABLE_ATTRS = Collections.unmodifiableList(F.asList(
        "bucketNumber"
    ));

    /** {@inheritDoc} */
    @Override public List<String> filtrableAttributes() {
        return FILTRABLE_ATTRS;
    }

    /** {@inheritDoc} */
    @Override public void visitAll(AttributeVisitor v) {
        v.accept(0, "name", String.class);
        v.accept(1, "bucketNumber", int.class);
        v.accept(2, "bucketSize", long.class);
        v.accept(3, "stripesCount", int.class);
        v.accept(4, "cachedPagesCount", int.class);
    }

    /** {@inheritDoc} */
    @Override public void visitAll(PagesListView row, AttributeWithValueVisitor v) {
        v.accept(0, "name", String.class, row.name());
        v.acceptInt(1, "bucketNumber", row.bucketNumber());
        v.acceptLong(2, "bucketSize", row.bucketSize());
        v.acceptInt(3, "stripesCount", row.stripesCount());
        v.acceptInt(4, "cachedPagesCount", row.cachedPagesCount());
    }

    /** {@inheritDoc} */
    @Override public int count() {
        return 5;
    }
}
