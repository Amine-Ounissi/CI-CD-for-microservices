package com.value.buildingblocks.presentation.errors;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class InternalServerErrorException extends ApiErrorException {
  private static final long serialVersionUID = 2193661356663904667L;
  
  public InternalServerErrorException(String message, Throwable cause) {
    super(message, cause);
  }
  
  public InternalServerErrorException(String message) {
    super(message);
  }
  
  public InternalServerErrorException(Throwable cause) {
    super(cause);
  }
  
  public InternalServerErrorException() {}
  
  public InternalServerErrorException withMessage(String message) {
    return (InternalServerErrorException)super.withMessage(message);
  }
}
