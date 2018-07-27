package spendee;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import jfxtras.fxml.JFXtrasBuilderFactory;
import spendee.model.Account;

import java.lang.reflect.Constructor;

public class Main extends Application {

  @Override
  public void start( Stage primaryStage ) throws Exception {
    Account account = new Account();

    FXMLLoader loader = new FXMLLoader( Main.class.getClassLoader().getResource( "ui/fxml/main.fxml" ),
                                        null, new JFXtrasBuilderFactory() );

    loader.setControllerFactory( type -> {
      try {
        // Poor man's DI
        for ( Constructor<?> constructor : type.getConstructors() ) {
          if ( constructor.getParameterCount() == 1 && constructor.getParameterTypes()[0] == Account.class ) {
            return constructor.newInstance( account );
          }
        }

        return type.newInstance();
      }
      catch ( Exception aE ) {
        aE.printStackTrace();
        return null;
      }
    } );

    Parent root = loader.load();

    primaryStage.setTitle( "SpendeeFX" );
    Scene scene = new Scene( root, 300, 275 );

    primaryStage.setScene( scene );
    primaryStage.show();
  }


  public static void main( String[] args ) {
    launch( args );
  }
}
