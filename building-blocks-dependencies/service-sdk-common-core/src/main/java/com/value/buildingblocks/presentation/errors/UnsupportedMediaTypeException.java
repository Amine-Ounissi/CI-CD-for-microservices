package com.value.buildingblocks.presentation.errors;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
public class UnsupportedMediaTypeException extends ErrorAwareApiException {
  private static final long serialVersionUID = 1731573938310395586L;
  
  public UnsupportedMediaTypeException() {}
  
  public UnsupportedMediaTypeException(String message) {
    super(message);
  }
  
  public UnsupportedMediaTypeException(Throwable cause) {
    super(cause);
  }
  
  public UnsupportedMediaTypeException(String message, Throwable cause) {
    super(message, cause);
  }
  
  public UnsupportedMediaTypeException withMessage(String message) {
    return (UnsupportedMediaTypeException)super.withMessage(message);
  }
  
  public UnsupportedMediaTypeException withErrors(List<Error> errors) {
    return (UnsupportedMediaTypeException)super.withErrors(errors);
  }
}
