package spendee.ui.filters;

import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import spendee.model.Category;
import spendee.model.Transaction;
import spendee.model.Wallet;
import spendee.model.filter.EFilterType;
import spendee.model.filter.Filter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

public class CategoryFilter implements IFilterController {

  @FXML private MenuButton categoryFilter;

  private Wallet wallet;

  public CategoryFilter( Wallet aWallet ) {
    wallet = aWallet;
  }

  @Override public void initialize() {
    populate();
  }

  private void populate() {
    ObservableList<MenuItem> categories = categoryFilter.getItems();

    CheckMenuItem selectAll = new CheckMenuItem( "All categories" );
    selectAll.setSelected( true );

    categories.add( selectAll );
    categories.add( new SeparatorMenuItem() );

    Map<Category.Type, List<Category>> categoriesByType = wallet.getUnfilteredTransactions().stream()
                                                                .map( Transaction::getCategory )
                                                                .collect( groupingBy( Category::getType ) );

    List<CheckMenuItem> incomeEntries = getMenuItems( categoriesByType.getOrDefault( Category.Type.INCOME,
                                                                                     new ArrayList<>() ) );
    List<CheckMenuItem> expenseEntries = getMenuItems( categoriesByType.getOrDefault( Category.Type.EXPENSE,
                                                                                      new ArrayList<>() ) );

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


      List<String> selectedCategories = allEntries.stream()
                                                  .map( CheckMenuItem.class::cast )
                                                  .filter( CheckMenuItem::isSelected )
                                                  .map( CheckMenuItem::getText )
                                                  .collect( toList() );

      Predicate<Transaction> predicate = t -> selectedCategories.stream().anyMatch( t.getCategory().getName()::equals );
      wallet.filter( EFilterType.CATEGORY, new Filter<>( predicate, selectedCategories ) );
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

  @Override public void reset() {
    categoryFilter.getItems().clear();
    populate();
  }


  private List<CheckMenuItem> getMenuItems( List<Category> aCategories ) {
    return aCategories.stream()
                      .map( Category::getName )
                      .distinct()
                      .sorted()
                      .map( CheckMenuItem::new )
                      .collect( toList() );
  }
}
