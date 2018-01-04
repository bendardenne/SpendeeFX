package spendee;

import javafx.util.Pair;

/**
 * Application-wide preferences.
 * Set of (key, default value) pairs for settings to be stored within the Preferences API.
 */
public class Preferences {

  // Last file opened by the application (to try and restore on restart).
  public static final Pair<String, String> OPEN_FILE = new Pair<>( "open-file", null );

}
