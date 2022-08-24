package com.value.authentication.tokenconverter.exception;

import java.security.GeneralSecurityException;

public class JwsGenerationException extends RuntimeException {
  static final long serialVersionUID = 1517955991530154008L;
  
  public JwsGenerationException(GeneralSecurityException e) {
    super(e);
  }
}
