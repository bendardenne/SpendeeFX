package spendee.ui.charts;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.collections.ObservableList;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.chart.PieChart;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;

import java.text.DecimalFormat;


public class DonutChart extends PieChart {

  private static final DecimalFormat FORMATTER = new DecimalFormat( "#" );

  private final Circle innerCircle = new Circle();
  private TextFlow label = new TextFlow();
  private StackPane pane = new StackPane();

  private DoubleBinding totalValue = Bindings.createDoubleBinding( () -> getData().stream()
                                                                                  .mapToDouble( Data::getPieValue )
                                                                                  .sum(), dataProperty() );

  public DonutChart() {
    innerCircle.getStyleClass().add( "inner-circle" );
    label.setTextAlignment( TextAlignment.CENTER );

    pane.getChildren().addAll( innerCircle, label );
    pane.setMouseTransparent( true );

    this.dataProperty().addListener( ( observable, oldValue, newValue ) -> {
      newValue.forEach( slice -> slice.getNode().setOnMouseEntered( e -> {
        double value = slice.getPieValue();

        Text name = new Text( slice.getName() + "\n" );
        Text amount = new Text( FORMATTER.format( value ) + "€\n" );
        Text share = new Text( FORMATTER.format( 100 * slice.getPieValue() / totalValue.get() ) + "%" );

        label.getChildren().addAll( name, amount, share );
        label.setMinWidth( innerCircle.getRadius() * 1.8 );
      } ) );

      newValue.forEach( slice -> slice.getNode().setOnMouseExited( e -> label.getChildren().clear() ) );
    } );
  }

  @Override
  protected void layoutChartChildren( double top, double left, double contentWidth, double contentHeight ) {
    super.layoutChartChildren( top, left, contentWidth, contentHeight );

    addInnerCircleIfNotPresent();
    updateInnerCircleLayout();
  }

  private void addInnerCircleIfNotPresent() {
    if ( getData().size() > 0 ) {
      Node pie = getData().get( 0 ).getNode();
      if ( pie.getParent() instanceof Pane ) {
        Pane parent = ( Pane ) pie.getParent();

        if ( !parent.getChildren().contains( pane ) ) {
          parent.getChildren().add( pane );
        }
      }
    }
  }


  private void updateInnerCircleLayout() {
    double minX = Double.MAX_VALUE, minY = Double.MAX_VALUE;
    double maxX = Double.MIN_VALUE, maxY = Double.MIN_VALUE;
    for ( PieChart.Data data : getData() ) {
      Node node = data.getNode();

      Bounds bounds = node.getBoundsInParent();
      if ( bounds.getMinX() < minX ) {
        minX = bounds.getMinX();
      }
      if ( bounds.getMinY() < minY ) {
        minY = bounds.getMinY();
      }
      if ( bounds.getMaxX() > maxX ) {
        maxX = bounds.getMaxX();
      }
      if ( bounds.getMaxY() > maxY ) {
        maxY = bounds.getMaxY();
      }
    }

    innerCircle.setRadius( ( maxX - minX ) / 3 );

    double centerX = minX + ( maxX - minX ) / 2;
    double centerY = minY + ( maxY - minY ) / 2;

    pane.setTranslateX( centerX );
    pane.setTranslateY( centerY );

    pane.toFront();
  }
}