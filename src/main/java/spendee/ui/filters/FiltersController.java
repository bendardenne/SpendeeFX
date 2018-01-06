package spendee.ui.filters;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import spendee.model.DataStore;
import spendee.model.EFilterType;

import java.util.regex.Pattern;

public class FiltersController {

  private DataStore dataStore = DataStore.getInstance();

  @FXML private TextField noteFilter;

  @FXML public void initialize() {
    noteFilter.textProperty().addListener( ( observable, oldValue, newValue ) -> {
      dataStore.filter( EFilterType.NOTE, t -> Pattern.compile( newValue, Pattern.CASE_INSENSITIVE ).matcher( t.getNote() ).find() );
    } );
  }
}
