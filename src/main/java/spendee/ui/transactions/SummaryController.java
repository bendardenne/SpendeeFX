package spendee.ui.transactions;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import spendee.model.Account;
import spendee.model.Transaction;

import java.text.DecimalFormat;


public class SummaryController {

  @FXML private Label incomeTotal;
  @FXML private Label expenseTotal;
  @FXML private Label balance;
  @FXML private Label balanceLabel;

  private Account account;

  public SummaryController(Account aAccount ) {
    account = aAccount;
  }

  @FXML public void initialize() {
    ObservableList<Transaction> transactions = account.getTransactions();
    DecimalFormat formatter = new DecimalFormat( "#.##" );


    incomeTotal.textProperty().bind( Bindings.createStringBinding(
        () -> formatter.format( transactions
                                    .stream()
                                    .filter( t -> t.getAmount() > 0 )
                                    .mapToDouble( Transaction::getAmount )
                                    .sum() ),
        transactions ) );

    expenseTotal.textProperty().bind( Bindings.createStringBinding(
        () -> formatter.format( transactions
                                    .stream()
                                    .filter( t -> t.getAmount() < 0 )
                                    .mapToDouble( Transaction::getAmount )
                                    .sum() ), transactions ) );


    balance.textProperty().bind( Bindings.createStringBinding(
        () -> formatter.format( transactions
                                    .stream()
                                    .mapToDouble( Transaction::getAmount )
                                    .sum() ), transactions ) );

    StringBinding positiveBalance = Bindings.createStringBinding( () -> transactions
                                                                            .stream()
                                                                            .mapToDouble( Transaction::getAmount )
                                                                            .sum() > 0 ? "income" : "expenses",
                                                                  transactions );

    balance.getStyleClass().add( positiveBalance.getValue() );
    balanceLabel.getStyleClass().add( positiveBalance.getValue() );

    transactions.addListener( ( ListChangeListener<Transaction> ) c -> {
      balance.getStyleClass().removeAll( "income", "expenses" );
      balanceLabel.getStyleClass().removeAll( "income", "expenses" );

      balance.getStyleClass().add( positiveBalance.getValue() );
      balanceLabel.getStyleClass().add( positiveBalance.getValue() );
    } );
  }

}
