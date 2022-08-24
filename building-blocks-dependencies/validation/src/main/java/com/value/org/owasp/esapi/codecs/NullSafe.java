package com.value.org.owasp.esapi.codecs;

public class NullSafe {
  public static boolean equals(Object a, Object b) {
    if (a == b)
      return true; 
    if (a == null)
      return (b == null); 
    if (b == null)
      return false; 
    return a.equals(b);
  }
  
  public static int hashCode(Object o) {
    if (o == null)
      return 0; 
    return o.hashCode();
  }
  
  public static String toString(Object o) {
    if (o == null)
      return "(null)"; 
    return o.toString();
  }
}
