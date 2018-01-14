package spendee.ui.filters;

import impl.org.controlsfx.autocompletion.SuggestionProvider;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import org.controlsfx.control.textfield.TextFields;
import spendee.model.Wallet;
import spendee.model.EFilterType;
import spendee.model.Transaction;
import spendee.ui.StatusController;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;

public class NoteFilter implements IFilterController {

  private static final Pattern HASHTAG = Pattern.compile( "#\\p{Alnum}+" );
  private static final int MAXIMUM_HASHTAGS = 10;     // Max number of # to show in the list


  @FXML private TextField noteFilter;
  @FXML private FlowPane hashtagsList;

  @FXML private Button clearButton;

  private Wallet wallet;

  private SuggestionProvider<String> hashtagProvider;
  private StatusController statusController;

  public NoteFilter(Wallet aWallet){
    wallet = aWallet;
  }

  @Override public void initialize() {
    noteFilter.textProperty().addListener( ( observable, oldValue, newValue ) ->
                                               wallet.filter( EFilterType.NOTE, makeRegexPredicate( newValue ) ) );

    clearButton.setOnAction( e -> reset() );

    // Will be populated later.
    hashtagProvider = SuggestionProvider.create( Collections.emptyList() );
    TextFields.bindAutoCompletion( noteFilter, hashtagProvider );
    wallet.getTransactions().addListener( ( ListChangeListener<Transaction> ) c -> populateHashtagsList() );
    populateHashtagsList();
  }

  @Override public void reset() {
    noteFilter.setText( "" );
  }

  private void populateHashtagsList() {
    hashtagsList.getChildren().clear();

    Map<String, Long> map = extractHashtags( wallet.getTransactions() );

    map.entrySet().stream()
       .sorted( Collections.reverseOrder(
           Comparator.comparingLong( Map.Entry::getValue ) ) )
       .map( Map.Entry::getKey )
       .limit( MAXIMUM_HASHTAGS )
       .map( Hyperlink::new )
       .forEach( link -> {
         link.setOnAction( e -> noteFilter.setText( link.getText() ) );
         hashtagsList.getChildren().add( link );
       } );

    hashtagProvider.clearSuggestions();
    hashtagProvider.addPossibleSuggestions( map.keySet() );
  }


  private Map<String, Long> extractHashtags( ObservableList<? extends Transaction> aList ) {
    return aList.stream()
                .map( Transaction::getNote )
                .map( this::extractHashtags )
                .flatMap( Collection::stream )
                .collect( groupingBy( Function.identity(), counting() ) );
  }


  private Collection<String> extractHashtags( String string ) {
    Set<String> found = new HashSet<>();
    Matcher matcher = HASHTAG.matcher( string );
    while ( matcher.find() ) {
      found.add( matcher.group() );
    }
    return found;
  }

  private Predicate<Transaction> makeRegexPredicate( String newValue ) {
    return t -> {
      try {
        return Pattern.compile( newValue, Pattern.CASE_INSENSITIVE ).matcher( t.getNote() ).find();
      }
      catch ( PatternSyntaxException e ) {
        statusController.message( "Incorrect regex: " + e.getDescription() );
      }

      return true;
    };
  }

  public void setStatusController( StatusController aStatusController ) {
    statusController = aStatusController;
  }
}
