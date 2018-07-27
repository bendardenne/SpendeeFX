package spendee.ui.filters;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import jdk.nashorn.api.scripting.NashornScriptEngineFactory;
import spendee.model.Transaction;
import spendee.model.Account;
import spendee.model.filter.EFilterType;
import spendee.model.filter.Filter;
import spendee.ui.StatusController;

import javax.script.ScriptEngine;
import javax.script.ScriptException;
import java.util.function.Predicate;

public class ScriptingFilter implements IFilterController {

  @FXML private TextArea scriptingFilter;
  @FXML private Button clearButton;

  private Account account;
  private StatusController statusController;

  public ScriptingFilter( Account aAccount ) {
    account = aAccount;
  }

  @Override public void initialize() {
    // Hopefully, with Java 9, we can get ES6 arrow functions and then we can do e.g.
    // transaction -> transaction.getAmount() > 0
    clearButton.setOnAction( e -> reset() );

    ScriptEngine scriptEngine = new NashornScriptEngineFactory().getScriptEngine( "--language=es6" );
    scriptingFilter.textProperty().addListener( ( observable, oldValue, newValue ) -> {
      try {
        Predicate<Transaction> predicate = ( Predicate<Transaction> )
            scriptEngine.eval( "new java.util.function.Predicate( " + newValue + " )" );
        account.filter( EFilterType.JAVASCRIPT, new Filter<>( predicate, newValue ) );
      }
      catch ( ScriptException aE ) {
        account.filter( EFilterType.JAVASCRIPT, Filter.acceptAll( "" ) );
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
