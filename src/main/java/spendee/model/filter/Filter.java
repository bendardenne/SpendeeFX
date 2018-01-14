package spendee.model.filter;

import spendee.model.Transaction;

import java.util.function.Predicate;

/**
 * Slightly enriched Predicate that also stores the predicate "parameters", i.e. on what accepted values the predicate
 * filters.
 * For instance, for a predicate like t -> t == 5;  the "parameter" would be 5.
 */
public class Filter<T, U> implements Predicate<T> {

  private final Predicate<T> predicate;
  private final U accepted;

  public Filter( Predicate<T> aPredicate, U aAccepted ) {
    predicate = aPredicate;
    accepted = aAccepted;
  }

  public Predicate getPredicate() {
    return predicate;
  }

  public U getAccepted() {
    return accepted;
  }

  @Override public boolean test( T aT ) {
    return predicate.test( aT );
  }

  @Override public String toString() {
    return accepted.toString();
  }

  public static <U> Filter<Transaction, U> acceptAll( U aAccepted ) {
    return new Filter<>( t -> true, aAccepted );
  }
}
