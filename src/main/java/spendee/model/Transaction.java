package spendee.model;

import java.time.ZonedDateTime;

public class Transaction {

  private final double amount;
  private final String note;
  private final ZonedDateTime date;
  private final Category category;

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
