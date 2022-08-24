package com.value.buildingblocks.jwt.core.exception;

public class JsonWebTokenException extends Exception {
  public JsonWebTokenException(String message) {
    super(message);
  }
  
  public JsonWebTokenException(String message, Throwable cause) {
    super(message, cause);
  }
  
  public JsonWebTokenException(Throwable cause) {
    super(cause);
  }
}
