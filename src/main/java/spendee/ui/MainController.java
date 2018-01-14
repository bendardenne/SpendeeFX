package spendee.ui;

import javafx.fxml.FXML;
import spendee.model.CSVDecoder;
import spendee.model.Wallet;
import spendee.ui.filters.FiltersController;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.prefs.Preferences;

import static spendee.Preferences.OPEN_FILE;

public class MainController {

  @FXML private StatusController statusController;
  @FXML private FiltersController filtersController;
  @FXML private MenubarController menubarController;
  private Wallet wallet;

  public MainController( Wallet aWallet ) {
    wallet = aWallet;
  }

  @FXML public void initialize() {

    String file = Preferences.userRoot().get( OPEN_FILE.getKey(), OPEN_FILE.getValue() );
    if ( file != null ) {
      try {
        wallet.setTransactions( CSVDecoder.decode( Paths.get( file ) ) );
      }
      catch ( IOException aE ) {
        statusController.message( String.format( "Could not restore previous CSV (%s): %s", file, aE ) );
      }
    }


    filtersController.setStatusController( statusController );
    menubarController.setStatusController( statusController );
  }

}
