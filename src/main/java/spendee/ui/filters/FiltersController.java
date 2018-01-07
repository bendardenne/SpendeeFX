package spendee.ui.filters;

import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import spendee.model.Category;
import spendee.model.DataStore;
import spendee.model.EFilterType;
import spendee.model.Transaction;
import spendee.ui.StatusController;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import static java.util.stream.Collectors.*;

public class FiltersController {

  private static final Pattern HASHTAG = Pattern.compile( "#\\p{Alnum}+" );
  private static final int MAXIMUM_HASHTAGS = 10;

  private DataStore dataStore = DataStore.getInstance();

  @FXML private TextField noteFilter;
  @FXML private MenuButton categoryFilter;
  @FXML private FlowPane hashtagsList;
  @FXML private Button clearButton;

  private StatusController statusController;

  @FXML public void initialize() {
    noteFilter.textProperty().addListener( ( observable, oldValue, newValue ) ->
                                               dataStore.filter( EFilterType.NOTE, makeRegexPredicate( newValue ) ) );

    clearButton.setOnAction( e -> noteFilter.setText( "" ) );

    dataStore.getTransactions().addListener( ( ListChangeListener<Transaction> ) c -> initializeHashtagsList() );


    initializeCategoryFilter();
    initializeHashtagsList();
  }

  private void initializeHashtagsList() {
    hashtagsList.getChildren().clear();

    Map<String, Long> map = extractHashtags( dataStore.getTransactions() );

    map.entrySet().stream()
       .sorted( Collections.reverseOrder(
           Comparator.comparingLong( Map.Entry::getValue ) ) )
       .map( Map.Entry::getKey )
       .limit( MAXIMUM_HASHTAGS )
       .map( Hyperlink::new )
       .forEach( link -> {
         link.setOnAction( e -> noteFilter.setText( link.getText() ) );
         hashtagsList.getChildren().add(link);
       } );
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

  private void initializeCategoryFilter() {
    ObservableList<MenuItem> categories = categoryFilter.getItems();

    CheckMenuItem selectAll = new CheckMenuItem( "All categories" );
    selectAll.setSelected( true );

    categories.add( selectAll );
    categories.add( new SeparatorMenuItem() );

    Map<Category.Type, List<Category>> categoriesByType = dataStore.getTransactions().stream()
                                                                   .map( Transaction::getCategory )
                                                                   .collect( groupingBy( Category::getType ) );

    List<CheckMenuItem> incomeEntries = getMenuItems( categoriesByType.get( Category.Type.INCOME ) );
    List<CheckMenuItem> expenseEntries = getMenuItems( categoriesByType.get( Category.Type.EXPENSE ) );

    categories.addAll( incomeEntries );
    categories.add( new SeparatorMenuItem() );
    categories.addAll( expenseEntries );

    List<CheckMenuItem> allEntries = new ArrayList<>( incomeEntries );
    allEntries.addAll( expenseEntries );

    Observable[] observables = allEntries.stream()
                                         .map( CheckMenuItem::selectedProperty )
                                         .collect( toList() )
                                         .toArray( new Observable[allEntries.size()] );

    EventHandler<ActionEvent> setupCategoryFilter = event -> {
      selectAll.setSelected( allEntries.stream().allMatch( CheckMenuItem::isSelected ) );

      Predicate<Transaction> predicate = t -> allEntries.stream()
                                                        .map( CheckMenuItem.class::cast )
                                                        .filter( CheckMenuItem::isSelected )
                                                        .map( CheckMenuItem::getText )
                                                        .anyMatch( t.getCategory().getName()::equals );

      dataStore.filter( EFilterType.CATEGORY, predicate );
    };

    allEntries.forEach( item -> {
      item.setSelected( true );
      item.setOnAction( setupCategoryFilter );
    } );

    selectAll.setOnAction( event -> {
      allEntries.stream()
                .map( CheckMenuItem.class::cast )
                .forEach( i -> i.setSelected( selectAll.isSelected() ) );
      setupCategoryFilter.handle( event );
    } );

    StringBinding content = Bindings.createStringBinding( () -> {
      long selected = allEntries.stream().filter( CheckMenuItem::isSelected ).count();
      if ( selected == allEntries.size() ) {
        return "All categories (" + allEntries.size() + ")";
      }
      else {
        return selected + " categories";
      }
    }, observables );

    categoryFilter.textProperty().bind( content );
  }

  private List<CheckMenuItem> getMenuItems( List<Category> aCategories ) {
    return aCategories.stream()
                      .map( Category::getName )
                      .distinct()
                      .sorted()
                      .map( CheckMenuItem::new )
                      .collect( toList() );
  }

  public void setStatusController( StatusController aStatusController ) {
    statusController = aStatusController;
  }
}
