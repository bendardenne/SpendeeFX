package spendee.ui.charts;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;
import javafx.util.Pair;
import javafx.util.StringConverter;
import org.gillius.jfxutils.chart.ChartPanManager;
import org.gillius.jfxutils.chart.JFXChartUtil;
import spendee.model.Category;
import spendee.model.DataStore;
import spendee.model.Transaction;
import spendee.ui.CssClassProvider;

import java.text.DecimalFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.DoubleAdder;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

public class ChartsController {

  private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern( "MMM dd" );
  private static final DecimalFormat AMOUNT_FORMATTER = new DecimalFormat( "+#####€;-#####€" );

  @FXML private DonutChart incomeDonut;
  @FXML private DonutChart expenseDonut;

  @FXML private LineChart<Long, Double> timeSeries;
  @FXML private NumberAxis xAxis;
  @FXML private NumberAxis yAxis;

  private DataStore dataStore = DataStore.getInstance();

  @FXML public void initialize() {
    ObservableList<Transaction> transactions = dataStore.getTransactions();
    updateCharts( transactions );

    transactions.addListener( ( ListChangeListener<? super Transaction> ) ( e ) -> updateCharts( e.getList() ) );

    xAxis.setAutoRanging( true );
    xAxis.setForceZeroInRange( false );

    // Use FXUtils Stable Axis thing ??
    xAxis.setTickLabelFormatter( new StringConverter<Number>() {
      @Override public String toString( Number object ) {
        ZonedDateTime dateTime = ZonedDateTime
            .ofInstant( Instant.ofEpochMilli( object.longValue() ), ZoneId.systemDefault() );
        return DATE_TIME_FORMATTER.format( dateTime );
      }

      @Override public Number fromString( String string ) {
        return 0;  // Not used
      }
    } );

    // Zoom only with mouse wheel
    JFXChartUtil.setupZooming( timeSeries, mouseEvent -> {
      if ( mouseEvent.getButton() != MouseButton.MIDDLE ) {
        mouseEvent.consume();
      }
    } );

    timeSeries.setOnScroll( e -> {
      ObservableList<XYChart.Data<Long, Double>> dataPoint = timeSeries.getData().get( 0 ).getData();

      // Prevent zooming out further than makes sense
      if ( e.getDeltaY() < 0 && dataPoint.size() > 0 ) {
        //FIXME not correct
        if ( xAxis.getLowerBound() <= dataPoint.get( 0 ).getXValue() &&
             xAxis.getUpperBound() >= dataPoint.get( dataPoint.size() - 1 ).getXValue() &&
             yAxis.getLowerBound() <= dataPoint.get( 0 ).getYValue() &&
             yAxis.getUpperBound() >= dataPoint.get( dataPoint.size() - 1 ).getYValue() ) {
          e.consume();
        }
      }
    } );

    ChartPanManager panManager = new ChartPanManager( timeSeries );
    panManager.start();
  }

  private void updateCharts( ObservableList<? extends Transaction> aTransactions ) {
    Stream<? extends Transaction> incomes = aTransactions.stream().filter( t -> t.getAmount() > 0 );
    Stream<? extends Transaction> expenses = aTransactions.stream().filter( t -> t.getAmount() < 0 );

    List<PieChart.Data> incomeData = makePieData( incomes );
    List<PieChart.Data> expenseData = makePieData( expenses );


    incomeDonut.setData( FXCollections.observableList( incomeData ) );
    expenseDonut.setData( FXCollections.observableList( expenseData ) );

    incomeData.forEach(
        slice -> slice.getNode().getStyleClass().add( CssClassProvider.getCssClass( slice.getName() ) ) );

    expenseData.forEach(
        slice -> slice.getNode().getStyleClass().add( CssClassProvider.getCssClass( slice.getName() ) ) );

    timeSeries.setData( FXCollections.singletonObservableList( makeBalanceSeries( aTransactions.stream() ) ) );
  }

  private XYChart.Series<Long, Double> makeBalanceSeries( Stream<? extends Transaction> aStream ) {
    XYChart.Series<Long, Double> series = new XYChart.Series<>();

    DoubleAdder acc = new DoubleAdder();

    List<Pair<Transaction, Double>> balances = aStream.sorted( Comparator.comparing( Transaction::getDate ) )
                                                      .map( transaction -> {
                                                        acc.add( transaction.getAmount() );
                                                        return new Pair<Transaction, Double>( transaction, acc.sum() );
                                                      } ).collect( Collectors.toList() );

    for ( Pair<Transaction, Double> balance : balances ) {
      Transaction transaction = balance.getKey();
      XYChart.Data<Long, Double> dataPoint = new LineChart.Data<>( transaction.getDate().toInstant().toEpochMilli(),
                                                                   balance.getValue() );
      dataPoint.setNode( new HoverNode( transaction, balance.getValue() ) );
      series.getData().add( dataPoint );
    }

    return series;
  }

  private List<PieChart.Data> makePieData( Stream<? extends Transaction> aIncomes ) {
    Map<Category, Double> amountByCategory = aIncomes
        .collect( Collectors.groupingBy(
            Transaction::getCategory,
            Collectors.summingDouble( t -> Math.abs( t.getAmount() ) ) ) );

    return amountByCategory.entrySet()
                           .stream()
                           .sorted( Collections.reverseOrder( Comparator.comparingDouble( Map.Entry::getValue ) ) )
                           .map( entry -> new PieChart.Data( entry.getKey().getName(), entry.getValue() ) )
                           .collect( toList() );
  }

  private class HoverNode extends StackPane {

    private TextFlow label = new TextFlow();

    private HoverNode( Transaction aTransaction, double aValue ) {
      createLabel( aTransaction, aValue );

      setPrefSize( 10, 10 );

      String transactionColor = aTransaction.getAmount() < 0 ? "default-color6" : "default-color2";

      getStyleClass().addAll( transactionColor, "chart-line-symbol", "chart-series-line" );
      label.getStyleClass().addAll( transactionColor, "chart-line-symbol", "chart-series-line" );

      setOnMouseEntered( event -> {
        getChildren().setAll( label );
        toFront();
      } );

      setOnMouseExited( e -> getChildren().clear() );
    }

    private void createLabel( Transaction aTransaction, double aValue ) {
      Text category = new Text( aTransaction.getCategory().getName() + "\n" );
      category.getStyleClass().add( "line-name-label" );

      Text date = new Text( DATE_TIME_FORMATTER.format( aTransaction.getDate() ) + "\n" );
      date.getStyleClass().add( "line-date-label" );

      Text amount = new Text( AMOUNT_FORMATTER.format( aTransaction.getAmount() ) + "\n" );
      amount.getStyleClass().add( aTransaction.getCategory().getType().toString().toLowerCase() );

      Text note = new Text( aTransaction.getNote().isEmpty() ? "" : aTransaction.getNote() + "\n" );

      Separator separator = new Separator();

      Text resultingBalance = new Text( AMOUNT_FORMATTER.format( aValue ) );
      resultingBalance.getStyleClass().add( aValue > 0 ? "income" : "expense" );

      separator.setPrefWidth( resultingBalance.getBoundsInParent().getWidth() );

      label.getChildren().addAll( category, date, note, amount,
                                  separator, new Text( "\n" ),
                                  resultingBalance );

      label.setLineSpacing( 5 );
      label.setPadding( new Insets( 10, 10, 10, 10 ) );
      label.setTextAlignment( TextAlignment.CENTER );
      label.setMinSize( Label.USE_PREF_SIZE, Label.USE_PREF_SIZE );
      label.setTranslateY( 70 );
    }
  }
}
