package spendee.ui.filters;

import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import org.controlsfx.control.textfield.TextFields;
import spendee.model.Account;
import spendee.model.Transaction;
import spendee.model.filter.EFilterType;
import spendee.model.filter.Filter;
import spendee.ui.StatusController;
import spendee.util.HashtagUtil;

import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class NoteFilter implements IFilterController {

  private static final int MAXIMUM_HASHTAGS = 10;     // Max number of # to show in the list

  @FXML private TextField noteFilter;
  @FXML private FlowPane hashtagsList;

  @FXML private Button clearButton;

  private final Account account;
  private StatusController statusController;

  public NoteFilter( Account aAccount ) {
    account = aAccount;
  }

  @Override
  public void initialize() {
    noteFilter.textProperty().addListener( ( observable, oldValue, newValue ) ->
                                               account.filter( EFilterType.NOTE, makeRegexFilter( newValue ) ) );

    clearButton.setOnAction( e -> reset() );
    // Will be populated later.
//    hashtagProvider = SuggestionProvider.create( Collections.emptyList() );
//    TextFields.bindAutoCompletion( noteFilter, hashtagProvider );
    account.getTransactions().addListener( ( ListChangeListener<Transaction> ) c -> populateHashtagsList() );
    populateHashtagsList();

    account.filterProperty( EFilterType.NOTE ).addListener( ( observable, oldValue, newValue ) ->
                                                                noteFilter.setText(
                                                                    ( String ) account.getFilter( EFilterType.NOTE )
                                                                        .getAccepted() ) );
  }

  @Override
  public void reset() {
    noteFilter.setText( "" );
  }

  private void populateHashtagsList() {
    hashtagsList.getChildren().clear();

    Map<String, Long> map = HashtagUtil.extractHashtags( account.getTransactions() );

    map.entrySet().stream()
        .sorted( Collections.reverseOrder( Comparator.comparingLong( Map.Entry::getValue ) ) )
        .map( Map.Entry::getKey )
        .limit( MAXIMUM_HASHTAGS )
        .map( Hyperlink::new )
        .forEach( link -> {
          link.setOnAction( e -> noteFilter.setText( link.getText() ) );
          hashtagsList.getChildren().add( link );
        } );

//    hashtagProvider.clearSuggestions();
//    hashtagProvider.addPossibleSuggestions( map.keySet() );
    TextFields.bindAutoCompletion( noteFilter, map.keySet() );
  }

  private Filter<Transaction, String> makeRegexFilter( String newValue ) {
    Predicate<Transaction> predicate = t -> {
      try {
        return Pattern.compile( newValue, Pattern.CASE_INSENSITIVE ).matcher( t.getNote() ).find();
      }
      catch ( PatternSyntaxException e ) {
        statusController.message( "Incorrect regex: " + e.getDescription() );
      }

      return true;
    };

    return new Filter<>( predicate, newValue );
  }

  public void setStatusController( StatusController aStatusController ) {
    statusController = aStatusController;
  }
}
