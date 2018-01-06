package spendee.ui.transactions;

import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import spendee.model.DataStore;
import spendee.model.Transaction;

public class TransactionsTableController {

  @FXML private TableView<Transaction> transactionsTable;

  private DataStore dataStore = DataStore.getInstance();

  @FXML public void initialize( ) {
    transactionsTable.setItems( dataStore.getTransactions() );
  }
}
