package com.value.buildingblocks.backend.api.exceptionhandler;

import com.value.buildingblocks.backend.api.config.ApiProperties;
import com.value.buildingblocks.presentation.errors.BadRequestException;
import com.value.buildingblocks.presentation.errors.Error;
import java.util.HashMap;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import org.springframework.core.convert.converter.Converter;

public class ConstraintViolationConverter implements
  Converter<ConstraintViolationException, BadRequestException> {

  private ApiProperties apiProperties;

  public ConstraintViolationConverter(ApiProperties apiProperties) {
    this.apiProperties = apiProperties;
  }

  public BadRequestException convert(ConstraintViolationException constraintViolationException) {
    BadRequestException exception = new BadRequestException();
    exception.setMessage(this.apiProperties.getErrors().getMessage400());
    if (constraintViolationException.getConstraintViolations() != null) {
      for (ConstraintViolation<?> constraintViolation : (Iterable<ConstraintViolation<?>>) constraintViolationException
        .getConstraintViolations()) {
        exception.getErrors().add(buildError(constraintViolation));
      }
    }
    return exception;
  }

  private Error buildError(ConstraintViolation<?> constraintViolation) {
    Error error = new Error(
      (constraintViolation.getPropertyPath() != null) ? constraintViolation.getPropertyPath()
        .toString() : "", constraintViolation.getMessage(), new HashMap<>());
    error.getContext().put("rejectedValue", String.valueOf(constraintViolation.getInvalidValue()));
    return error;
  }
}
