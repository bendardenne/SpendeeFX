package spendee.ui.transactions;

import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import spendee.model.Transaction;
import spendee.model.Account;

public class TransactionsTableController {

  @FXML private TableView<Transaction> transactionsTable;

  private Account account;

  public TransactionsTableController( Account aAccount ) {
    account = aAccount;
  }

  @FXML public void initialize() {
    SortedList<Transaction> sortedList = new SortedList<>( account.getTransactions() );
    sortedList.comparatorProperty().bind( transactionsTable.comparatorProperty() );

    transactionsTable.getColumns().stream().filter( col -> "Note".equals( col.getText() ) )
                     .findFirst()
                     .ifPresent( ( col ) ->
                                     ( ( TableColumn<Transaction, String> ) col )
                                         .setCellFactory( new NoteCellFactory( account ) ) );


    transactionsTable.setItems( sortedList );
  }
}
