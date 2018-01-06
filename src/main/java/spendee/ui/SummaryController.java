package spendee.ui;

import javafx.beans.binding.Bindings;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import spendee.model.DataStore;
import spendee.model.Transaction;


public class SummaryController {

  @FXML private Label incomeTotal;
  @FXML private Label expenseTotal;

  private DataStore dataStore = DataStore.getInstance();

  @FXML public void initialize() {
    ObservableList<Transaction> transactions = dataStore.getTransactions();

    incomeTotal.textProperty().bind( Bindings.createStringBinding(
        () -> Double.toString( transactions
                                   .stream()
                                   .filter( t -> t.getAmount() > 0 )
                                   .mapToDouble( Transaction::getAmount )
                                   .sum() ), transactions ) );

    expenseTotal.textProperty().bind( Bindings.createStringBinding(
        () -> Double.toString( transactions
                                   .stream()
                                   .filter( t -> t.getAmount() < 0 )
                                   .mapToDouble( Transaction::getAmount )
                                   .sum() ), transactions ) );
  }

}
