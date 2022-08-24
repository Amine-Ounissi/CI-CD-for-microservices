package com.value.buildingblocks.idempotency;

import org.springframework.http.HttpStatus;

abstract class DoubleSubmissionFilterException extends RuntimeException {
  public DoubleSubmissionFilterException(String message) {
    super(message, null, true, false);
  }
  
  public abstract HttpStatus getHttpStatus();
}
