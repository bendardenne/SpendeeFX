package spendee.ui.filters;

import javafx.beans.binding.Bindings;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import jfxtras.scene.control.CalendarPicker;
import spendee.model.Transaction;
import spendee.model.Wallet;
import spendee.model.filter.EFilterType;
import spendee.model.filter.Filter;
import spendee.util.DateUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;

public class DateFilter implements IFilterController {

  @FXML private CalendarPicker dateFilter;
  @FXML private Hyperlink thisMonth;
  @FXML private Hyperlink thisYear;
  @FXML private Hyperlink lastMonth;
  @FXML private Hyperlink lastYear;
  @FXML private Hyperlink allTime;
  @FXML private Label dateLabel;

  private Wallet wallet;

  public DateFilter( Wallet aWallet ) {
    wallet = aWallet;
  }

  @Override public void initialize() {
    ObservableList<Calendar> selectedDates = dateFilter.calendars();
    dateLabel.textProperty().bind( Bindings.createStringBinding(
        () -> DateUtil.prettyDate( selectedDates.sorted() ), selectedDates ) );

    selectedDates.addListener( ( ListChangeListener<Calendar> ) c -> {
      ObservableList<? extends Calendar> dates = c.getList().sorted();
      if ( dates.size() == 0 ) {
        wallet.filter( EFilterType.DATE, Filter.acceptAll( new ArrayList<>() ) );
      }
      else {
        wallet.filter( EFilterType.DATE, makeFilter( dates ) );
      }
    } );

    int currentMonth = Calendar.getInstance().get( Calendar.MONTH );
    int currentYear = Calendar.getInstance().get( Calendar.YEAR );

    thisMonth.setOnAction( e -> selectedDates.setAll( getMonthList( currentMonth ) ) );
    thisYear.setOnAction( e -> selectedDates.setAll( getYearList( currentYear ) ) );
    lastMonth.setOnAction( e -> selectedDates.setAll( getMonthList( currentMonth - 1 ) ) );
    lastYear.setOnAction( e -> selectedDates.setAll( getYearList( currentYear - 1 ) ) );

    allTime.setOnAction( e -> reset() );
  }

  private Filter<Transaction, Object> makeFilter( List<? extends Calendar> aDates ) {
    return new Filter<>( t -> aDates.stream()
                                    .map( DateUtil::toLocalDate )
                                    .anyMatch( t.getDate().toLocalDate()::equals ),
                         aDates );
  }

  private Collection<Calendar> getYearList( int aYear ) {
    Calendar calendar = Calendar.getInstance();
    calendar.set( Calendar.YEAR, aYear );
    List<Calendar> calendars = new ArrayList<>();

    for ( int i = 1; i <= calendar.getActualMaximum( Calendar.DAY_OF_YEAR ); i++ ) {
      calendar.set( Calendar.DAY_OF_YEAR, i );
      calendars.add( ( Calendar ) calendar.clone() );
    }

    return calendars;
  }

  private Collection<Calendar> getMonthList( int aMonth ) {
    Calendar calendar = Calendar.getInstance();
    calendar.set( Calendar.MONTH, aMonth );
    List<Calendar> calendars = new ArrayList<>();

    for ( int i = 1; i <= calendar.getActualMaximum( Calendar.DAY_OF_MONTH ); i++ ) {
      calendar.set( Calendar.DAY_OF_MONTH, i );
      calendars.add( ( Calendar ) calendar.clone() );
    }

    return calendars;
  }

  @Override public void reset() {
    dateFilter.calendars().clear();
  }
}
