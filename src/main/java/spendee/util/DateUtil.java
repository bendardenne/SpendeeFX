package spendee.util;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

public final class DateUtil {

  private static final SimpleDateFormat SINGLE_DAY = new SimpleDateFormat( "MMM dd" );
  private static final SimpleDateFormat WHOLE_MONTH = new SimpleDateFormat( "MMM YYYY" );

  public static LocalDate toLocalDate( Calendar aCalendar ) {
    return LocalDateTime.ofInstant( aCalendar.toInstant(), ZoneId.systemDefault() ).toLocalDate();
  }

  public static String prettyDate( List<Calendar> aCalendars ) {
    if ( aCalendars.size() == 0 ) {
      return "";
    }

    if ( aCalendars.size() == 1 ) {
      return SINGLE_DAY.format( aCalendars.get( 0 ).getTime() );
    }

    Calendar firstDay = aCalendars.get( 0 );
    Calendar lastDay = aCalendars.get( aCalendars.size() - 1 );

    long days =
        TimeUnit.DAYS.convert( lastDay.getTimeInMillis() - firstDay.getTimeInMillis(), TimeUnit.MILLISECONDS ) + 1;

    // Check whole month
    if ( firstDay.get( Calendar.DAY_OF_MONTH ) == firstDay.getActualMinimum( Calendar.DAY_OF_MONTH ) &&
         days == firstDay.getActualMaximum( Calendar.DAY_OF_MONTH ) ) {
      return WHOLE_MONTH.format( aCalendars.get( 0 ).getTime() );
    }

    if ( firstDay.get( Calendar.DAY_OF_WEEK ) == Calendar.MONDAY && aCalendars.size() == 7 ) {
      return "Week of " + SINGLE_DAY.format( aCalendars.get( 0 ).getTime() );
    }

    return days + "selected";
  }

}
