package spendee.ui.filters;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import jdk.nashorn.api.scripting.NashornScriptEngineFactory;
import spendee.model.DataStore;
import spendee.model.EFilterType;
import spendee.model.Transaction;
import spendee.ui.StatusController;

import javax.script.ScriptEngine;
import javax.script.ScriptException;
import java.util.function.Predicate;

public class ScriptingFilter implements IFilterController {

  @FXML private TextArea scriptingFilter;
  @FXML private Button clearButton;

  private DataStore dataStore = DataStore.getInstance();
  private StatusController statusController;

  @Override public void initialize() {
    // Hopefully, with Java 9, we can get ES6 arrow functions and then we can do e.g.
    // transaction -> transaction.getAmount()  > 0

    clearButton.setOnAction( e -> reset() );

    ScriptEngine scriptEngine = new NashornScriptEngineFactory().getScriptEngine( "--language=es6");
    scriptingFilter.textProperty().addListener( ( observable, oldValue, newValue ) -> {
      try {
        Predicate<Transaction> predicate = ( Predicate<Transaction> )
            scriptEngine.eval( "new java.util.function.Predicate( " + newValue + " )" );
        dataStore.filter( EFilterType.JAVASCRIPT, predicate );
      }
      catch ( ScriptException aE ) {
        dataStore.filter( EFilterType.JAVASCRIPT, t -> true );
        statusController.message( aE.getMessage() );
      }
    } );
  }

  @Override public void reset() {
    scriptingFilter.setText( "" );
  }

  public void setStatusController( StatusController aStatusController ) {
    statusController = aStatusController;
  }
}
