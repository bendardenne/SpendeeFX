package spendee.ui;

import javafx.scene.control.MenuButton;
import javafx.scene.control.SkinBase;

public class SelectAllMenuButtonSkin extends SkinBase<SelectAllMenuButton> {

  private final MenuButton menuButton;

  public SelectAllMenuButtonSkin( SelectAllMenuButton aButton, MenuButton aMenuButton ) {
    super( aButton );
    menuButton = aMenuButton;

    menuButton.minWidthProperty().bind( aButton.minWidthProperty() );
    menuButton.minHeightProperty().bind( aButton.minHeightProperty() );

    menuButton.prefWidthProperty().bind( aButton.prefWidthProperty() );
    menuButton.prefHeightProperty().bind( aButton.prefHeightProperty() );

    menuButton.maxWidthProperty().bind( aButton.maxWidthProperty() );
    menuButton.maxHeightProperty().bind( aButton.maxHeightProperty() );

  }
}
