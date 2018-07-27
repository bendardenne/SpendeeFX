package spendee.ui.filters;

import javafx.beans.binding.Bindings;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import spendee.model.Account;
import spendee.model.Transaction;
import spendee.ui.StatusController;

public class FiltersController {

  @FXML private Hyperlink resetFilters;
  @FXML private Label shownTransactions;

  @FXML private DateFilter dateFilterController;
  @FXML private AmountFilter amountFilterController;
  @FXML private CategoryFilter categoryFilterController;
  @FXML private NoteFilter noteFilterController;
  @FXML private ScriptingFilter scriptingFilterController;
  @FXML private WalletFilter walletFilterController;

  private Account account;
  private StatusController statusController;

  public FiltersController(Account aAccount ){
    account = aAccount;
  }

  @FXML public void initialize() {
    account.getUnfilteredTransactions().addListener( ( ListChangeListener<Transaction> ) c -> resetFilters() );
    resetFilters.setOnAction( e -> resetFilters() );

    shownTransactions.textProperty().bind( Bindings.format( "Showing %d of %d transactions",
                                                            Bindings.size( account.getTransactions() ),
                                                            Bindings.size( account.getUnfilteredTransactions() ) ) );
  }

  private void resetFilters() {
    account.resetFilters();

    dateFilterController.reset();
    amountFilterController.reset();
    categoryFilterController.reset();
    scriptingFilterController.reset();
    walletFilterController.reset();
  }

  public void setStatusController( StatusController aStatusController ) {
    statusController = aStatusController;
    noteFilterController.setStatusController( aStatusController );
    scriptingFilterController.setStatusController( aStatusController );
  }
}
