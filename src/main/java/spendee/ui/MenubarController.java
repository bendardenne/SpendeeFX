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

public class MenubarController {

  @FXML private MenuBar menuBar;
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
      }
      catch ( IOException aE ) {
        // TODO
        aE.printStackTrace();
      }
    }
  }


}
