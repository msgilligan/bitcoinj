/**
 *
 */
module com.blockchaincommons.airgap {
    requires org.bitcoinj.core;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.core;
    requires jackson.annotations;
    requires slf4j.api;

    exports com.blockchaincommons.airgap;
    exports com.blockchaincommons.airgap.json;
}