package spendee.ui.charts;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.util.Pair;
import javafx.util.StringConverter;
import spendee.model.DataStore;
import spendee.model.Transaction;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.DoubleAdder;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

public class ChartsController {

  @FXML private DonutChart incomeDonut;
  @FXML private DonutChart expenseDonut;

  @FXML private LineChart<Number, Number> timeSeries;
  @FXML private NumberAxis xAxis;
  @FXML private NumberAxis yAxis;

  private DataStore dataStore = DataStore.getInstance();

  @FXML public void initialize() {
    ObservableList<Transaction> transactions = dataStore.getTransactions();
    updateCharts( transactions );

    transactions.addListener( ( ListChangeListener<? super Transaction> ) ( e ) -> {
      updateCharts( e.getList() );
    } );

    xAxis.setAutoRanging( true );
    xAxis.setForceZeroInRange( false );

    xAxis.setTickLabelFormatter( new StringConverter<Number>() {
      @Override public String toString( Number object ) {
        ZonedDateTime dateTime = ZonedDateTime.ofInstant( Instant.ofEpochMilli( object.longValue() ), ZoneId.systemDefault() );
        return DateTimeFormatter.ofPattern( "MMM dd" ).format( dateTime );
      }

      @Override public Number fromString( String string ) {
        return 0;  // Not used
      }
    } );

  }

  private void updateCharts( ObservableList<? extends Transaction> aTransactions ) {
    Stream<? extends Transaction> incomes = aTransactions.stream().filter( t -> t.getAmount() > 0 );
    Stream<? extends Transaction> expenses = aTransactions.stream().filter( t -> t.getAmount() < 0 );

    List<PieChart.Data> incomeData = makePieData( incomes );
    List<PieChart.Data> expenseData = makePieData( expenses );

    incomeDonut.setData( FXCollections.observableList( incomeData ) );
    expenseDonut.setData( FXCollections.observableList( expenseData ) );

    timeSeries.setData( FXCollections.singletonObservableList( makeBalanceSeries( aTransactions.stream() ) ) );
  }

  private XYChart.Series<Number, Number> makeBalanceSeries( Stream<? extends Transaction> aStream ) {
    XYChart.Series<Number, Number> series = new XYChart.Series<>();
    series.setName( "Balance" );

    DoubleAdder acc = new DoubleAdder();

    List<Pair<Number, Double>> balances = aStream.sorted( Comparator.comparing( Transaction::getDate ) ).map( i -> {
      acc.add( i.getAmount() );
      return new Pair<>( (Number) i.getDate().toInstant().toEpochMilli(), acc.sum() );
    } ).collect( Collectors.toList() );

    for ( Pair<Number, Double> balance : balances ) {
      System.out.println(balance.getKey());
      System.out.println(balance.getKey() instanceof Long);
      series.getData().add( new LineChart.Data<>( balance.getKey(), balance.getValue() ) );
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
