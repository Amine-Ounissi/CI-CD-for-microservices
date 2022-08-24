package com.value.buildingblocks.jwt.core.exception;

public class BlacklistException extends Exception {
  public BlacklistException(String message) {
    super(message);
  }
  
  public BlacklistException(String message, Throwable cause) {
    super(message, cause);
  }
}
