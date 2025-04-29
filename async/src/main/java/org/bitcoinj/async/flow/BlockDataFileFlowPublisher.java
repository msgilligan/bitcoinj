/*
 * Copyright by the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.bitcoinj.async.flow;

import org.bitcoinj.utils.BlockFileLoader;

import java.nio.ByteBuffer;
import java.util.concurrent.Executor;
import java.util.concurrent.SubmissionPublisher;

/**
 * A {@link SubmissionPublisher} that publishes a stream of blocks as in {@link ByteBuffer} format. The blocks are read from
 * <b>Bitcoin Core</b> block datafiles using a {@link BlockFileLoader}.
 */
public class BlockDataFileFlowPublisher extends SubmissionPublisher<ByteBuffer> implements BlockPublisher<ByteBuffer> {

    /**
     * Constructor that creates and configures a source of blocks in {@link ByteBuffer}
     * @param executor An executor that the publishing thread(s) will run on
     * @param maxBufferCapacity the maximum capacity for each subscriber's buffer (see superclass for details)
     * @param blockFileLoader an instance to read Bitcoin Core block files from a directory or file list
     */
    public BlockDataFileFlowPublisher(Executor executor, int maxBufferCapacity, BlockFileLoader blockFileLoader) {
        super(executor, maxBufferCapacity);
        executor.execute(() -> {
            blockFileLoader.streamBuffers().forEach(this::submit);
            close();
        });
    }
}
