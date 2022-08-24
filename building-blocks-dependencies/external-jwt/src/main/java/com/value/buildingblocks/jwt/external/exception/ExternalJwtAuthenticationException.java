package com.value.buildingblocks.jwt.external.exception;

import org.springframework.security.core.AuthenticationException;

public class ExternalJwtAuthenticationException extends AuthenticationException {
  public ExternalJwtAuthenticationException(String message) {
    super(message);
  }
}
