package spendee.ui.transactions;

import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import spendee.model.Transaction;
import spendee.model.Wallet;

public class TransactionsTableController {

  @FXML private TableView<Transaction> transactionsTable;

  private Wallet wallet;

  public TransactionsTableController( Wallet aWallet ) {
    wallet = aWallet;
  }

  @FXML public void initialize() {
    SortedList<Transaction> sortedList = new SortedList<>( wallet.getTransactions() );
    sortedList.comparatorProperty().bind( transactionsTable.comparatorProperty() );
    transactionsTable.setItems( sortedList );
  }
}
