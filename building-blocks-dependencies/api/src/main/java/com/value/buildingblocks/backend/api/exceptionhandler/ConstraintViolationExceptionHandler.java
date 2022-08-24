package com.value.buildingblocks.backend.api.exceptionhandler;

import com.value.buildingblocks.configuration.filter.ExcludeFromComponentScan;
import com.value.buildingblocks.presentation.errors.BadRequestException;
import javax.validation.ConstraintViolationException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.annotation.Order;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@ConditionalOnProperty(prefix = "value.api.errors", name = {
  "handle-constraint-violation-exception"}, havingValue = "true", matchIfMissing = true)
@Order(13)
@ExcludeFromComponentScan
public class ConstraintViolationExceptionHandler extends AbstractApiExceptionHandler {

  private Converter<ConstraintViolationException, BadRequestException> constraintViolationConverter;

  public ConstraintViolationExceptionHandler(
    Converter<ConstraintViolationException, BadRequestException> constraintViolationConverter) {
    this.constraintViolationConverter = constraintViolationConverter;
  }

  @ExceptionHandler({ConstraintViolationException.class})
  public ResponseEntity<String> onError(ConstraintViolationException exception) {
    BadRequestException badRequestException = (BadRequestException) this.constraintViolationConverter
      .convert(exception);
    logException((Exception) exception, HttpStatus.BAD_REQUEST);
    return createApiExceptionResponse((Exception) badRequestException, HttpStatus.BAD_REQUEST);
  }
}
