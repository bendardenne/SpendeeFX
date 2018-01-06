package spendee.ui.filters;

import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import spendee.model.DataStore;
import spendee.model.EFilterType;
import spendee.model.Transaction;
import spendee.ui.StatusController;

import java.util.List;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

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

    List<CheckMenuItem> categoryButtons = dataStore.getTransactions().stream()
                                                   .map( Transaction::getCategory )
                                                   .distinct()
                                                   .map( CheckMenuItem::new )
                                                   .collect( toList() );

    Observable[] observables = categoryButtons.stream()
                                              .map( CheckMenuItem::selectedProperty )
                                              .collect( toList() )
                                              .toArray( new Observable[categoryButtons.size()] );

    EventHandler<ActionEvent> setupCategoryFilter = event -> {
      selectAll.setSelected( categoryButtons.stream().allMatch( CheckMenuItem::isSelected ) );

      Predicate<Transaction> predicate = t -> categoryButtons.stream()
                                                             .map( CheckMenuItem.class::cast )
                                                             .filter( CheckMenuItem::isSelected )
                                                             .map( CheckMenuItem::getText )
                                                             .anyMatch( t.getCategory()::equals );

      dataStore.filter( EFilterType.CATEGORY, predicate );
    };

    categoryButtons.forEach( item -> {
      item.setSelected( true );
      categories.add( item );
      item.setOnAction( setupCategoryFilter );
    } );

    selectAll.setOnAction( event -> {
      categoryButtons.stream()
                     .map( CheckMenuItem.class::cast )
                     .forEach( i -> i.setSelected( selectAll.isSelected() ) );
      setupCategoryFilter.handle( event );
    } );

    StringBinding content = Bindings.createStringBinding( () -> {
      long selected = categoryButtons.stream().filter( CheckMenuItem::isSelected ).count();
      if ( selected == categoryButtons.size() ) {
        return "All categories";
      }
      else {
        return selected + " categories";
      }
    }, observables );

    categoryFilter.textProperty().bind( content );
  }

  public void setStatusController( StatusController aStatusController ) {
    statusController = aStatusController;
  }
}
