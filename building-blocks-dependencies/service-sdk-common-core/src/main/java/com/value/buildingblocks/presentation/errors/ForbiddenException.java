package com.value.buildingblocks.presentation.errors;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class ForbiddenException extends ErrorAwareApiException {
  private static final long serialVersionUID = -7046879879886810987L;
  
  public ForbiddenException() {}
  
  public ForbiddenException(String message) {
    super(message);
  }
  
  public ForbiddenException(Throwable cause) {
    super(cause);
  }
  
  public ForbiddenException(String message, Throwable cause) {
    super(message, cause);
  }
  
  public ForbiddenException withMessage(String message) {
    return (ForbiddenException)super.withMessage(message);
  }
  
  public ForbiddenException withErrors(List<Error> errors) {
    return (ForbiddenException)super.withErrors(errors);
  }
}
