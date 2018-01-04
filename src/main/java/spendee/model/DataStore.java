package spendee.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.prefs.Preferences;

import static spendee.Preferences.OPEN_FILE;

public class DataStore {

  private static DataStore instance = new DataStore();

  static {
    // Restore last open file.
    String file = Preferences.userRoot().get( OPEN_FILE.getKey(), OPEN_FILE.getValue() );
    if(file != null) {
      try {
        instance.setTransactions( CSVDecoder.decode( Paths.get(file) ) );
      }
      catch ( IOException aE ) {
        // TODO
      }
    }
  }

  public static DataStore getInstance() {
    return instance;
  }

  private ObservableList<Transaction> transactions = FXCollections.observableArrayList();

  public ObservableList<Transaction> getTransactions() {
    return transactions;
  }

  public void setTransactions( List<Transaction> aTransactions ) {
    transactions.clear();
    transactions.addAll( aTransactions );
  }
}
