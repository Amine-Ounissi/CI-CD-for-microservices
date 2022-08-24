package com.value.authentication.tokenconverter.exception.common;

import org.slf4j.event.Level;

public abstract class TokenConverterBaseException extends RuntimeException implements TokenConverterException {
  private final transient ErrorSet errorSet = new ErrorSet();
  
  private Level loggingLevel = Level.DEBUG;
  
  public ErrorSet getErrorSet() {
    return this.errorSet;
  }
  
  protected void setErrors(String error, String errorDescription) {
    this.errorSet.setError(error);
    this.errorSet.setErrorDescription(errorDescription);
  }
  
  public Level getLoggingLevel() {
    return this.loggingLevel;
  }
  
  public void setLoggingLevel(Level loggingLevel) {
    this.loggingLevel = loggingLevel;
  }
}
