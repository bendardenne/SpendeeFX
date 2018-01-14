package spendee.ui.config;

import javafx.fxml.FXML;
import javafx.scene.control.Spinner;
import spendee.model.Wallet;

public class ConfigController {

  @FXML private Spinner<Integer> initialWallet;
  private Wallet wallet;

  public ConfigController( Wallet aWallet ) {
    wallet = aWallet;
  }

  @FXML public void initialize() {
    wallet.initialValueProperty().bind( initialWallet.valueProperty() );
  }

}
