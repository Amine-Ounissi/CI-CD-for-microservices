package com.value.buildingblocks.presentation.errors;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class UnauthorizedException extends ErrorAwareApiException {
  private static final long serialVersionUID = 8399029240686442375L;
  
  public UnauthorizedException() {}
  
  public UnauthorizedException(String message) {
    super(message);
  }
  
  public UnauthorizedException(Throwable cause) {
    super(cause);
  }
  
  public UnauthorizedException(String message, Throwable cause) {
    super(message, cause);
  }
  
  public UnauthorizedException withMessage(String message) {
    return (UnauthorizedException)super.withMessage(message);
  }
  
  public UnauthorizedException withErrors(List<Error> errors) {
    return (UnauthorizedException)super.withErrors(errors);
  }
}
