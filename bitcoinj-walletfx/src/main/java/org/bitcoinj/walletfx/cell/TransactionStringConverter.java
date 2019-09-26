package org.bitcoinj.walletfx.cell;

import javafx.util.StringConverter;
import org.bitcoinj.core.Coin;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.core.Transaction;
import org.bitcoinj.utils.MonetaryFormat;
import org.bitcoinj.wallet.Wallet;

/**
 * Conversion between String and bitcoinj Transaction type
 */
public class TransactionStringConverter extends StringConverter<Transaction> {
    private Wallet wallet;
    private NetworkParameters networkParameters;

    public TransactionStringConverter(Wallet wallet) {
        this.wallet = wallet;
        this.networkParameters = wallet.getNetworkParameters();
    }

    @Override
    public String toString(Transaction tx) {
        Coin value = tx.getValue(wallet);
        CharSequence valueAsString = MonetaryFormat.BTC.format(value);
        if (value.isPositive()) {
            return "Incoming payment of " + valueAsString;
        } else if (value.isNegative()) {
//            Address address = tx.getOutput(0).getAddressFromP2PKHScript(networkParameters);
//            return "Outbound payment to " + address;
            return "Outbound payment of " + valueAsString;
        }
        return "Payment with id " + tx.getTxId();
    }

    @Override
    public Transaction fromString(String string) {
        // This direction is currently unsupported
        return null;
    }
}
