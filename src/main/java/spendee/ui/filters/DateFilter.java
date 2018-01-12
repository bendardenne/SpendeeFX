package spendee.ui.filters;

import javafx.beans.binding.Bindings;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import jfxtras.scene.control.CalendarPicker;
import spendee.model.DataStore;
import spendee.model.EFilterType;
import spendee.util.DateUtil;

import java.util.*;

public class DateFilter implements IFilterController {

  @FXML private CalendarPicker dateFilter;
  @FXML private Hyperlink thisMonth;
  @FXML private Hyperlink thisYear;
  @FXML private Hyperlink allTime;
  @FXML private Label dateLabel;

  private DataStore dataStore = DataStore.getInstance();

  @Override public void initialize() {
    dateLabel.textProperty().bind( Bindings.createStringBinding(
        () -> DateUtil.prettyDate( dateFilter.calendars() ), dateFilter.calendars() ) );

    dateFilter.calendars().addListener( ( ListChangeListener<Calendar> ) c -> {
      ObservableList<? extends Calendar> dates = c.getList().sorted();
      if ( dates.size() == 0 ) {
        dataStore.filter( EFilterType.DATE, t -> true );
      }
      else {
        dataStore.filter( EFilterType.DATE, t -> dates.stream().map( DateUtil::toLocalDate )
                                                      .anyMatch( t.getDate().toLocalDate()::equals ));
      }
    } );

    thisMonth.setOnAction( e -> dateFilter.calendars().setAll( getCurrentMonth() ) );
    thisYear.setOnAction( e -> dateFilter.calendars().setAll( getCurrentYear() ) );
    allTime.setOnAction( e -> reset() );
  }

  private Collection<Calendar> getCurrentYear() {
    Calendar calendar = Calendar.getInstance( );
    List<Calendar> calendars = new ArrayList<>(  );

    for( int i = 1; i <= calendar.getActualMaximum( Calendar.DAY_OF_YEAR ); i++ ){
      calendar.set(Calendar.DAY_OF_YEAR, i );
      calendars.add( ( Calendar ) calendar.clone() );
    }

    return calendars;
  }

   private Collection<Calendar> getCurrentMonth() {
    Calendar calendar = Calendar.getInstance( );
    List<Calendar> calendars = new ArrayList<>(  );

    for( int i = 1; i <= calendar.getActualMaximum( Calendar.DAY_OF_MONTH ); i++ ){
      calendar.set(Calendar.DAY_OF_MONTH, i );
      calendars.add( ( Calendar ) calendar.clone() );
    }

    return calendars;
  }

  @Override public void reset() {
    dateFilter.calendars().clear();
  }
}
