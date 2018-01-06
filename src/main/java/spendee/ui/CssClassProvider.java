package spendee.ui;

public final class CssClassProvider {

  private CssClassProvider() {

  }

  public static String getCssClass( String aIdentifier ) {
    String cssClass = aIdentifier.toLowerCase();
    cssClass = cssClass.replaceAll( "\\s", "-" );
    cssClass = cssClass.replaceAll( "[^a-z\\-]", "" );
    cssClass = cssClass.replaceAll( "--+", "-" );
    return cssClass;
  }
}
