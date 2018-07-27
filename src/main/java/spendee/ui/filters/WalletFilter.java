package spendee.ui.filters;

import javafx.fxml.FXML;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.MenuItem;
import spendee.model.Account;
import spendee.model.Transaction;
import spendee.ui.SelectAllMenuButton;

public class WalletFilter implements IFilterController {

  @FXML private SelectAllMenuButton walletFilter;

  private final Account account;

  public WalletFilter( Account aAccount ) {
    account = aAccount;
  }

  @Override public void initialize() {
    populate();
  }

  private void populate() {
    CheckMenuItem selectAll = new CheckMenuItem( "All wallets" );
    selectAll.setSelected( true );

    account.getUnfilteredTransactions().stream()
           .map( Transaction::getWallet )
           .distinct()
           .map( WalletFilter::createMenuItem )
           .forEach( walletFilter.getItems()::add );

    walletFilter.selectAll();
  }

  private static MenuItem createMenuItem( String aWallet ) {
    return new CheckMenuItem( aWallet );
  }

  @Override public void reset() {
    walletFilter.getItems().clear();
    populate();
  }
}
