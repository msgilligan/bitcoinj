/**
 * Module Info for Wallet Template
 * Note: relies upon several filename-based Automatic Modules and the
 * BadAss JLink Gradle Plugin for correct operation
 *
 * if you are using IntelliJ and it isn't finding the org.bitcoinj.core, see:
 * https://blog.jetbrains.com/idea/2017/03/support-for-java-9-modules-in-intellij-idea-2017-1/
 * I made it work by adding a filename-based JAR dependency on the output JAR of the 'core' module.
 */
module wallettemplate {
        requires java.logging;
        requires java.desktop;

        requires javafx.controls;
        requires javafx.fxml;

        requires slf4j.api;

        requires org.bitcoinj.core;     // Automatic module
        requires com.blockchaincommons.airgap;
        requires com.blockchaincommons.airgap.fx;

        requires org.bouncycastle.provider;
        requires com.google.common;

        requires protobuf.java;
        requires com.google.zxing;                  // ZXing automatic module name
        requires com.google.zxing.javase;
        requires fontawesomefx;         // Filename-based automatic module name
        requires jsr305;
        requires webcam.capture;

        opens wallettemplate to javafx.fxml;
        opens wallettemplate.controls to javafx.fxml;
        opens wallettemplate.utils to javafx.fxml;
        exports wallettemplate;
        exports wallettemplate.controls;
        exports wallettemplate.utils;

}
