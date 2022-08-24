package com.value.buildingblocks.jwt.internal.exception;

import org.springframework.security.core.AuthenticationException;

public class InternalJwtAuthenticationException extends AuthenticationException {
  public InternalJwtAuthenticationException(String msg, Throwable thrw) {
    super(msg, thrw);
  }
  
  public InternalJwtAuthenticationException(String msg) {
    super(msg);
  }
}
