package com.value.authentication.tokenconverter.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.FORBIDDEN)
public class TokenConverterValidationException extends Exception {
  public TokenConverterValidationException(String message) {
    super(message);
  }
}
