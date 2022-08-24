package com.value.authentication.tokenconverter.exception;

import com.value.authentication.tokenconverter.exception.common.TokenConverterBaseException;

public class InvalidIssuerException extends TokenConverterBaseException {
  public InvalidIssuerException() {
    setErrors("Invalid Issuer", "The token must have a valid issuer");
  }
}
