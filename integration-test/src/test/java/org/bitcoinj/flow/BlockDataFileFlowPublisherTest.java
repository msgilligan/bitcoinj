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

package org.bitcoinj.flow;

import org.bitcoinj.async.flow.BlockSubscriber;
import org.bitcoinj.base.BitcoinNetwork;
import org.bitcoinj.base.Network;
import org.bitcoinj.core.Context;
import org.bitcoinj.async.flow.BlockDataFileFlowPublisher;
import org.bitcoinj.store.BlockStoreException;
import org.bitcoinj.utils.BlockFileLoader;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Work-in-progress tests of {@link BlockDataFileFlowPublisher} and {@link BlockSubscriber}
 */
@Ignore
public class BlockDataFileFlowPublisherTest {
    @Before
    public void setUp() {
        Context.propagate(new Context());
    }

    @Test
    public void readEntireBitcoindBlockchain() {
        BlockFileLoader loader = new BlockFileLoader(BitcoinNetwork.MAINNET, BlockFileLoader.getReferenceClientBlockFileList());
        BlockSubscriber subscriber = new BlockSubscriber();

        try (var publisher = new BlockDataFileFlowPublisher(Executors.newFixedThreadPool(8), 1024, loader)) {
            publisher.subscribe(subscriber);
            subscriber.totalCount().join();
        }
        int blockCount = subscriber.currentCount().get();
        System.out.println("Final block count: " + blockCount);
        assertTrue(blockCount > 1);
    }

    @Test
    @Ignore("Implementation not finished, currently the same as readEntireBitcoindBlockchain() test")
    public void readEntireBitcoindBlockchainIntoBlockStore() throws BlockStoreException {
        Network network = BitcoinNetwork.MAINNET;
        BlockFileLoader loader = new BlockFileLoader(network, BlockFileLoader.getReferenceClientBlockFileList());
//        NetworkParameters params = NetworkParameters.of(network);
//        BlockStore store = new MemoryBlockStore(params.getGenesisBlock());
//        AbstractBlockChain chain = new BlockChain(network, store);

        BlockSubscriber subscriber = new BlockSubscriber();

        try (var publisher = new BlockDataFileFlowPublisher(Executors.newFixedThreadPool(8), 1024, loader)) {
            publisher.subscribe(subscriber);
            subscriber.totalCount().join();
        }
        int blockCount = subscriber.currentCount().get();
        System.out.println("Final block count: " + blockCount);
        assertTrue(blockCount > 1);
    }

}
