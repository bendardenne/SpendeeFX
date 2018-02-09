package spendee.ui.transactions;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;
import org.controlsfx.control.HyperlinkLabel;
import spendee.model.Transaction;
import spendee.model.Wallet;
import spendee.model.filter.EFilterType;
import spendee.model.filter.Filter;
import spendee.util.HashtagUtil;

import java.util.Collection;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class NoteCellFactory implements Callback<TableColumn<Transaction, String>, TableCell<Transaction, String>> {

  private Wallet wallet;
  private EventHandler<ActionEvent> linkClicked = event -> {
    String hashtag = ( ( Hyperlink ) event.getSource() ).getText();
    wallet.filter( EFilterType.NOTE, makeRegexFilter( hashtag ) );
  };

  public NoteCellFactory( Wallet aWallet ) {
    wallet = aWallet;
  }

  @Override public TableCell<Transaction, String> call( TableColumn<Transaction, String> param ) {
    return new TableCell<Transaction, String>() {
      @Override protected void updateItem( String item, boolean empty ) {
        super.updateItem( item, empty );

        if ( empty || item == null ) {
          setText( null );
          setGraphic( null );
          return;
        }

        Collection<String> hashtags = HashtagUtil.extractHashtags( item );
        String[] split = item.split( "\\s+" );

        StringBuilder sb = new StringBuilder();

        for ( String s : split ) {
          if ( hashtags.contains( s ) ) {
            sb.append( " [" ).append( s ).append( "] " );
          }
          else {
            sb.append( s ).append( ' ' );
          }
        }

        HyperlinkLabel label = new HyperlinkLabel( sb.toString() );
        label.setOnAction( linkClicked );
        setGraphic( label );
      }
    };
  }


  private Filter<Transaction, String> makeRegexFilter( String newValue ) {
    Predicate<Transaction> predicate = t -> {
      try {
        return Pattern.compile( newValue, Pattern.CASE_INSENSITIVE ).matcher( t.getNote() ).find();
      }
      catch ( PatternSyntaxException ignored ) {
      }

      return true;
    };

    return new Filter<>( predicate, newValue );
  }
}
