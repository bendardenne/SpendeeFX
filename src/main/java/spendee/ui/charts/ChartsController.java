package spendee.ui.charts;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.*;
import spendee.model.DataStore;
import spendee.model.Transaction;

import java.net.URL;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.DoubleAdder;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

public class ChartsController implements Initializable {

  @FXML private DonutChart incomeDonut;
  @FXML private DonutChart expenseDonut;

  @FXML private LineChart<String, Number> timeSeries;
  @FXML private CategoryAxis xAxis;
  @FXML private NumberAxis yAxis;

  private DataStore dataStore = DataStore.getInstance();

  @Override public void initialize( URL location, ResourceBundle resources ) {
    dataStore.getTransactions().addListener( ( ListChangeListener<? super Transaction> ) ( e ) -> {
      Stream<? extends Transaction> incomes = e.getList().stream().filter( t -> t.getAmount() > 0 );
      Stream<? extends Transaction> expenses = e.getList().stream().filter( t -> t.getAmount() < 0 );

      List<PieChart.Data> incomeData = makePieData( incomes );
      List<PieChart.Data> expenseData = makePieData( expenses );

      incomeDonut.setData( FXCollections.observableList( incomeData ) );
      expenseDonut.setData( FXCollections.observableList( expenseData ) );

      timeSeries.setData( FXCollections.singletonObservableList( makeBalanceSeries( e.getList().stream() ) ) );
    } );
  }

  private XYChart.Series<String, Number> makeBalanceSeries( Stream<? extends Transaction> aStream ) {
    XYChart.Series<String, Number> series = new XYChart.Series<>();
    series.setName( "Balance" );

    DoubleAdder acc = new DoubleAdder();

    List<Double> balances = aStream.map( i -> {
      acc.add( i.getAmount() );
      return acc.sum();
    } ).collect( Collectors.toList() );

    // TODO better X values (time-based)
    int i = 0;
    for ( double balance : balances ) {
      series.getData().add( new LineChart.Data<>( Integer.toString( i ), balance ) );
      i++;
    }

    return series;
  }

  private List<PieChart.Data> makePieData( Stream<? extends Transaction> aIncomes ) {
    Map<String, Double> amountByCategory = aIncomes
        .collect( Collectors.groupingBy(
            Transaction::getCategory,
            Collectors.summingDouble( t -> Math.abs( t.getAmount() ) ) ) );

    return amountByCategory.entrySet()
                           .stream()
                           .sorted( Comparator.comparingDouble( Map.Entry::getValue ) )
                           .map( entry -> new PieChart.Data( entry.getKey(), entry.getValue() ) )
                           .collect( toList() );
  }
}
