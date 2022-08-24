package com.value.authentication.tokenconverter.exception;

import com.value.authentication.tokenconverter.exception.common.TokenConverterBaseException;

public class NotFoundException extends TokenConverterBaseException {

  public NotFoundException() {
    setErrors("Not Found", "The userinfo request returned a not found error.");
  }
}
