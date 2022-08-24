package com.value.authentication.tokenconverter.exception;

import com.value.authentication.tokenconverter.exception.common.TokenConverterBaseException;
import com.value.authentication.tokenconverter.exception.common.TokenConverterException;
import org.slf4j.event.Level;

public class NotWhitelistedException extends TokenConverterBaseException implements
  TokenConverterException {
  public NotWhitelistedException(String errorDescription) {
    setErrors("Not Allowed", errorDescription);
    setLoggingLevel(Level.WARN);
  }
}
