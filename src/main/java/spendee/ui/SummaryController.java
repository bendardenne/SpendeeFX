package spendee.ui;

import javafx.beans.binding.Bindings;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import spendee.model.DataStore;
import spendee.model.Transaction;

import java.text.DecimalFormat;


public class SummaryController {

  @FXML private Label incomeTotal;
  @FXML private Label expenseTotal;

  private DataStore dataStore = DataStore.getInstance();

  @FXML public void initialize() {
    ObservableList<Transaction> transactions = dataStore.getTransactions();
    DecimalFormat formatter = new DecimalFormat( "#.##" );

    incomeTotal.textProperty().bind( Bindings.createStringBinding(
        () -> formatter.format( transactions
                                   .stream()
                                   .filter( t -> t.getAmount() > 0 )
                                   .mapToDouble( Transaction::getAmount )
                                   .sum() ), transactions ) );

    expenseTotal.textProperty().bind( Bindings.createStringBinding(
        () -> formatter.format( transactions
                                   .stream()
                                   .filter( t -> t.getAmount() < 0 )
                                   .mapToDouble( Transaction::getAmount )
                                   .sum() ), transactions ) );
  }

}
