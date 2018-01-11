package spendee;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import jfxtras.fxml.JFXtrasBuilderFactory;

public class Main extends Application {

  @Override
  public void start( Stage primaryStage ) throws Exception {
    Parent root = FXMLLoader.load( Main.class.getClassLoader().getResource( "ui/fxml/main.fxml" ),
                                   null, new JFXtrasBuilderFactory() ) ;

    primaryStage.setTitle( "SpendeeFX" );
    Scene scene = new Scene( root, 300, 275 );

    primaryStage.setScene( scene );
    primaryStage.show();
  }


  public static void main( String[] args ) {
    launch( args );
  }
}
