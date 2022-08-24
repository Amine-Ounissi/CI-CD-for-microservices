package com.value.authentication.tokenconverter.exception;

import com.value.authentication.tokenconverter.exception.common.TokenConverterBaseException;

public class ServerErrorException extends TokenConverterBaseException {
  public ServerErrorException() {
    setErrors("Authentication server error", "The Authentication server has returned an error");
  }
}
