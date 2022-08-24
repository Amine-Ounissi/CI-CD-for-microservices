package com.value.authentication.tokenconverter.exception;

import com.value.authentication.tokenconverter.exception.common.TokenConverterBaseException;

public class InvalidUrlException extends TokenConverterBaseException {
  public InvalidUrlException(String errorDescription) {
    setErrors("Invalid URL", errorDescription);
  }
}
