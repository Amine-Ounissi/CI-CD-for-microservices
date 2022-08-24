package com.value.buildingblocks.jwt.core.exception;

public class TokenKeyException extends RuntimeException {
  public TokenKeyException(String message) {
    super(message);
  }
  
  public TokenKeyException(String message, Throwable cause) {
    super(message, cause);
  }
}
