package spendee.ui;

import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanExpression;
import javafx.beans.binding.StringBinding;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.*;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

public class SelectAllMenuButton extends Control {

  private final MenuButton menuButton = new MenuButton();

  private final CheckMenuItem selectAll;
  private final ObservableList<MenuItem> items = FXCollections.observableArrayList();

  public SelectAllMenuButton() {
    selectAll = new CheckMenuItem( "All" );

    items.addListener( ( ListChangeListener<MenuItem> ) change -> {
      menuButton.getItems().clear();
      menuButton.getItems().addAll( selectAll, new SeparatorMenuItem() );
      menuButton.getItems().addAll( items );
      bindText();
      bindSelected();
    } );

    selectAll.setOnAction( event -> {
      getCheckItems().forEach( item -> item.setSelected( selectAll.isSelected() ) );
      getCheckItems().stream()
                     .map( MenuItem::getOnAction )
                     .filter( Objects::nonNull )
                     .forEach( handler -> handler.handle( event ) );
    } );

    getChildren().add( menuButton );
  }

  private void bindSelected() {
    Optional<BooleanExpression> allSelected = getCheckItems().stream()
                                                             .map( CheckMenuItem::selectedProperty )
                                                             .map( BooleanExpression.class::cast )
                                                             .reduce( BooleanExpression::and );

    allSelected.ifPresent( e -> e.addListener( ( observable, oldValue, newValue ) -> {
      selectAll.setSelected( newValue );
    } ) );
  }

  @Override protected Skin<?> createDefaultSkin() {
    return new SelectAllMenuButtonSkin( this, menuButton );
  }

  public void selectAll() {
    selectAll.setSelected( true );
    selectAll.getOnAction().handle( new ActionEvent( this, null ) );
  }

  public void deselectAll() {
    selectAll.setSelected( false );
    selectAll.getOnAction().handle( new ActionEvent( this, null ) );
  }

  private void bindText() {
    List<CheckMenuItem> checkItems = getCheckItems();
    Observable[] observables = checkItems.stream()
                                         .map( CheckMenuItem::selectedProperty )
                                         .collect( toList() )
                                         .toArray( new Observable[checkItems.size()] );

    StringBinding content = Bindings.createStringBinding( () -> {
      long selected = checkItems.stream().filter( CheckMenuItem::isSelected ).count();
      if ( selected == checkItems.size() ) {
        return "All (" + checkItems.size() + ")";
      }
      else {
        return selected + " selected";
      }
    }, observables );

    menuButton.textProperty().bind( content );
  }

  public ObservableList<MenuItem> getItems() {
    return items;
  }

  private List<CheckMenuItem> getCheckItems() {
    return items.stream()
                .filter( CheckMenuItem.class::isInstance )
                .map( CheckMenuItem.class::cast )
                .collect( toList() );
  }
}
