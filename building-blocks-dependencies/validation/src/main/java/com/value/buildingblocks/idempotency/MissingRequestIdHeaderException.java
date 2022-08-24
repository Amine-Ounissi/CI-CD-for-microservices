package com.value.buildingblocks.idempotency;

import org.springframework.http.HttpStatus;

public class MissingRequestIdHeaderException extends DoubleSubmissionFilterException {
  public MissingRequestIdHeaderException() {
    super("X-Request-Id header is required");
  }
  
  public HttpStatus getHttpStatus() {
    return HttpStatus.BAD_REQUEST;
  }
}
