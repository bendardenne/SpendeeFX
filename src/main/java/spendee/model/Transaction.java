package spendee.model;

import java.time.LocalDateTime;

public class Transaction {

  private double amount;
  private String note;
  private LocalDateTime date;
  private String category;

  public Transaction( double aAmount, String aNote, LocalDateTime aDate, String aCategory ) {
    amount = aAmount;
    note = aNote;
    date = aDate;
    category = aCategory;
  }

  public double getAmount() {
    return amount;
  }

  public String getNote() {
    return note;
  }

  public LocalDateTime getDate() {
    return date;
  }

  public String getCategory() {
    return category;
  }
}
