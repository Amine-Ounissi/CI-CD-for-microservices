package com.value.org.owasp.esapi.codecs;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultEncoder implements Encoder {
  private static volatile Encoder singletonInstance;
  
  public static Encoder getInstance() {
    if (singletonInstance == null)
      synchronized (DefaultEncoder.class) {
        if (singletonInstance == null)
          singletonInstance = new DefaultEncoder(); 
      }  
    return singletonInstance;
  }
  
  private List codecs = new ArrayList();
  
  private HTMLEntityCodec htmlCodec = new HTMLEntityCodec();
  
  private PercentCodec percentCodec = new PercentCodec();
  
  private JavaScriptCodec javaScriptCodec = new JavaScriptCodec();
  
  private static final Logger logger = LoggerFactory.getLogger(Encoder.class);
  
  private static final char[] IMMUNE_HTML = new char[] { ',', '.', '-', '_', ' ' };
  
  private static final char[] IMMUNE_HTMLATTR = new char[] { ',', '.', '-', '_' };
  
  private static final char[] IMMUNE_CSS = new char[0];
  
  private static final char[] IMMUNE_JAVASCRIPT = new char[] { ',', '.', '_' };
  
  private static final char[] IMMUNE_VBSCRIPT = new char[] { ',', '.', '_' };
  
  private static final char[] IMMUNE_XML = new char[] { ',', '.', '-', '_', ' ' };
  
  private static final char[] IMMUNE_SQL = new char[] { ' ' };
  
  private static final char[] IMMUNE_OS = new char[] { '-' };
  
  private static final char[] IMMUNE_XMLATTR = new char[] { ',', '.', '-', '_' };
  
  private static final char[] IMMUNE_XPATH = new char[] { ',', '.', '-', '_', ' ' };
  
  private DefaultEncoder() {
    this.codecs.add(this.htmlCodec);
    this.codecs.add(this.percentCodec);
    this.codecs.add(this.javaScriptCodec);
  }
  
  public String canonicalize(String input, boolean strict) {
    return canonicalize(input, strict, strict);
  }
  
  public String canonicalize(String input, boolean restrictMultiple, boolean restrictMixed) {
    if (input == null)
      return null; 
    String working = input;
    Codec codecFound = null;
    int mixedCount = 1;
    int foundCount = 0;
    boolean clean = false;
    while (!clean) {
      clean = true;
      Iterator<Codec> i = this.codecs.iterator();
      while (i.hasNext()) {
        Codec codec = i.next();
        String old = working;
        working = codec.decode(working);
        if (!old.equals(working)) {
          if (codecFound != null && codecFound != codec)
            mixedCount++; 
          codecFound = codec;
          if (clean)
            foundCount++; 
          clean = false;
        } 
      } 
    } 
    if (foundCount >= 2 && mixedCount > 1) {
      if (restrictMultiple || restrictMixed)
        throw new IntrusionException("Input validation failure", "Multiple (" + foundCount + "x) and mixed encoding (" + mixedCount + "x) detected in " + input); 
      logger.warn("Failure in ESAPI Encoder: Multiple ({}x) and mixed encoding ({}x) detected in {}", new Object[] { Integer.valueOf(foundCount), Integer.valueOf(mixedCount), input });
    } else if (foundCount >= 2) {
      if (restrictMultiple)
        throw new IntrusionException("Input validation failure", "Multiple (" + foundCount + "x) encoding detected in " + input); 
      logger.warn("Failure in ESAPI Encoder: Multiple ({}x) encoding detected in {}", Integer.valueOf(foundCount), input);
    } else if (mixedCount > 1) {
      if (restrictMixed)
        throw new IntrusionException("Input validation failure", "Mixed encoding (" + mixedCount + "x) detected in " + input); 
      logger.warn("Failure in ESAPI Encoder: Mixed encoding ({}x) detected in {}", Integer.valueOf(mixedCount), input);
    } 
    return working;
  }
}
