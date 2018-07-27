package spendee.ui.filters;

import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.util.Pair;
import org.controlsfx.control.RangeSlider;
import spendee.model.Transaction;
import spendee.model.Account;
import spendee.model.filter.EFilterType;
import spendee.model.filter.Filter;

import static java.util.stream.Collectors.summarizingDouble;

public class AmountFilter implements IFilterController {

  protected static final int MAXIMUM_AMOUNT = 10000000;
  @FXML private RangeSlider amountFilter;
  @FXML private Label minAmount;
  @FXML private Label maxAmount;

  private Account account;

  public AmountFilter( Account aAccount ) {
    account = aAccount;
  }

  @Override public void initialize() {
    amountFilter.minProperty().bind( Bindings.createDoubleBinding( this::getMin, account.getUnfilteredTransactions() ) );
    amountFilter.maxProperty().bind( Bindings.createDoubleBinding( this::getMax, account.getUnfilteredTransactions() ) );

    amountFilter.setHighValue( amountFilter.getMax() );
    amountFilter.setLowValue( amountFilter.getMin() );

    // Update filter when high and low are changed.
    ChangeListener<Number> updateAmountFilter = ( observable, oldValue, newValue ) ->
        account.filter( EFilterType.AMOUNT, makeAmountFilter() );

    amountFilter.lowValueProperty().addListener( updateAmountFilter );
    amountFilter.highValueProperty().addListener( updateAmountFilter );

    minAmount.textProperty().bind( amountFilter.lowValueProperty().asString( "%.0f" ) );
    maxAmount.textProperty().bind( amountFilter.highValueProperty().asString( "%.0f" ) );
  }

  private Filter<Transaction, Pair<Double, Double>> makeAmountFilter() {
    return new Filter<>(
        t -> amountFilter.getLowValue() <= t.getAmount() && t.getAmount() <= amountFilter.getHighValue(),
        new Pair<>( amountFilter.getLowValue(), amountFilter.getHighValue() ) );
  }

  private double getMax() {
    if ( account.getUnfilteredTransactions().size() == 0 ) {
      return MAXIMUM_AMOUNT;
    }

    return account.getUnfilteredTransactions()
                  .stream()
                  .collect( summarizingDouble( Transaction::getAmount ) )
                  .getMax();
  }

  private double getMin() {
    if ( account.getUnfilteredTransactions().size() == 0 ) {
      return -MAXIMUM_AMOUNT;
    }

    return account.getUnfilteredTransactions()
                  .stream()
                  .collect( summarizingDouble( Transaction::getAmount ) )
                  .getMin();
  }

  @Override public void reset() {
    amountFilter.setHighValue( amountFilter.getMax() );
    amountFilter.setLowValue( amountFilter.getMin() );
  }
}
