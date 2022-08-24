package com.value.org.owasp.esapi.codecs;

public class Hex {
  public static String toHex(byte[] b, boolean leading0x) {
    StringBuffer hexString = new StringBuffer();
    if (leading0x)
      hexString.append("0x"); 
    for (int i = 0; i < b.length; i++) {
      int j = b[i] & 0xFF;
      String hex = Integer.toHexString(j);
      if (hex.length() == 1)
        hexString.append('0'); 
      hexString.append(hex);
    } 
    return hexString.toString();
  }
  
  public static String encode(byte[] b, boolean leading0x) {
    return toHex(b, leading0x);
  }
  
  public static byte[] fromHex(String hexStr) {
    String hexRep = hexStr;
    if (hexStr.startsWith("0x"))
      hexRep = hexStr.substring(2); 
    int len = hexRep.length() / 2;
    byte[] rawBytes = new byte[len];
    for (int i = 0; i < len; i++) {
      String substr = hexRep.substring(i * 2, i * 2 + 2);
      rawBytes[i] = (byte)Integer.parseInt(substr, 16);
    } 
    return rawBytes;
  }
  
  public static byte[] decode(String hexStr) {
    return fromHex(hexStr);
  }
}
