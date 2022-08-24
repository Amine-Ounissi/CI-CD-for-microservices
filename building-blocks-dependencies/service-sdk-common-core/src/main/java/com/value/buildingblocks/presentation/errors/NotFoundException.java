package com.value.buildingblocks.presentation.errors;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class NotFoundException extends ErrorAwareApiException {
  private static final long serialVersionUID = -893199051016137726L;
  
  public NotFoundException() {}
  
  public NotFoundException(String message) {
    super(message);
  }
  
  public NotFoundException(Throwable cause) {
    super(cause);
  }
  
  public NotFoundException(String message, Throwable cause) {
    super(message, cause);
  }
  
  public NotFoundException withMessage(String message) {
    return (NotFoundException)super.withMessage(message);
  }
  
  public NotFoundException withErrors(List<Error> errors) {
    return (NotFoundException)super.withErrors(errors);
  }
}
