package spendee.model;

public class Category {

  public enum Type {INCOME, EXPENSE, TRANSFER}

  private String name;
  private Type type;

  public Category( String aName, Type aType ){
    name = aName;
    type = aType;
  }

  public String getName() {
    return name;
  }

  public Type getType() {
    return type;
  }

  @Override public String toString() {
    return name;
  }

  @Override public boolean equals( Object obj ) {
    if( obj instanceof Category ) {
      Category other = ( Category ) obj;
      return type == other.type && name.equals( other.name);
    }

    return false;
  }

  @Override public int hashCode() {
    return name.hashCode() + type.hashCode();
  }
}
