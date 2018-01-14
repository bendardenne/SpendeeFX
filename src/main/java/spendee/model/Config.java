package spendee.model;

import javafx.beans.property.SimpleDoubleProperty;

public class Config {

  private SimpleDoubleProperty initialWallet = new SimpleDoubleProperty( this, "initialWallet" );

  public SimpleDoubleProperty initialWalletProperty() {
    return initialWallet;
  }

  public double getInitialWallet() {
    return initialWallet.get();
  }

  public void setInitialWallet( double aInitialWallet ) {
    this.initialWallet.set( aInitialWallet );
  }



}
