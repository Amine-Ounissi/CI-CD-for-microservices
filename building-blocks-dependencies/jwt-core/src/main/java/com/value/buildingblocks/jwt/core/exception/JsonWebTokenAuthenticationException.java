package com.value.buildingblocks.jwt.core.exception;

import org.springframework.security.core.AuthenticationException;

public class JsonWebTokenAuthenticationException extends AuthenticationException {
  public JsonWebTokenAuthenticationException(String message) {
    super(message);
  }
  
  public JsonWebTokenAuthenticationException(String message, Throwable cause) {
    super(message, cause);
  }
}
