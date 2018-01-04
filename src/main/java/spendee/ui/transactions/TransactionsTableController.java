package spendee.ui.transactions;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableView;
import spendee.model.DataStore;
import spendee.model.Transaction;

import java.net.URL;
import java.util.ResourceBundle;

public class TransactionsTableController implements Initializable {

  @FXML private TableView<Transaction> transactionsTable;

  private DataStore dataStore = DataStore.getInstance();

  @Override public void initialize( URL location, ResourceBundle resources ) {
    transactionsTable.setItems( dataStore.getTransactions() );
  }
}
