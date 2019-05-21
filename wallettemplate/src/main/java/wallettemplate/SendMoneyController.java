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

package wallettemplate;

import javafx.beans.value.ObservableValue;
import javafx.scene.layout.HBox;
import org.bitcoinj.core.*;
import org.bitcoinj.wallet.SendRequest;
import org.bitcoinj.wallet.Wallet;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.MoreExecutors;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.bouncycastle.crypto.params.KeyParameter;
import wallettemplate.controls.BitcoinAddressValidator;
import wallettemplate.utils.TextFieldValidator;
import wallettemplate.utils.WTUtils;

import static com.google.common.base.Preconditions.checkState;
import static wallettemplate.utils.GuiUtils.*;

import javax.annotation.Nullable;

public class SendMoneyController {
    public Button sendBtn;
    public Button cancelBtn;
    public Button signBtn;
    public TextField address;
    public Label titleLabel;
    public TextField amountEdit;
    public Label btcLabel;

    public Main.OverlayUI overlayUI;

    private Wallet.SendResult sendResult;
    private KeyParameter aesKey;

    private HardwareSigner hwSigner;

    // Called by FXMLLoader
    public void initialize() {
        Coin balance = Main.bitcoin.wallet().getBalance();
        checkState(!balance.isZero());
        BitcoinAddressValidator addressValidator = new BitcoinAddressValidator(Main.params, address);
        addressValidator.getObservableValidity().addListener(this::addressValidityChanged);
        new TextFieldValidator(amountEdit, text ->
                !WTUtils.didThrow(() -> checkState(Coin.parseCoin(text).compareTo(balance) <= 0)));
        amountEdit.setText(balance.toPlainString());
        address.setPromptText(Address.fromKey(Main.params, new ECKey(), Main.PREFERRED_OUTPUT_SCRIPT_TYPE).toString());
        initSigner();
    }

    public void setSigner(HardwareSigner hardwareSigner) {
        this.hwSigner = hardwareSigner;
        initSigner();
    }

    private void initSigner() {
        if (hwSigner != null) {
            signBtn.setText(hwSigner.getButtonText());
        }
    }

    private void addressValidityChanged(ObservableValue<? extends Boolean> observable, Boolean oldVal, Boolean newVal) {
        // Send is disabled if address is not valid
        sendBtn.setDisable(!newVal);
        // Sign is disabled if address is not valid OR there is no hwSigner
        signBtn.setDisable(!newVal || hwSigner == null);
    }

    public void cancel(ActionEvent event) {
        overlayUI.done();
    }

    public void sign(ActionEvent event) {
        SendRequest req = createSendRequest();
        try {
            req.signInputs = false;
            Main.bitcoin.wallet().completeTx(req);
        } catch (InsufficientMoneyException e) {
            informationalAlert("Could not empty the wallet",
                    "You may have too little money left in the wallet to make a transaction.");
            overlayUI.done();
        }
        hwSigner.displaySigningOverlay(req.tx, this);
    }

    public void send(ActionEvent event) {
        // Address exception cannot happen as we validated it beforehand.
        try {
            SendRequest req = createSendRequest();
            sendResult = Main.bitcoin.wallet().sendCoins(req); // Sign and broadcast
            Futures.addCallback(sendResult.broadcastComplete, new FutureCallback<Transaction>() {
                @Override
                public void onSuccess(@Nullable Transaction result) {
                    checkGuiThread();
                    overlayUI.done();
                }

                @Override
                public void onFailure(Throwable t) {
                    // We died trying to empty the wallet.
                    crashAlert(t);
                }
            }, MoreExecutors.directExecutor());
            sendResult.tx.getConfidence().addEventListener((tx, reason) -> {
                if (reason == TransactionConfidence.Listener.ChangeReason.SEEN_PEERS)
                    updateTitleForBroadcast();
            });
            sendBtn.setDisable(true);
            signBtn.setDisable(true);
            address.setDisable(true);
            ((HBox)amountEdit.getParent()).getChildren().remove(amountEdit);
            ((HBox)btcLabel.getParent()).getChildren().remove(btcLabel);
            updateTitleForBroadcast();
        } catch (InsufficientMoneyException e) {
            informationalAlert("Could not empty the wallet",
                    "You may have too little money left in the wallet to make a transaction.");
            overlayUI.done();
        } catch (ECKey.KeyIsEncryptedException e) {
            askForPasswordAndRetry();
        }
    }

    private SendRequest createSendRequest() {
        Coin amount = Coin.parseCoin(amountEdit.getText());
        Address destination = Address.fromString(Main.params, address.getText());
        SendRequest req;
        if (amount.equals(Main.bitcoin.wallet().getBalance()))
            req = SendRequest.emptyWallet(destination);
        else
            req = SendRequest.to(destination, amount);
        req.aesKey = aesKey;
        return req;
    }


    private void askForPasswordAndRetry() {
        Main.OverlayUI<WalletPasswordController> pwd = Main.instance.overlayUI("wallet_password.fxml");
        final String addressStr = address.getText();
        final String amountStr = amountEdit.getText();
        pwd.controller.aesKeyProperty().addListener((observable, old, cur) -> {
            // We only get here if the user found the right password. If they don't or they cancel, we end up back on
            // the main UI screen. By now the send money screen is history so we must recreate it.
            checkGuiThread();
            Main.OverlayUI<SendMoneyController> screen = Main.instance.overlayUI("send_money.fxml");
            screen.controller.aesKey = cur;
            screen.controller.address.setText(addressStr);
            screen.controller.amountEdit.setText(amountStr);
            screen.controller.send(null);
        });
    }

    private void updateTitleForBroadcast() {
        final int peers = sendResult.tx.getConfidence().numBroadcastPeers();
        titleLabel.setText(String.format("Broadcasting ... seen by %d peers", peers));
    }

}
