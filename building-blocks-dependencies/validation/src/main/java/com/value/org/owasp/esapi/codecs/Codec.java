package com.value.org.owasp.esapi.codecs;

public abstract class Codec {
  private static final String[] hex = new String[256];
  
  static {
    for (char c = Character.MIN_VALUE; c < 'ÿ'; c = (char)(c + 1)) {
      if ((c >= '0' && c <= '9') || (c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z')) {
        hex[c] = null;
      } else {
        hex[c] = toHex(c).intern();
      } 
    } 
  }
  
  public String encode(char[] immune, String input) {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < input.length(); i++) {
      char c = input.charAt(i);
      sb.append(encodeCharacter(immune, Character.valueOf(c)));
    } 
    return sb.toString();
  }
  
  public String encodeCharacter(char[] immune, Character c) {
    return "" + c;
  }
  
  public String decode(String input) {
    StringBuilder sb = new StringBuilder();
    PushbackString pbs = new PushbackString(input);
    while (pbs.hasNext()) {
      Character c = decodeCharacter(pbs);
      if (c != null) {
        sb.append(c);
        continue;
      } 
      sb.append(pbs.next());
    } 
    return sb.toString();
  }
  
  public Character decodeCharacter(PushbackString input) {
    return input.next();
  }
  
  public static String getHexForNonAlphanumeric(char c) {
    if (c < 'ÿ')
      return hex[c]; 
    return toHex(c);
  }
  
  public static String toOctal(char c) {
    return Integer.toOctalString(c);
  }
  
  public static String toHex(char c) {
    return Integer.toHexString(c);
  }
  
  public static boolean containsCharacter(char c, char[] array) {
    for (char ch : array) {
      if (c == ch)
        return true; 
    } 
    return false;
  }
}
