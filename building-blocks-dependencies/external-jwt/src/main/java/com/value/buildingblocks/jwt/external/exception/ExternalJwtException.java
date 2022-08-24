package com.value.buildingblocks.jwt.external.exception;

public class ExternalJwtException extends Exception {
  public ExternalJwtException(String message) {
    super(message);
  }
  
  public ExternalJwtException(String message, Throwable cause) {
    super(message, cause);
  }
}
