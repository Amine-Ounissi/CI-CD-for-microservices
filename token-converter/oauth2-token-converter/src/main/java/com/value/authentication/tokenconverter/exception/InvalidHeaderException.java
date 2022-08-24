package com.value.authentication.tokenconverter.exception;

import com.value.authentication.tokenconverter.exception.common.TokenConverterBaseException;

public class InvalidHeaderException extends TokenConverterBaseException {
  public InvalidHeaderException() {
    setErrors("Invalid Authorization Header", "The header should be in the format: Bearer <token>");
  }
}
