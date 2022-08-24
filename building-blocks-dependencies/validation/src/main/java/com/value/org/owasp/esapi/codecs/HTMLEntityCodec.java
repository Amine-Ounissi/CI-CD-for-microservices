package com.value.org.owasp.esapi.codecs;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class HTMLEntityCodec extends Codec {
  private static final char REPLACEMENT_CHAR = '�';
  
  private static final String REPLACEMENT_HEX = "fffd";
  
  private static final String REPLACEMENT_STR = "�";
  
  private static final Map<Character, String> characterToEntityMap = mkCharacterToEntityMap();
  
  private static final Trie<Character> entityToCharacterTrie = mkEntityToCharacterTrie();
  
  public String encodeCharacter(char[] immune, Character c) {
    if (containsCharacter(c.charValue(), immune))
      return "" + c; 
    String hex = Codec.getHexForNonAlphanumeric(c.charValue());
    if (hex == null)
      return "" + c; 
    if ((c.charValue() <= '\037' && c.charValue() != '\t' && c.charValue() != '\n' && c.charValue() != '\r') || (c.charValue() >= '' && c.charValue() <= '')) {
      hex = "fffd";
      c = Character.valueOf('�');
    } 
    String entityName = characterToEntityMap.get(c);
    if (entityName != null)
      return "&" + entityName + ";"; 
    return "&#x" + hex + ";";
  }
  
  public Character decodeCharacter(PushbackString input) {
    input.mark();
    Character first = input.next();
    if (first == null) {
      input.reset();
      return null;
    } 
    if (first.charValue() != '&') {
      input.reset();
      return null;
    } 
    Character second = input.next();
    if (second == null) {
      input.reset();
      return null;
    } 
    if (second.charValue() == '#') {
      Character c = getNumericEntity(input);
      if (c != null)
        return c; 
    } else if (Character.isLetter(second.charValue())) {
      input.pushback(second);
      Character c = getNamedEntity(input);
      if (c != null)
        return c; 
    } 
    input.reset();
    return null;
  }
  
  private Character getNumericEntity(PushbackString input) {
    Character first = input.peek();
    if (first == null)
      return null; 
    if (first.charValue() == 'x' || first.charValue() == 'X') {
      input.next();
      return parseHex(input);
    } 
    return parseNumber(input);
  }
  
  private Character parseNumber(PushbackString input) {
    StringBuilder sb = new StringBuilder();
    while (input.hasNext()) {
      Character c = input.peek();
      if (Character.isDigit(c.charValue())) {
        sb.append(c);
        input.next();
        continue;
      } 
      if (c.charValue() == ';')
        input.next(); 
    } 
    try {
      int i = Integer.parseInt(sb.toString());
      if (Character.isValidCodePoint(i))
        return Character.valueOf((char)i); 
    } catch (NumberFormatException numberFormatException) {}
    return null;
  }
  
  private Character parseHex(PushbackString input) {
    StringBuilder sb = new StringBuilder();
    while (input.hasNext()) {
      Character c = input.peek();
      if ("0123456789ABCDEFabcdef".indexOf(c.charValue()) != -1) {
        sb.append(c);
        input.next();
        continue;
      } 
      if (c.charValue() == ';')
        input.next(); 
    } 
    try {
      int i = Integer.parseInt(sb.toString(), 16);
      if (Character.isValidCodePoint(i))
        return Character.valueOf((char)i); 
    } catch (NumberFormatException numberFormatException) {}
    return null;
  }
  
  private Character getNamedEntity(PushbackString input) {
    StringBuilder possible = new StringBuilder();
    int len = Math.min(input.remainder().length(), entityToCharacterTrie.getMaxKeyLength());
    int i;
    for (i = 0; i < len; i++)
      possible.append(Character.toLowerCase(input.next().charValue())); 
    Map.Entry<CharSequence, Character> entry = entityToCharacterTrie.getLongestMatch(possible);
    if (entry == null)
      return null; 
    input.reset();
    input.next();
    len = ((CharSequence)entry.getKey()).length();
    for (i = 0; i < len; i++)
      input.next(); 
    if (input.peek(';'))
      input.next(); 
    return entry.getValue();
  }
  
  private static synchronized Map<Character, String> mkCharacterToEntityMap() {
    Map<Character, String> map = new HashMap<>(252);
    map.put(Character.valueOf('"'), "quot");
    map.put(Character.valueOf('&'), "amp");
    map.put(Character.valueOf('<'), "lt");
    map.put(Character.valueOf('>'), "gt");
    map.put(Character.valueOf(' '), "nbsp");
    map.put(Character.valueOf('¡'), "iexcl");
    map.put(Character.valueOf('¢'), "cent");
    map.put(Character.valueOf('£'), "pound");
    map.put(Character.valueOf('¤'), "curren");
    map.put(Character.valueOf('¥'), "yen");
    map.put(Character.valueOf('¦'), "brvbar");
    map.put(Character.valueOf('§'), "sect");
    map.put(Character.valueOf('¨'), "uml");
    map.put(Character.valueOf('©'), "copy");
    map.put(Character.valueOf('ª'), "ordf");
    map.put(Character.valueOf('«'), "laquo");
    map.put(Character.valueOf('¬'), "not");
    map.put(Character.valueOf('­'), "shy");
    map.put(Character.valueOf('®'), "reg");
    map.put(Character.valueOf('¯'), "macr");
    map.put(Character.valueOf('°'), "deg");
    map.put(Character.valueOf('±'), "plusmn");
    map.put(Character.valueOf('²'), "sup2");
    map.put(Character.valueOf('³'), "sup3");
    map.put(Character.valueOf('´'), "acute");
    map.put(Character.valueOf('µ'), "micro");
    map.put(Character.valueOf('¶'), "para");
    map.put(Character.valueOf('·'), "middot");
    map.put(Character.valueOf('¸'), "cedil");
    map.put(Character.valueOf('¹'), "sup1");
    map.put(Character.valueOf('º'), "ordm");
    map.put(Character.valueOf('»'), "raquo");
    map.put(Character.valueOf('¼'), "frac14");
    map.put(Character.valueOf('½'), "frac12");
    map.put(Character.valueOf('¾'), "frac34");
    map.put(Character.valueOf('¿'), "iquest");
    map.put(Character.valueOf('À'), "Agrave");
    map.put(Character.valueOf('Á'), "Aacute");
    map.put(Character.valueOf('Â'), "Acirc");
    map.put(Character.valueOf('Ã'), "Atilde");
    map.put(Character.valueOf('Ä'), "Auml");
    map.put(Character.valueOf('Å'), "Aring");
    map.put(Character.valueOf('Æ'), "AElig");
    map.put(Character.valueOf('Ç'), "Ccedil");
    map.put(Character.valueOf('È'), "Egrave");
    map.put(Character.valueOf('É'), "Eacute");
    map.put(Character.valueOf('Ê'), "Ecirc");
    map.put(Character.valueOf('Ë'), "Euml");
    map.put(Character.valueOf('Ì'), "Igrave");
    map.put(Character.valueOf('Í'), "Iacute");
    map.put(Character.valueOf('Î'), "Icirc");
    map.put(Character.valueOf('Ï'), "Iuml");
    map.put(Character.valueOf('Ð'), "ETH");
    map.put(Character.valueOf('Ñ'), "Ntilde");
    map.put(Character.valueOf('Ò'), "Ograve");
    map.put(Character.valueOf('Ó'), "Oacute");
    map.put(Character.valueOf('Ô'), "Ocirc");
    map.put(Character.valueOf('Õ'), "Otilde");
    map.put(Character.valueOf('Ö'), "Ouml");
    map.put(Character.valueOf('×'), "times");
    map.put(Character.valueOf('Ø'), "Oslash");
    map.put(Character.valueOf('Ù'), "Ugrave");
    map.put(Character.valueOf('Ú'), "Uacute");
    map.put(Character.valueOf('Û'), "Ucirc");
    map.put(Character.valueOf('Ü'), "Uuml");
    map.put(Character.valueOf('Ý'), "Yacute");
    map.put(Character.valueOf('Þ'), "THORN");
    map.put(Character.valueOf('ß'), "szlig");
    map.put(Character.valueOf('à'), "agrave");
    map.put(Character.valueOf('á'), "aacute");
    map.put(Character.valueOf('â'), "acirc");
    map.put(Character.valueOf('ã'), "atilde");
    map.put(Character.valueOf('ä'), "auml");
    map.put(Character.valueOf('å'), "aring");
    map.put(Character.valueOf('æ'), "aelig");
    map.put(Character.valueOf('ç'), "ccedil");
    map.put(Character.valueOf('è'), "egrave");
    map.put(Character.valueOf('é'), "eacute");
    map.put(Character.valueOf('ê'), "ecirc");
    map.put(Character.valueOf('ë'), "euml");
    map.put(Character.valueOf('ì'), "igrave");
    map.put(Character.valueOf('í'), "iacute");
    map.put(Character.valueOf('î'), "icirc");
    map.put(Character.valueOf('ï'), "iuml");
    map.put(Character.valueOf('ð'), "eth");
    map.put(Character.valueOf('ñ'), "ntilde");
    map.put(Character.valueOf('ò'), "ograve");
    map.put(Character.valueOf('ó'), "oacute");
    map.put(Character.valueOf('ô'), "ocirc");
    map.put(Character.valueOf('õ'), "otilde");
    map.put(Character.valueOf('ö'), "ouml");
    map.put(Character.valueOf('÷'), "divide");
    map.put(Character.valueOf('ø'), "oslash");
    map.put(Character.valueOf('ù'), "ugrave");
    map.put(Character.valueOf('ú'), "uacute");
    map.put(Character.valueOf('û'), "ucirc");
    map.put(Character.valueOf('ü'), "uuml");
    map.put(Character.valueOf('ý'), "yacute");
    map.put(Character.valueOf('þ'), "thorn");
    map.put(Character.valueOf('ÿ'), "yuml");
    map.put(Character.valueOf('Œ'), "OElig");
    map.put(Character.valueOf('œ'), "oelig");
    map.put(Character.valueOf('Š'), "Scaron");
    map.put(Character.valueOf('š'), "scaron");
    map.put(Character.valueOf('Ÿ'), "Yuml");
    map.put(Character.valueOf('ƒ'), "fnof");
    map.put(Character.valueOf('ˆ'), "circ");
    map.put(Character.valueOf('˜'), "tilde");
    map.put(Character.valueOf('Α'), "Alpha");
    map.put(Character.valueOf('Β'), "Beta");
    map.put(Character.valueOf('Γ'), "Gamma");
    map.put(Character.valueOf('Δ'), "Delta");
    map.put(Character.valueOf('Ε'), "Epsilon");
    map.put(Character.valueOf('Ζ'), "Zeta");
    map.put(Character.valueOf('Η'), "Eta");
    map.put(Character.valueOf('Θ'), "Theta");
    map.put(Character.valueOf('Ι'), "Iota");
    map.put(Character.valueOf('Κ'), "Kappa");
    map.put(Character.valueOf('Λ'), "Lambda");
    map.put(Character.valueOf('Μ'), "Mu");
    map.put(Character.valueOf('Ν'), "Nu");
    map.put(Character.valueOf('Ξ'), "Xi");
    map.put(Character.valueOf('Ο'), "Omicron");
    map.put(Character.valueOf('Π'), "Pi");
    map.put(Character.valueOf('Ρ'), "Rho");
    map.put(Character.valueOf('Σ'), "Sigma");
    map.put(Character.valueOf('Τ'), "Tau");
    map.put(Character.valueOf('Υ'), "Upsilon");
    map.put(Character.valueOf('Φ'), "Phi");
    map.put(Character.valueOf('Χ'), "Chi");
    map.put(Character.valueOf('Ψ'), "Psi");
    map.put(Character.valueOf('Ω'), "Omega");
    map.put(Character.valueOf('α'), "alpha");
    map.put(Character.valueOf('β'), "beta");
    map.put(Character.valueOf('γ'), "gamma");
    map.put(Character.valueOf('δ'), "delta");
    map.put(Character.valueOf('ε'), "epsilon");
    map.put(Character.valueOf('ζ'), "zeta");
    map.put(Character.valueOf('η'), "eta");
    map.put(Character.valueOf('θ'), "theta");
    map.put(Character.valueOf('ι'), "iota");
    map.put(Character.valueOf('κ'), "kappa");
    map.put(Character.valueOf('λ'), "lambda");
    map.put(Character.valueOf('μ'), "mu");
    map.put(Character.valueOf('ν'), "nu");
    map.put(Character.valueOf('ξ'), "xi");
    map.put(Character.valueOf('ο'), "omicron");
    map.put(Character.valueOf('π'), "pi");
    map.put(Character.valueOf('ρ'), "rho");
    map.put(Character.valueOf('ς'), "sigmaf");
    map.put(Character.valueOf('σ'), "sigma");
    map.put(Character.valueOf('τ'), "tau");
    map.put(Character.valueOf('υ'), "upsilon");
    map.put(Character.valueOf('φ'), "phi");
    map.put(Character.valueOf('χ'), "chi");
    map.put(Character.valueOf('ψ'), "psi");
    map.put(Character.valueOf('ω'), "omega");
    map.put(Character.valueOf('ϑ'), "thetasym");
    map.put(Character.valueOf('ϒ'), "upsih");
    map.put(Character.valueOf('ϖ'), "piv");
    map.put(Character.valueOf(' '), "ensp");
    map.put(Character.valueOf(' '), "emsp");
    map.put(Character.valueOf(' '), "thinsp");
    map.put(Character.valueOf('‌'), "zwnj");
    map.put(Character.valueOf('‍'), "zwj");
    map.put(Character.valueOf('‎'), "lrm");
    map.put(Character.valueOf('‏'), "rlm");
    map.put(Character.valueOf('–'), "ndash");
    map.put(Character.valueOf('—'), "mdash");
    map.put(Character.valueOf('‘'), "lsquo");
    map.put(Character.valueOf('’'), "rsquo");
    map.put(Character.valueOf('‚'), "sbquo");
    map.put(Character.valueOf('“'), "ldquo");
    map.put(Character.valueOf('”'), "rdquo");
    map.put(Character.valueOf('„'), "bdquo");
    map.put(Character.valueOf('†'), "dagger");
    map.put(Character.valueOf('‡'), "Dagger");
    map.put(Character.valueOf('•'), "bull");
    map.put(Character.valueOf('…'), "hellip");
    map.put(Character.valueOf('‰'), "permil");
    map.put(Character.valueOf('′'), "prime");
    map.put(Character.valueOf('″'), "Prime");
    map.put(Character.valueOf('‹'), "lsaquo");
    map.put(Character.valueOf('›'), "rsaquo");
    map.put(Character.valueOf('‾'), "oline");
    map.put(Character.valueOf('⁄'), "frasl");
    map.put(Character.valueOf('€'), "euro");
    map.put(Character.valueOf('ℑ'), "image");
    map.put(Character.valueOf('℘'), "weierp");
    map.put(Character.valueOf('ℜ'), "real");
    map.put(Character.valueOf('™'), "trade");
    map.put(Character.valueOf('ℵ'), "alefsym");
    map.put(Character.valueOf('←'), "larr");
    map.put(Character.valueOf('↑'), "uarr");
    map.put(Character.valueOf('→'), "rarr");
    map.put(Character.valueOf('↓'), "darr");
    map.put(Character.valueOf('↔'), "harr");
    map.put(Character.valueOf('↵'), "crarr");
    map.put(Character.valueOf('⇐'), "lArr");
    map.put(Character.valueOf('⇑'), "uArr");
    map.put(Character.valueOf('⇒'), "rArr");
    map.put(Character.valueOf('⇓'), "dArr");
    map.put(Character.valueOf('⇔'), "hArr");
    map.put(Character.valueOf('∀'), "forall");
    map.put(Character.valueOf('∂'), "part");
    map.put(Character.valueOf('∃'), "exist");
    map.put(Character.valueOf('∅'), "empty");
    map.put(Character.valueOf('∇'), "nabla");
    map.put(Character.valueOf('∈'), "isin");
    map.put(Character.valueOf('∉'), "notin");
    map.put(Character.valueOf('∋'), "ni");
    map.put(Character.valueOf('∏'), "prod");
    map.put(Character.valueOf('∑'), "sum");
    map.put(Character.valueOf('−'), "minus");
    map.put(Character.valueOf('∗'), "lowast");
    map.put(Character.valueOf('√'), "radic");
    map.put(Character.valueOf('∝'), "prop");
    map.put(Character.valueOf('∞'), "infin");
    map.put(Character.valueOf('∠'), "ang");
    map.put(Character.valueOf('∧'), "and");
    map.put(Character.valueOf('∨'), "or");
    map.put(Character.valueOf('∩'), "cap");
    map.put(Character.valueOf('∪'), "cup");
    map.put(Character.valueOf('∫'), "int");
    map.put(Character.valueOf('∴'), "there4");
    map.put(Character.valueOf('∼'), "sim");
    map.put(Character.valueOf('≅'), "cong");
    map.put(Character.valueOf('≈'), "asymp");
    map.put(Character.valueOf('≠'), "ne");
    map.put(Character.valueOf('≡'), "equiv");
    map.put(Character.valueOf('≤'), "le");
    map.put(Character.valueOf('≥'), "ge");
    map.put(Character.valueOf('⊂'), "sub");
    map.put(Character.valueOf('⊃'), "sup");
    map.put(Character.valueOf('⊄'), "nsub");
    map.put(Character.valueOf('⊆'), "sube");
    map.put(Character.valueOf('⊇'), "supe");
    map.put(Character.valueOf('⊕'), "oplus");
    map.put(Character.valueOf('⊗'), "otimes");
    map.put(Character.valueOf('⊥'), "perp");
    map.put(Character.valueOf('⋅'), "sdot");
    map.put(Character.valueOf('⌈'), "lceil");
    map.put(Character.valueOf('⌉'), "rceil");
    map.put(Character.valueOf('⌊'), "lfloor");
    map.put(Character.valueOf('⌋'), "rfloor");
    map.put(Character.valueOf('〈'), "lang");
    map.put(Character.valueOf('〉'), "rang");
    map.put(Character.valueOf('◊'), "loz");
    map.put(Character.valueOf('♠'), "spades");
    map.put(Character.valueOf('♣'), "clubs");
    map.put(Character.valueOf('♥'), "hearts");
    map.put(Character.valueOf('♦'), "diams");
    return Collections.unmodifiableMap(map);
  }
  
  private static synchronized Trie<Character> mkEntityToCharacterTrie() {
    Trie<Character> trie = new HashTrie<>();
    for (Map.Entry<Character, String> entry : characterToEntityMap.entrySet())
      trie.put(entry.getValue(), entry.getKey()); 
    return Trie.Util.unmodifiable(trie);
  }
}
