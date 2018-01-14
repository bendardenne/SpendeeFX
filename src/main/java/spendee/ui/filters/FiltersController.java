package spendee.ui.filters;

import javafx.beans.binding.Bindings;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import spendee.model.Wallet;
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

  private Wallet wallet;
  private StatusController statusController;

  public FiltersController(Wallet aWallet){
    wallet = aWallet;
  }

  @FXML public void initialize() {
    wallet.getUnfilteredTransactions().addListener( ( ListChangeListener<Transaction> ) c -> resetFilters() );
    resetFilters.setOnAction( e -> resetFilters() );

    shownTransactions.textProperty().bind( Bindings.format( "Showing %d of %d transactions",
                                                            Bindings.size( wallet.getTransactions() ),
                                                            Bindings.size( wallet.getUnfilteredTransactions() ) ) );
  }

  private void resetFilters() {
    wallet.resetFilters();

    dateFilterController.reset();
    amountFilterController.reset();
    categoryFilterController.reset();
    scriptingFilterController.reset();
  }

  public void setStatusController( StatusController aStatusController ) {
    statusController = aStatusController;
    noteFilterController.setStatusController( aStatusController );
    scriptingFilterController.setStatusController( aStatusController );
  }
}
