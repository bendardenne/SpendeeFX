package spendee.ui.filters;

import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import org.controlsfx.control.RangeSlider;
import spendee.model.DataStore;
import spendee.model.EFilterType;
import spendee.model.Transaction;

import static java.util.stream.Collectors.summarizingDouble;

public class AmountFilter implements IFilterController{

  @FXML private RangeSlider amountFilter;
  @FXML private Label minAmount;
  @FXML private Label maxAmount;

  private DataStore dataStore = DataStore.getInstance();

  @Override public void initialize() {
    amountFilter.minProperty().bind( Bindings.createDoubleBinding(
        () -> dataStore.getUnfilteredTransactions().stream().collect( summarizingDouble( Transaction::getAmount ) )
                       .getMin(), dataStore.getUnfilteredTransactions() ) );
    amountFilter.maxProperty().bind( Bindings.createDoubleBinding(
        () -> dataStore.getUnfilteredTransactions().stream().collect( summarizingDouble(
            Transaction::getAmount ) ).getMax(), dataStore.getUnfilteredTransactions() ) );

    amountFilter.setHighValue( amountFilter.getMax() );
    amountFilter.setLowValue( amountFilter.getMin() );

    // Update filter when high and low are changed.
    ChangeListener<Number> updateAmountFilter = ( observable, oldValue, newValue ) ->
        dataStore.filter( EFilterType.AMOUNT, t -> amountFilter.getLowValue() <= t.getAmount() &&
                                                   t.getAmount() <= amountFilter.getHighValue() );

    amountFilter.lowValueProperty().addListener( updateAmountFilter );
    amountFilter.highValueProperty().addListener( updateAmountFilter );

    minAmount.textProperty().bind( amountFilter.lowValueProperty().asString( "%.2f" ) );
    maxAmount.textProperty().bind( amountFilter.highValueProperty().asString( "%.2f" ) );
  }

  @Override public void reset() {
    amountFilter.setHighValue( amountFilter.getMax() );
    amountFilter.setLowValue( amountFilter.getMin() );
  }
}
