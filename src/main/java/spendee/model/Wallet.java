package spendee.model;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import spendee.model.filter.EFilterType;
import spendee.model.filter.Filter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

public class Wallet {

  private final ObservableList<Transaction> unfilteredTransactions = FXCollections.observableArrayList();
  private FilteredList<Transaction> transactions = new FilteredList<>( unfilteredTransactions );

  private final Map<EFilterType, ObjectProperty<Filter<Transaction, ?>>> filters = new HashMap<>(
      EFilterType.values().length );

  private final ObjectBinding<Predicate<? super Transaction>> binding;

  private SimpleDoubleProperty initialValue = new SimpleDoubleProperty( this, "initialValue" );

  public Wallet() {
    // Dynamic predicate so to that filtered list is updated when the filters are changed.
    resetFilters();
    binding = Bindings.createObjectBinding( () -> t -> filters.values().stream().allMatch( p -> p.get().test( t ) ),
                                            filters.values().toArray( new ObjectProperty[]{} ) );

    transactions.predicateProperty().bind( binding );

  }

  public ObservableList<Transaction> getTransactions() {
    return transactions;
  }

  public ObservableList<Transaction> getUnfilteredTransactions() {
    return unfilteredTransactions;
  }

  public void setTransactions( List<Transaction> aTransactions ) {
    transactions.predicateProperty().unbind();
    unfilteredTransactions.setAll( aTransactions );
    transactions.predicateProperty().bind( binding );
  }

  public void resetFilters() {
    for ( EFilterType type : EFilterType.values() ) {
      ObjectProperty<Filter<Transaction, ?>> filter = filters
          .getOrDefault( type, new SimpleObjectProperty<>( this, type.toString() ) );

      filter.setValue( Filter.acceptAll( "" ) );
      filters.put( type, filter );
    }
  }

  public void filter( EFilterType aType, Filter<Transaction, ?> aFilter ) {
    filters.get( aType ).setValue( aFilter );
  }

  public Filter<Transaction, ?> getFilter( EFilterType aType ) {
    return filters.get( aType ).get();
  }


  public ObjectProperty<Filter<Transaction, ?>> filterProperty( EFilterType aType ) {
    return filters.get( aType );
  }

  public double getInitialValue() {
    return initialValue.get();
  }

  public SimpleDoubleProperty initialValueProperty() {
    return initialValue;
  }

  public void setInitialValue( double aInitialValue ) {
    initialValue.set( aInitialValue );
  }
}
