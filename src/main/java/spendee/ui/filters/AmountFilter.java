package spendee.ui.filters;

import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import org.controlsfx.control.RangeSlider;
import spendee.model.EFilterType;
import spendee.model.Transaction;
import spendee.model.Wallet;

import static java.util.stream.Collectors.summarizingDouble;

public class AmountFilter implements IFilterController {

  protected static final int MAXIMUM_AMOUNT = 10000000;
  @FXML private RangeSlider amountFilter;
  @FXML private Label minAmount;
  @FXML private Label maxAmount;

  private Wallet wallet;

  public AmountFilter( Wallet aWallet ) {
    wallet = aWallet;
  }

  @Override public void initialize() {
    amountFilter.minProperty().bind( Bindings.createDoubleBinding( this::getMin, wallet.getUnfilteredTransactions() ) );

//    amountFilter.minProperty().bind( Bindings.createDoubleBinding(
//        () -> Double.MIN_VALUE,
//        wallet.getUnfilteredTransactions() ) );


//    amountFilter.maxProperty().bind( Bindings.createDoubleBinding(
//        () -> Double.MAX_VALUE,
//        wallet.getUnfilteredTransactions() ) );
//
    amountFilter.maxProperty().bind( Bindings.createDoubleBinding( this::getMax, wallet.getUnfilteredTransactions() ) );

    amountFilter.setHighValue( amountFilter.getMax() );
    amountFilter.setLowValue( amountFilter.getMin() );

    // Update filter when high and low are changed.
    ChangeListener<Number> updateAmountFilter = ( observable, oldValue, newValue ) ->
        wallet.filter( EFilterType.AMOUNT, t -> amountFilter.getLowValue() <= t.getAmount() &&
                                                t.getAmount() <= amountFilter.getHighValue() );

    amountFilter.lowValueProperty().addListener( updateAmountFilter );
    amountFilter.highValueProperty().addListener( updateAmountFilter );

    minAmount.textProperty().bind( amountFilter.lowValueProperty().asString( "%.0f" ) );
    maxAmount.textProperty().bind( amountFilter.highValueProperty().asString( "%.0f" ) );
  }

  private double getMax() {
    if ( wallet.getUnfilteredTransactions().size() == 0 ) {
      return MAXIMUM_AMOUNT;
    }

    return wallet.getUnfilteredTransactions()
                 .stream()
                 .collect( summarizingDouble( Transaction::getAmount ) )
                 .getMax();
  }

  private double getMin() {
    if ( wallet.getUnfilteredTransactions().size() == 0 ) {
      return -MAXIMUM_AMOUNT;
    }

    return wallet.getUnfilteredTransactions()
                 .stream()
                 .collect( summarizingDouble( Transaction::getAmount ) )
                 .getMin();
  }

  @Override public void reset() {
    amountFilter.setHighValue( amountFilter.getMax() );
    amountFilter.setLowValue( amountFilter.getMin() );
  }
}
