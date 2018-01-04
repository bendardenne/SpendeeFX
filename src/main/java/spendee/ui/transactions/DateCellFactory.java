package spendee.ui.transactions;

import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateCellFactory<S> implements Callback<TableColumn<S, LocalDateTime>, TableCell<S, LocalDateTime>> {
  private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern( "MMM dd, YYYY" );

  @Override
  public TableCell<S, LocalDateTime> call( TableColumn<S, LocalDateTime> arg0 ) {
    return new TableCell<S, LocalDateTime>() {
      @Override
      protected void updateItem( LocalDateTime item, boolean empty ) {
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
