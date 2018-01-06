package spendee.ui;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

public class ColorProvider {

  private static final Map<String, Color> COLORS = new HashMap<>();

  static {
    COLORS.put( "Salary", Color.decode( "#71C643" ) );
    COLORS.put( "Meal Vouchers", Color.decode( "#BED940" ) );
    COLORS.put( "Reimbursement", Color.decode( "#18B272" ) );
    COLORS.put( "BlaBlaCar", Color.decode( "#47A7E6" ) );

    COLORS.put( "Car", Color.decode( "#47A7E6" ) );
    COLORS.put( "Savings", Color.decode( "#6D6E89" ) );
    COLORS.put( "Rent", Color.decode( "#DE8135" ) );
    COLORS.put( "Gifts", Color.decode( "#F5534B" ) );
    COLORS.put( "Groceries", Color.decode( "#B47B55" ) );
    COLORS.put( "Take Away & Restaurant", Color.decode( "#B9965E" ) );
    COLORS.put( "Drinks", Color.decode( "#DF8C29" ) );
    COLORS.put( "Brewing", Color.decode( "#18B272" ) );
    COLORS.put( "Utilities", Color.decode( "#61708C" ) );
    COLORS.put( "Transportation", Color.decode( "#FFCC00" ) );
    COLORS.put( "Concerts", Color.decode( "#60CFCB" ) );
    COLORS.put( "Music", Color.decode( "#FFA800" ) );
    COLORS.put( "Reading", Color.decode( "#5CC5AC" ) );

  }

  private ColorProvider() {

  }

  public static Color getColor( String aCategory ) {
    return COLORS.getOrDefault( aCategory, Color.RED );
  }

}
