package com.value.org.owasp.esapi.codecs;

public class IntrusionException extends RuntimeException {
  private static final long serialVersionUID = 1L;
  
  public IntrusionException(String message, String message2) {
    super(message + " " + message2);
  }
}
