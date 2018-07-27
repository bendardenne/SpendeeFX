package spendee.ui.config;

import javafx.fxml.FXML;
import javafx.scene.control.Spinner;
import spendee.model.Account;

public class ConfigController {

  @FXML private Spinner<Integer> initialWallet;
  private Account account;

  public ConfigController( Account aAccount ) {
    account = aAccount;
  }

  @FXML public void initialize() {
    account.initialValueProperty().bind( initialWallet.valueProperty() );
  }

}
