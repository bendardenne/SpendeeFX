package spendee.ui;

import javafx.application.Platform;
import javafx.fxml.FXML;
import org.controlsfx.control.StatusBar;

import java.util.Timer;
import java.util.TimerTask;

public class StatusController {

  @FXML private StatusBar statusBar;

  private Timer hideMessage;

  public StatusController() {
    hideMessage = new Timer( "hide status message" );
  }

  public void message( String text ) {
    statusBar.setText( text );
    hideMessage.cancel();


    hideMessage = new Timer( "hide status message" );
    hideMessage.schedule( new TimerTask() {
      @Override public void run() {
        Platform.runLater( () -> statusBar.setText( "" ) );
      }
    }, 5000 );
  }

}
