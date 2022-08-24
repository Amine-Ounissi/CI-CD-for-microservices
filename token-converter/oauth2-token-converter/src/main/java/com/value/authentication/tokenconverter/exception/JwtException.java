package com.value.authentication.tokenconverter.exception;

import com.value.authentication.tokenconverter.exception.common.TokenConverterBaseException;

public class JwtException extends TokenConverterBaseException {
  public JwtException(String errorDescription) {
    setErrors("token_error", errorDescription);
  }
}
