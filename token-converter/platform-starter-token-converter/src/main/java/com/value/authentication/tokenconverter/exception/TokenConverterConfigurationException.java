package com.value.authentication.tokenconverter.exception;

public class TokenConverterConfigurationException extends RuntimeException {
  static final long serialVersionUID = 2988154832204122642L;
  
  public TokenConverterConfigurationException(String message) {
    super(message);
  }
  
  public TokenConverterConfigurationException(Exception e) {
    super(e);
  }
}
