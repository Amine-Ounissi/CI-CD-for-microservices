package com.value.buildingblocks.idempotency;

import org.springframework.http.HttpStatus;

public class DuplicateRequestException extends DoubleSubmissionFilterException {
  public DuplicateRequestException(String requestId) {
    super("Duplicate X-Request-Id header value: " + requestId);
  }
  
  public HttpStatus getHttpStatus() {
    return HttpStatus.CONFLICT;
  }
}
