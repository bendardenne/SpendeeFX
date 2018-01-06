package spendee.model;

import java.time.ZonedDateTime;

public class Transaction {

  private double amount;
  private String note;
  private ZonedDateTime date;
  private String category;

  public Transaction( double aAmount, String aNote, ZonedDateTime aDate, String aCategory ) {
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

  public ZonedDateTime getDate() {
    return date;
  }

  public String getCategory() {
    return category;
  }
}
