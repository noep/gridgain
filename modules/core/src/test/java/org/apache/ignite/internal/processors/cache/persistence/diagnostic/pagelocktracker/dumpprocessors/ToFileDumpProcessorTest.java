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

package org.apache.ignite.internal.processors.cache.persistence.diagnostic.pagelocktracker.dumpprocessors;

import java.io.File;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.StandardOpenOption;
import junit.framework.TestCase;
import org.apache.ignite.internal.processors.cache.persistence.diagnostic.pagelocktracker.PageLockDump;
import org.apache.ignite.internal.processors.cache.persistence.diagnostic.pagelocktracker.LockTrackerFactory;
import org.apache.ignite.internal.processors.cache.persistence.diagnostic.pagelocktracker.PageLockTracker;
import org.apache.ignite.internal.util.typedef.internal.U;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static java.nio.file.Paths.get;

/**
 *
 */
public class ToFileDumpProcessorTest extends TestCase {
    /** */
    private File file;

    @Before
    public void beforeTest() {
        cleanFile();
    }

    @After
    public void afterTest() {
        //  cleanFile();
    }

    /**
     * Clean files.
     */
    public void cleanFile() {
        if (file != null && file.exists())
            file.delete();
    }

    /** */
    @Test
    public void testToFileDump() throws Exception {
        String igHome = U.defaultWorkDirectory();

        System.out.println("IGNITE_HOME:" + igHome);

        PageLockTracker pageLockTracker = LockTrackerFactory.create("test");

        pageLockTracker.onBeforeReadLock(1, 2, 3);
        pageLockTracker.onReadLock(1, 2, 3, 4);

        PageLockDump pageLockDump = pageLockTracker.dump();

        Assert.assertNotNull(pageLockDump);

        String expDumpStr = ToStringDumpProcessor.toStringDump(pageLockDump);

        String filePath = ToFileDumpProcessor.toFileDump(pageLockDump, file = new File(igHome), "test");

        System.out.println("Dump saved:" + filePath);

        boolean found = false;

        for (File file : file.listFiles()) {
            if (file.getAbsolutePath().equals(filePath)) {
                found = true;

                break;
            }
        }

        Assert.assertTrue(found);

        String actDumpStr;

        try (FileChannel ch = FileChannel.open(get(filePath), StandardOpenOption.READ)) {
            long size = ch.size();

            ByteBuffer buf = ByteBuffer.allocate((int)size);

            while (buf.position() != buf.capacity())
                ch.read(buf);

            actDumpStr = new String(buf.array());
        }

        Assert.assertEquals(expDumpStr, actDumpStr);
    }
}