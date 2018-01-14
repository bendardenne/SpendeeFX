package spendee.model;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.collections.transformation.FilteredList;

import java.util.List;
import java.util.function.Predicate;

public class Wallet {

  private ObservableList<Transaction> backingStore = FXCollections.observableArrayList();

  private ObservableMap<EFilterType, Predicate<Transaction>> filters = FXCollections.observableHashMap();
  private FilteredList<Transaction> transactions = new FilteredList<>( backingStore );
  private final ObjectBinding<Predicate<? super Transaction>> binding;

  private SimpleDoubleProperty initialValue = new SimpleDoubleProperty( this, "initialValue" );

  public Wallet() {
    // Dynamic predicate so to that filtered list is updated when the filters are changed.
    binding = Bindings.createObjectBinding(
        () -> t -> filters.values().stream().allMatch( p -> p.test( t ) ), filters );

    transactions.predicateProperty().bind( binding );
  }

  public ObservableList<Transaction> getTransactions() {
    return transactions;
  }

  public ObservableList<Transaction> getUnfilteredTransactions() {
    return backingStore;
  }

  public void setTransactions( List<Transaction> aTransactions ) {
    transactions.predicateProperty().unbind();
    backingStore.setAll( aTransactions );
    transactions.predicateProperty().bind( binding );
  }

  public void resetFilters() {
    filters.clear();
  }

  public void filter( EFilterType aType, Predicate<Transaction> aPredicate ) {
    filters.put( aType, aPredicate );
  }

  public double getInitialValue() {
    return initialValue.get();
  }

  public SimpleDoubleProperty initialValueProperty() {
    return initialValue;
  }

  public void setInitialValue( double aInitialValue ) {
    this.initialValue.set( aInitialValue );
  }
}
