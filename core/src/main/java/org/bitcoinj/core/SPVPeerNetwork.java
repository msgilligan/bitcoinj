package org.bitcoinj.core;

import org.bitcoinj.base.Network;
import org.bitcoinj.store.BlockStoreException;
import org.jspecify.annotations.NonNull;

import java.io.File;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A classic <b>bitcoinj</b> SPV implementation of a {@code PeerNetwork}.
 * {@link TxConfidenceTable}, {@link PeerGroup}, {@link BlockChain}, {@link org.bitcoinj.store.SPVBlockStore}
 */
public class SPVPeerNetwork implements /* PeerNetwork, */ TransactionBroadcaster {
    private final Network network;
    private final TxConfidenceTable txConfidenceTable;
    private final @NonNull PeerGroup peerGroup;
    private static final Map<Network, SPVPeerNetwork> networks = new ConcurrentHashMap<Network, SPVPeerNetwork>();

//    static PeerNetwork getPeerNetwork(Network network) {
//        return networks.get(network);
//    }

    static void registerPeerNetwork(Network network, SPVPeerNetwork peerNetwork) {
        networks.put(network, peerNetwork);
    }

    static SPVPeerNetwork createPeerNetwork(Network network, File directory, String storeFileName) throws BlockStoreException {
        // Create SPVBlockStore
        // Create Blockchain
        // Create PeerGroup
        // Register
        return null;
    }

    static SPVPeerNetwork fromPeerGroup(PeerGroup peerGroup) {
        return null;
    }

    static SPVPeerNetwork fromBlockChain(BlockChain blockChain) {
        return null;
    }

    public SPVPeerNetwork(Network network, TxConfidenceTable txConfidenceTable, PeerGroup peerGroup) {
        this.network = network;
        this.txConfidenceTable = txConfidenceTable;
        this.peerGroup = peerGroup;
    }
    
    //@Override
    public Network network() {
        return network;
    }

    public TxConfidenceTable txConfidenceTable() {
        return txConfidenceTable;
    }

    //@Override
    public TransactionBroadcast broadcastTransaction(Transaction tx) {
        return peerGroup.broadcastTransaction(tx);
    }
}
