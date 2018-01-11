package spendee.ui.filters;

import impl.org.controlsfx.autocompletion.SuggestionProvider;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.beans.value.ChangeListener;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import jfxtras.scene.control.CalendarPicker;
import org.controlsfx.control.RangeSlider;
import org.controlsfx.control.textfield.TextFields;
import spendee.model.Category;
import spendee.model.DataStore;
import spendee.model.EFilterType;
import spendee.model.Transaction;
import spendee.ui.StatusController;
import spendee.util.DateUtil;

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

  @FXML private Hyperlink resetFilters;

  @FXML private TextField noteFilter;
  @FXML private RangeSlider amountFilter;
  @FXML private Label minAmount;
  @FXML private Label maxAmount;
  @FXML private MenuButton categoryFilter;

  @FXML private CalendarPicker dateFilter;
  @FXML private Label dateLabel;

  @FXML private FlowPane hashtagsList;
  @FXML private Button clearButton;

  @FXML private Label shownTransactions;

  private StatusController statusController;
  private SuggestionProvider<String> hashtagProvider;

  @FXML public void initialize() {
    dataStore.getUnfilteredTransactions().addListener( ( ListChangeListener<Transaction> ) c -> resetFilters() );
    resetFilters.setOnAction( e -> resetFilters() );

    noteFilter.textProperty().addListener( ( observable, oldValue, newValue ) ->
                                               dataStore.filter( EFilterType.NOTE, makeRegexPredicate( newValue ) ) );

    // Will be populated later.
    hashtagProvider = SuggestionProvider.create( Collections.emptyList() );
    TextFields.bindAutoCompletion( noteFilter, hashtagProvider );

    clearButton.setOnAction( e -> noteFilter.setText( "" ) );

    dataStore.getTransactions().addListener( ( ListChangeListener<Transaction> ) c -> initializeHashtagsList() );

    initializeCategoryFilter();
    initializeAmountFilter();
    initializeDateFilter();
    initializeHashtagsList();

    shownTransactions.textProperty().bind( Bindings.format( "Showing %d of %d transactions",
                                                            Bindings.size( dataStore.getTransactions() ),
                                                            Bindings.size( dataStore.getUnfilteredTransactions() ) ) );
  }

  private void initializeDateFilter() {
    dateLabel.textProperty().bind( Bindings.createStringBinding(
        () -> DateUtil.prettyDate( dateFilter.calendars() ), dateFilter.calendars() ) );

    dateFilter.calendars().addListener( ( ListChangeListener<Calendar> ) c -> {
      ObservableList<? extends Calendar> dates = c.getList().sorted();
      if ( dates.size() == 0 ) {
        dataStore.filter( EFilterType.DATE, t -> true );
      }
      else {
        dataStore.filter( EFilterType.DATE, t -> dates.stream().map( DateUtil::toLocalDate )
                                                      .anyMatch( t.getDate().toLocalDate()::equals ));
      }
    } );
  }



  private void resetFilters() {
    dataStore.resetFilters();

    noteFilter.setText( "" );
    initializeCategoryFilter();

    amountFilter.setHighValue( amountFilter.getMax() );
    amountFilter.setLowValue( amountFilter.getMin() );
  }

  private void initializeAmountFilter() {
    amountFilter.minProperty().bind( Bindings.createDoubleBinding(
        () -> dataStore.getUnfilteredTransactions().stream().collect( summarizingDouble( Transaction::getAmount ) )
                       .getMin(), dataStore.getUnfilteredTransactions() ) );
    amountFilter.maxProperty().bind( Bindings.createDoubleBinding(
        () -> dataStore.getUnfilteredTransactions().stream().collect( summarizingDouble(
            Transaction::getAmount ) ).getMax(), dataStore.getUnfilteredTransactions() ) );

    amountFilter.setHighValue( amountFilter.getMax() );
    amountFilter.setLowValue( amountFilter.getMin() );

    // Update filter when high and low are changed.
    ChangeListener<Number> updateAmountFilter = ( observable, oldValue, newValue ) ->
        dataStore.filter( EFilterType.AMOUNT, t -> amountFilter.getLowValue() <= t.getAmount() &&
                                                   t.getAmount() <= amountFilter.getHighValue() );

    amountFilter.lowValueProperty().addListener( updateAmountFilter );
    amountFilter.highValueProperty().addListener( updateAmountFilter );

    minAmount.textProperty().bind( amountFilter.lowValueProperty().asString( "%.2f" ) );
    maxAmount.textProperty().bind( amountFilter.highValueProperty().asString( "%.2f" ) );

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

  private void initializeCategoryFilter() {
    ObservableList<MenuItem> categories = categoryFilter.getItems();
    categories.clear();

    CheckMenuItem selectAll = new CheckMenuItem( "All categories" );
    selectAll.setSelected( true );

    categories.add( selectAll );
    categories.add( new SeparatorMenuItem() );

    Map<Category.Type, List<Category>> categoriesByType = dataStore.getUnfilteredTransactions().stream()
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
