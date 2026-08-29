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
package org.bitcoinj.core;

import org.bitcoinj.base.BitcoinNetwork;
import org.bitcoinj.base.Network;
import org.bitcoinj.store.BlockStore;

/**
 * A {@code PeerNetwork} is a live, dynamically-updating view of a Bitcoin P2P network and its current Blockchain.
 * Its logical scope encompasses many key Bitcoin concepts:  the Blockchain, the UTXO set, and the Memory Pool.
 * In typical applications, there will be a single {@code PeerNetwork} instance. A {@code PeerNetwork} instance represents
 * the current state of a particular {@link BitcoinNetwork}, such as {@link BitcoinNetwork#MAINNET}
 * or {@link BitcoinNetwork#SIGNET}.
 * <p>
 * It combines the functionality of several pre-existing <b>bitcoinj</b> classes:
 * <ul>
 *     <li>{@link TxConfidenceTable}</li>
 *     <li>{@link PeerGroup}</li>
 *     <li>{@link AbstractBlockChain}</li>
 *     <li>{@link BlockStore}</li>
 *  </ul>
 *  Existing or traditional <b>bitcoinj</b> applications will use {@link SPVPeerNetwork}, which will
 *  be using {@link TxConfidenceTable}, {@link PeerGroup}, {@link BlockChain}, {@link org.bitcoinj.store.SPVBlockStore}.
 *  See {@link SPVPeerNetwork} for details.
 */
public interface PeerNetwork {
    Network network();
}
