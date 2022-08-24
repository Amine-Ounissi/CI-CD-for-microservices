package com.value.org.owasp.esapi.codecs;

import java.io.UnsupportedEncodingException;
import java.util.Set;
import java.util.stream.Collectors;

public class PercentCodec extends Codec {
  private static final String ALPHA_NUMERIC_STR = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
  
  private static final String RFC3986_RESERVED_STR = ":/?#[]@!$&'()*+,;=";
  
  private static final String RFC3986_NON_ALPHANUMERIC_UNRESERVED_STR = "-._~";
  
  private static final boolean ENCODED_NON_ALPHA_NUMERIC_UNRESERVED = true;
  
  private static final String UNENCODED_STR = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
  
  private static final Set<Character> UNENCODED_SET;
  
  static {
    UNENCODED_SET = (Set<Character>)"abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".chars().mapToObj(e -> Character.valueOf((char)e)).collect(Collectors.toSet());
  }
  
  private static byte[] toUtf8Bytes(String str) {
    try {
      return str.getBytes("UTF-8");
    } catch (UnsupportedEncodingException e) {
      throw new IllegalStateException("The Java spec requires UTF-8 support.", e);
    } 
  }
  
  private static StringBuilder appendTwoUpperHex(StringBuilder sb, int b) {
    if (b < -128 || b > 127)
      throw new IllegalArgumentException("b is not a byte (was " + b + ')'); 
    b &= 0xFF;
    if (b < 16)
      sb.append('0'); 
    return sb.append(Integer.toHexString(b).toUpperCase());
  }
  
  public String encodeCharacter(char[] immune, Character c) {
    String cStr = String.valueOf(c.charValue());
    if (immune != null && containsCharacter(c.charValue(), immune))
      return cStr; 
    if (UNENCODED_SET.contains(c))
      return cStr; 
    byte[] bytes = toUtf8Bytes(cStr);
    StringBuilder sb = new StringBuilder(bytes.length * 3);
    for (byte b : bytes)
      appendTwoUpperHex(sb.append('%'), b); 
    return sb.toString();
  }
  
  public Character decodeCharacter(PushbackString input) {
    input.mark();
    Character first = input.next();
    if (first == null) {
      input.reset();
      return null;
    } 
    if (first.charValue() != '%') {
      input.reset();
      return null;
    } 
    StringBuilder sb = new StringBuilder();
    int i;
    for (i = 0; i < 2; i++) {
      Character c = input.nextHex();
      if (c != null)
        sb.append(c); 
    } 
    if (sb.length() == 2)
      try {
        i = Integer.parseInt(sb.toString(), 16);
        if (Character.isValidCodePoint(i))
          return Character.valueOf((char)i); 
      } catch (NumberFormatException numberFormatException) {} 
    input.reset();
    return null;
  }
}
