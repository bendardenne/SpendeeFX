package spendee.ui.transactions;

import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class DateCellFactory<S> implements Callback<TableColumn<S, ZonedDateTime>, TableCell<S, ZonedDateTime>> {
  private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern( "MMM dd, YYYY" );

  @Override
  public TableCell<S, ZonedDateTime> call( TableColumn<S, ZonedDateTime> arg0 ) {
    return new TableCell<S, ZonedDateTime>() {
      @Override
      protected void updateItem( ZonedDateTime item, boolean empty ) {
        super.updateItem( item, empty );
        if ( item == null || empty ) {
          setGraphic( null );
        }
        else {
          setGraphic( new Label( item.format( FORMATTER ) ) );
        }
      }
    };
  }
}
