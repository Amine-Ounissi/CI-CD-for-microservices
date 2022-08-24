package com.value.buildingblocks.jwt.internal.exception;

public class InternalJwtException extends Exception {
  public InternalJwtException(String message) {
    super(message);
  }
  
  public InternalJwtException(String message, Throwable cause) {
    super(message, cause);
  }
}
