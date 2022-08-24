package com.value.buildingblocks.context;

public final class ContextConstants {
  public static final String CONTEXT_MARKER = "@";
  
  private static final class EmptyContextQualifier implements ContextQualifier {
    private final String[] context = new String[0];
    
    public boolean equals(Object obj) {
      if (this == obj)
        return true; 
      if (obj == null)
        return false; 
      if (getClass() != obj.getClass())
        return false; 
      ContextQualifier other = (ContextQualifier)obj;
      return "".equals(other.getKey());
    }
    
    public final String[] getContext() {
      return this.context;
    }
    
    public final String getKey() {
      return "";
    }
    
    public int hashCode() {
      return "".hashCode();
    }
    
    public String toString() {
      return "empty";
    }
    
    private EmptyContextQualifier() {}
  }
  
  public static final ContextQualifier EMPTY_CONTEXT_QUALIFIER = new EmptyContextQualifier();
  
  private ContextConstants() {
    throw new AssertionError("Cannot instantiate constants class");
  }
}
