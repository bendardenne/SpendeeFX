package spendee.ui.transactions;

import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import spendee.model.DataStore;
import spendee.model.Transaction;

public class TransactionsTableController {

  @FXML private TableView<Transaction> transactionsTable;

  private DataStore dataStore = DataStore.getInstance();

  @FXML public void initialize() {
    SortedList<Transaction> sortedList = new SortedList<>( dataStore.getTransactions() );
    sortedList.comparatorProperty().bind( transactionsTable.comparatorProperty() );
    transactionsTable.setItems( sortedList );
  }
}
