package spendee.ui;

import java.util.HashMap;
import java.util.Map;

/**
 * Provide the colours from java so that we can tell when a colour for a category is missing.
 */
public final class ColourProvider {
  private static final Map<String, String> COLOR_MAP = new HashMap<>();

  static {
    COLOR_MAP.put( "Salary", "#71C643" );
    COLOR_MAP.put( "Meal Vouchers", "#BED940" );
    COLOR_MAP.put( "Reimbursement", "#18B272" );
    COLOR_MAP.put( "Covoiturage", "#47A7E6" );
    COLOR_MAP.put( "Extra income", "#1EADCE" );

    COLOR_MAP.put( "Car", "#47A7E6" );
    COLOR_MAP.put( "Savings", "#6D6E89" );
    COLOR_MAP.put( "Rent", "#DE8135" );
    COLOR_MAP.put( "Home", "#DE8135" );
    COLOR_MAP.put( "Gifts", "#F5534B" );
    COLOR_MAP.put( "Groceries", "#B47B55" );
    COLOR_MAP.put( "Take Away & Restaurant", "#B9965E" );
    COLOR_MAP.put( "Snacks & Coffee Shops", "#FFCC00" );
    COLOR_MAP.put( "Drinks", "#DF8C29" );
    COLOR_MAP.put( "Beer", "#F5534B" );
    COLOR_MAP.put( "Brewing", "#18B272" );
    COLOR_MAP.put( "Utilities", "#61708C" );
    COLOR_MAP.put( "Transportation", "#FFCC00" );
    COLOR_MAP.put( "Concerts", "#60CFCB" );
    COLOR_MAP.put( "Music", "#FFA800" );
    COLOR_MAP.put( "Reading", "#5CC5AC" );
    COLOR_MAP.put( "Games", "#3D75AB" );
    COLOR_MAP.put( "Cinema", "#47A7E6" );
    COLOR_MAP.put( "Accommodation", "#6E8CB1" );
    COLOR_MAP.put( "Travel", "#F963A0" );
    COLOR_MAP.put( "Tourism", "#DE8135" );
    COLOR_MAP.put( "Family & Personal", "#F5534B" );
    COLOR_MAP.put( "Healthcare", "#E56274" );
    COLOR_MAP.put( "Clothing", "#BED940" );

    COLOR_MAP.put( "Other", "#7945D0" );
  }

  public static String getColourString( String aName ) {
      String color  = COLOR_MAP.getOrDefault( aName, "#FFFFFF" );
      if( "#FFFFFF".equals( color ) ){
        // TODO use a logger and warn
        System.out.println( "Color unknown for: " + aName );
      }
      return color;
  }

  private ColourProvider() {}
}
