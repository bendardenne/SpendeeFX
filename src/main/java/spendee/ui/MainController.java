package spendee.ui;

import javafx.fxml.FXML;
import spendee.ui.filters.FiltersController;

public class MainController {

  @FXML private StatusController statusController;
  @FXML private FiltersController filtersController;
  @FXML private MenubarController menubarController;

  @FXML public void initialize( ) {
    filtersController.setStatusController( statusController );
    menubarController.setStatusController( statusController );
  }

}
