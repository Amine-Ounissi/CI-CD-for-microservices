package com.value.authentication.tokenconverter.exception;

import com.value.authentication.tokenconverter.exception.common.TokenConverterBaseException;

public class UserinfoException extends TokenConverterBaseException {
  public UserinfoException(String error, String errorDescription) {
    setErrors(error, errorDescription);
  }
}
