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

package org.apache.ignite.internal.commandline.meta.subcommands;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.logging.Logger;
import org.apache.ignite.internal.binary.BinaryMetadata;
import org.apache.ignite.internal.client.GridClient;
import org.apache.ignite.internal.client.GridClientCompute;
import org.apache.ignite.internal.client.GridClientConfiguration;
import org.apache.ignite.internal.client.GridClientDisconnectedException;
import org.apache.ignite.internal.client.GridClientNode;
import org.apache.ignite.internal.commandline.CommandArgIterator;
import org.apache.ignite.internal.commandline.meta.MetadataSubCommandsList;
import org.apache.ignite.internal.commandline.meta.tasks.MetadataMarshalled;
import org.apache.ignite.internal.commandline.meta.tasks.MetadataUpdateTask;
import org.apache.ignite.internal.util.typedef.F;
import org.apache.ignite.internal.visor.VisorTaskArgument;

/** */
public class MetadataUpdateCommand
    extends MetadataAbstractSubCommand<MetadataMarshalled, MetadataMarshalled>
{
    /** Output file name. */
    private static String OPT_IN_FILE_NAME = "--in";

    /** {@inheritDoc} */
    @Override protected String taskName() {
        return MetadataUpdateTask.class.getName();
    }

    /** {@inheritDoc} */
    @Override public MetadataMarshalled parseArguments0(CommandArgIterator argIter) {
        String opt = argIter.nextArg("--in");

        if (!OPT_IN_FILE_NAME.equalsIgnoreCase(opt))
            throw new IllegalArgumentException("");

        Path inFile = FS.getPath(argIter.nextArg("input file name"));

        try (InputStream is = Files.newInputStream(inFile)) {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            int nRead;

            byte[] data = new byte[1024];

            while ((nRead = is.read(data, 0, data.length)) != -1)
                buffer.write(data, 0, nRead);

            buffer.flush();

            return new MetadataMarshalled(buffer.toByteArray(), null);
        }
        catch (IOException e) {
            throw new IllegalArgumentException("Cannot read metadata from " + inFile, e);
        }
    }

    /** {@inheritDoc} */
    @Override protected MetadataMarshalled execute0(
        GridClientConfiguration clientCfg,
        GridClient client
    ) throws Exception {
        GridClientCompute compute = client.compute();

        Collection<GridClientNode> connectableNodes = compute.nodes(GridClientNode::connectable);

        if (F.isEmpty(connectableNodes))
            throw new GridClientDisconnectedException("Connectable nodes not found", null);

        GridClientNode node = connectableNodes.stream()
            .findAny().orElse(null);

        if (node == null)
            node = compute.balancer().balancedNode(connectableNodes);

        return compute.projection(node).execute(
            taskName(),
            new VisorTaskArgument<>(node.nodeId(), arg(), false)
        );
    }

    /** {@inheritDoc} */
    @Override protected void printResult(MetadataMarshalled res, Logger log) {
        if (res.metadata() == null) {
            log.info("Type not found");

            return;
        }

        BinaryMetadata m = res.metadata();


    }

    /** {@inheritDoc} */
    @Override public String name() {
        return MetadataSubCommandsList.UPDATE.text();
    }
}