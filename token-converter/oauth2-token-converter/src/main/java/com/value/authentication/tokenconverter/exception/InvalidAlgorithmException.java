package com.value.authentication.tokenconverter.exception;

import com.value.authentication.tokenconverter.exception.common.TokenConverterBaseException;
import org.slf4j.event.Level;

public class InvalidAlgorithmException extends TokenConverterBaseException {
  public InvalidAlgorithmException(String errorDescription) {
    setLoggingLevel(Level.WARN);
    setErrors("Invalid Algorithm", errorDescription);
  }
}
