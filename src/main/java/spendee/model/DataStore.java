package spendee.model;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.ObjectBinding;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.collections.transformation.FilteredList;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.function.Predicate;
import java.util.prefs.Preferences;

import static spendee.Preferences.OPEN_FILE;

public class DataStore {

  private static DataStore instance = new DataStore();

  public static DataStore getInstance() {
    return instance;
  }

  static {
    // Restore last open file.
    String file = Preferences.userRoot().get( OPEN_FILE.getKey(), OPEN_FILE.getValue() );
    if ( file != null ) {
      try {
        instance.setTransactions( CSVDecoder.decode( Paths.get( file ) ) );
      }
      catch ( IOException aE ) {
        // TODO
      }
    }
  }

  private ObservableList<Transaction> backingStore = FXCollections.observableArrayList();
  private ObservableMap<EFilterType, Predicate<Transaction>> filters = FXCollections.observableHashMap();

  private FilteredList<Transaction> transactions = new FilteredList<>( backingStore );

  private DataStore() {
    // Dynamic predicate so to that filtered list is updated when the filters are changed.
    ObjectBinding<Predicate<? super Transaction>> binding = Bindings.createObjectBinding(
        () -> t -> filters.values().stream().allMatch( p -> p.test( t ) ), filters );

    transactions.predicateProperty().bind( binding );
  }

  public ObservableList<Transaction> getTransactions() {
    return transactions;
  }

  public void setTransactions( List<Transaction> aTransactions ) {
    backingStore.clear();
    backingStore.addAll( aTransactions );
  }

  public void filter( EFilterType aType, Predicate<Transaction> aPredicate ) {
    filters.put( aType, aPredicate );
  }
}
