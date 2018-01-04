package spendee.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.List;

public class DataStore {

  private static DataStore instance = new DataStore();

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
