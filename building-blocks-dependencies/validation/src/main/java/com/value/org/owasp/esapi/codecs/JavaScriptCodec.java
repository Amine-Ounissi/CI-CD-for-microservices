package com.value.org.owasp.esapi.codecs;

public class JavaScriptCodec extends Codec {
  public String encodeCharacter(char[] immune, Character c) {
    if (containsCharacter(c.charValue(), immune))
      return "" + c; 
    String hex = Codec.getHexForNonAlphanumeric(c.charValue());
    if (hex == null)
      return "" + c; 
    String temp = Integer.toHexString(c.charValue());
    if (c.charValue() < 'Ā') {
      String str = "00".substring(temp.length());
      return "\\x" + str + temp.toUpperCase();
    } 
    String pad = "0000".substring(temp.length());
    return "\\u" + pad + temp.toUpperCase();
  }
  
  public Character decodeCharacter(PushbackString input) {
    input.mark();
    Character first = input.next();
    if (first == null) {
      input.reset();
      return null;
    } 
    if (first.charValue() != '\\') {
      input.reset();
      return null;
    } 
    Character second = input.next();
    if (second == null) {
      input.reset();
      return null;
    } 
    if (second.charValue() == 'b')
      return Character.valueOf('\b'); 
    if (second.charValue() == 't')
      return Character.valueOf('\t'); 
    if (second.charValue() == 'n')
      return Character.valueOf('\n'); 
    if (second.charValue() == 'v')
      return Character.valueOf('\013'); 
    if (second.charValue() == 'f')
      return Character.valueOf('\f'); 
    if (second.charValue() == 'r')
      return Character.valueOf('\r'); 
    if (second.charValue() == '"')
      return Character.valueOf('"'); 
    if (second.charValue() == '\'')
      return Character.valueOf('\''); 
    if (second.charValue() == '\\')
      return Character.valueOf('\\'); 
    if (Character.toLowerCase(second.charValue()) == 'x') {
      StringBuilder sb = new StringBuilder();
      int i;
      for (i = 0; i < 2; i++) {
        Character c = input.nextHex();
        if (c != null) {
          sb.append(c);
        } else {
          input.reset();
          return null;
        } 
      } 
      try {
        i = Integer.parseInt(sb.toString(), 16);
        if (Character.isValidCodePoint(i))
          return Character.valueOf((char)i); 
      } catch (NumberFormatException e) {
        input.reset();
        return null;
      } 
    } else if (Character.toLowerCase(second.charValue()) == 'u') {
      StringBuilder sb = new StringBuilder();
      int i;
      for (i = 0; i < 4; i++) {
        Character c = input.nextHex();
        if (c != null) {
          sb.append(c);
        } else {
          input.reset();
          return null;
        } 
      } 
      try {
        i = Integer.parseInt(sb.toString(), 16);
        if (Character.isValidCodePoint(i))
          return Character.valueOf((char)i); 
      } catch (NumberFormatException e) {
        input.reset();
        return null;
      } 
    } else if (PushbackString.isOctalDigit(second)) {
      StringBuilder sb = new StringBuilder();
      sb.append(second);
      Character c2 = input.next();
      if (!PushbackString.isOctalDigit(c2)) {
        input.pushback(c2);
      } else {
        sb.append(c2);
        Character c3 = input.next();
        if (!PushbackString.isOctalDigit(c3)) {
          input.pushback(c3);
        } else {
          sb.append(c3);
        } 
      } 
      try {
        int i = Integer.parseInt(sb.toString(), 8);
        if (Character.isValidCodePoint(i))
          return Character.valueOf((char)i); 
      } catch (NumberFormatException e) {
        input.reset();
        return null;
      } 
    } 
    return second;
  }
}
