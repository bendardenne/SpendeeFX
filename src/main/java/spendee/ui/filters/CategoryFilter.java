package spendee.ui.filters;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import spendee.model.Account;
import spendee.model.Category;
import spendee.model.Transaction;
import spendee.model.filter.EFilterType;
import spendee.model.filter.Filter;
import spendee.ui.SelectAllMenuButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

public class CategoryFilter implements IFilterController {

  @FXML private SelectAllMenuButton categoryFilter;

  private Account account;

  public CategoryFilter( Account aAccount ) {
    account = aAccount;
  }

  @Override public void initialize() {
    populate();
  }

  private void populate() {
    ObservableList<MenuItem> categories = categoryFilter.getItems();

    Map<Category.Type, List<Category>> categoriesByType = account.getUnfilteredTransactions().stream()
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

    EventHandler<ActionEvent> setupCategoryFilter = event -> {
      List<String> selectedCategories = allEntries.stream()
                                                  .filter( CheckMenuItem::isSelected )
                                                  .map( CheckMenuItem::getText )
                                                  .collect( toList() );

      Predicate<Transaction> predicate = t -> selectedCategories.stream().anyMatch( t.getCategory().getName()::equals );
      account.filter( EFilterType.CATEGORY, new Filter<>( predicate, selectedCategories ) );
    };


    allEntries.forEach( item -> item.setOnAction( setupCategoryFilter ) );
    categoryFilter.selectAll();
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
