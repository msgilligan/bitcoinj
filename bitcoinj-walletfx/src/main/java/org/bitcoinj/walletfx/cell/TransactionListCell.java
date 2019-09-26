package org.bitcoinj.walletfx.cell;

import javafx.scene.control.cell.TextFieldListCell;
import org.bitcoinj.core.Transaction;

/**
 * ListCell for Bitcoin Transaction
 */
public class TransactionListCell extends TextFieldListCell<Transaction> {
    public TransactionListCell(TransactionStringConverter converter) {
        super(converter);
        this.setEditable(false);
    }
}
