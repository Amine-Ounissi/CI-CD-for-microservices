package com.value.buildingblocks.backend.api.exceptionhandler;

import com.value.buildingblocks.configuration.filter.ExcludeFromComponentScan;
import com.value.buildingblocks.presentation.errors.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.annotation.Order;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@ConditionalOnProperty(prefix = "value.api.errors", name = {
  "handle-method-argument-not-valid-exception"}, havingValue = "true", matchIfMissing = true)
@Order(20)
@ExcludeFromComponentScan
public class MethodArgumentNotValidExceptionHandler extends AbstractApiExceptionHandler {

  @Autowired
  private Converter<BindingResult, BadRequestException> bindingResultConverter;

  @ExceptionHandler({MethodArgumentNotValidException.class})
  public ResponseEntity<String> onError(MethodArgumentNotValidException exception) {
    BadRequestException badRequestException = (BadRequestException) this.bindingResultConverter
      .convert(exception.getBindingResult());
    logException((Exception) exception, HttpStatus.BAD_REQUEST);
    return createApiExceptionResponse((Exception) badRequestException, HttpStatus.BAD_REQUEST);
  }

  public Converter<BindingResult, BadRequestException> getBindingResultConverter() {
    return this.bindingResultConverter;
  }

  public void setBindingResultConverter(
    Converter<BindingResult, BadRequestException> bindingResultConverter) {
    this.bindingResultConverter = bindingResultConverter;
  }
}
