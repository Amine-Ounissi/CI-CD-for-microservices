package com.value.authentication.tokenconverter.exception;

import com.value.authentication.tokenconverter.exception.common.TokenConverterBaseException;
import com.value.authentication.tokenconverter.exception.common.TokenConverterException;

public class NoUserNameException extends TokenConverterBaseException implements
  TokenConverterException {
  public NoUserNameException() {
    setErrors("No username", "Unable to retrieve a Username.");
  }
}
