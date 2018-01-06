package spendee.model;

import java.time.ZonedDateTime;

public class Transaction {

  private double amount;
  private String note;
  private ZonedDateTime date;
  private Category category;

  public Transaction( double aAmount, String aNote, ZonedDateTime aDate, Category aCategory ) {
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

  public Category getCategory() {
    return category;
  }
}
