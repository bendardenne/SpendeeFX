package spendee.ui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.MenuBar;
import javafx.stage.FileChooser;
import spendee.model.CSVDecoder;
import spendee.model.DataStore;
import spendee.model.Transaction;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.prefs.Preferences;

import static spendee.Preferences.OPEN_FILE;

public class MenubarController {

  @FXML private MenuBar menuBar;

  private StatusController statusController;
  private DataStore dataStore = DataStore.getInstance();

  @FXML
  protected void onOpen( ActionEvent any ) {
    FileChooser chooser = new FileChooser();
    chooser.getExtensionFilters().add( new FileChooser.ExtensionFilter( "CSV files", "*.csv" ) );

    File file = chooser.showOpenDialog( menuBar.getParent().getScene().getWindow() );
    if ( file != null ) {
      try {
        List<Transaction> decoded = CSVDecoder.decode( file.toPath() );
        dataStore.setTransactions( decoded );
        Preferences.userRoot().put( OPEN_FILE.getKey(), file.toString() );
      }
      catch ( IOException aE ) {
        statusController.message( "Could not open file: " + aE.getMessage() );
      }
    }
  }

  public void setStatusController( StatusController aStatusController ) {
    statusController = aStatusController;
  }

}
