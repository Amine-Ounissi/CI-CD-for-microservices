package com.value.buildingblocks.access;

import org.springframework.core.convert.ConversionException;

public class AccessRuleConversionException extends ConversionException {
  public AccessRuleConversionException(String message, Throwable cause) {
    super(message, cause);
  }
}
