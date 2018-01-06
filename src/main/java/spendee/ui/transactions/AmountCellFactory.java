package spendee.ui.transactions;

import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;


public class AmountCellFactory<S> implements Callback<TableColumn<S, Double>, TableCell<S, Double>> {

  @Override public TableCell<S, Double> call( TableColumn<S, Double> param ) {
    return new TableCell<S, Double>() {
      @Override protected void updateItem( Double item, boolean empty ) {
        super.updateItem( item, empty );

        if ( empty || item == null ) {
          setText( null );
          setGraphic( null );
        }
        else {
          getStyleClass().removeAll( "cell-amount-positive", "cell-amount-negative" );
          getStyleClass().add( item < 0 ? "cell-amount-negative" : "cell-amount-positive" );
          setText( item.toString() );
        }
      }
    };
  }
}
