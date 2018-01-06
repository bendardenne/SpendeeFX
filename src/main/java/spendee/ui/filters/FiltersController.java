package spendee.ui.filters;

import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import spendee.model.Category;
import spendee.model.DataStore;
import spendee.model.EFilterType;
import spendee.model.Transaction;
import spendee.ui.StatusController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

public class FiltersController {

  private DataStore dataStore = DataStore.getInstance();

  @FXML private TextField noteFilter;
  @FXML private MenuButton categoryFilter;

  private StatusController statusController;

  @FXML public void initialize() {
    noteFilter.textProperty().addListener( ( observable, oldValue, newValue ) ->
                                               dataStore.filter( EFilterType.NOTE, makeRegexPredicate( newValue ) ) );

    initializeCategoryFilter();
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