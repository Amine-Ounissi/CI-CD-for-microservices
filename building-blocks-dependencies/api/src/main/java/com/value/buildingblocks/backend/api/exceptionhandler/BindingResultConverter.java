package com.value.buildingblocks.backend.api.exceptionhandler;

import com.value.buildingblocks.backend.api.config.ApiProperties;
import com.value.buildingblocks.presentation.errors.BadRequestException;
import com.value.buildingblocks.presentation.errors.Error;
import java.util.HashMap;
import java.util.List;
import javax.validation.constraints.Pattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.util.ClassUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

public class BindingResultConverter implements Converter<BindingResult, BadRequestException> {

  @Autowired
  private ApiProperties apiProperties;

  public BadRequestException convert(BindingResult bindingResult) {
    BadRequestException exception = new BadRequestException();
    exception.setMessage(this.apiProperties.getErrors().getMessage400());
    List<FieldError> fieldErrors = bindingResult.getFieldErrors();
    for (FieldError fieldError : fieldErrors) {
      convertFieldError(exception, fieldError);
    }
    List<ObjectError> globalErrors = bindingResult.getGlobalErrors();
    for (ObjectError objectError : globalErrors) {
      convertObjectError(exception, objectError);
    }
    return exception;
  }

  private void convertObjectError(BadRequestException exception, ObjectError objectError) {
    String code = objectError.getCode();
    String message = objectError.getDefaultMessage();
    String path = objectError.getObjectName();
    Object[] args = objectError.getArguments();
    Error error = buildError(code, message, path, args);
    exception.getErrors().add(error);
  }

  private void convertFieldError(BadRequestException exception, FieldError fieldError) {
    String code = fieldError.getCode();
    String message = fieldError.getDefaultMessage();
    String path = fieldError.getField();
    Object[] args = fieldError.getArguments();
    Error error = buildError(code, message, path, args);
    if (path.endsWith("additions[]")) {
      error.getContext().put("rejectedValue", "");
    } else {
      error.getContext().put("rejectedValue", String.valueOf(fieldError.getRejectedValue()));
    }
    exception.getErrors().add(error);
  }

  private Error buildError(String code, String message, String path, Object[] args) {
    Error error = new Error();
    error.setContext(new HashMap<>());
    error.setKey(this.apiProperties.getName() + "." + code + "." + path);
    error.setMessage(message);
    if (args != null) {
      int index = 0;
      for (Object arg : args) {
        if (ClassUtils.isPrimitiveOrWrapper(arg.getClass()) || arg instanceof String) {
          error.getContext().put("arg" + index, String.valueOf(arg));
          index++;
        }
        if (arg instanceof Pattern) {
          error.getContext().put("pattern" + index, ((Pattern) arg).regexp());
        }
      }
    }
    return error;
  }

  public ApiProperties getApiProperties() {
    return this.apiProperties;
  }

  public void setApiProperties(ApiProperties apiProperties) {
    this.apiProperties = apiProperties;
  }
}
