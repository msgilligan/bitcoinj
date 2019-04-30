/**
 *
 */
module com.blockchaincommons.airgap.fx {
    requires java.desktop;

    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.swing;


    requires webcam.capture;
    requires slf4j.api;

    opens com.blockchaincommons.airgap.fx.demoapp to javafx.fxml;
    exports com.blockchaincommons.airgap.fx.demoapp;
}