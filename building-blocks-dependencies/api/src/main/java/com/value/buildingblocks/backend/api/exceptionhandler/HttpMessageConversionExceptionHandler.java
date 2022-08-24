package com.value.buildingblocks.backend.api.exceptionhandler;

import com.fasterxml.jackson.core.JsonLocation;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.value.buildingblocks.backend.api.config.ApiProperties;
import com.value.buildingblocks.configuration.filter.ExcludeFromComponentScan;
import com.value.buildingblocks.presentation.errors.BadRequestException;
import com.value.buildingblocks.presentation.errors.Error;
import java.util.HashMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@ConditionalOnProperty(prefix = "value.api.errors", name = {
  "handle-http-message-conversion-exception"}, havingValue = "true", matchIfMissing = true)
@Order(25)
@ExcludeFromComponentScan
public class HttpMessageConversionExceptionHandler extends AbstractApiExceptionHandler {

  public static final String KEY_API_PARSE = "api.parse.error";

  public static final String MESSAGE_API_PARSE = "Invalid Message";

  private ApiProperties apiProperties;

  @Autowired
  public HttpMessageConversionExceptionHandler(ApiProperties apiProperties) {
    this.apiProperties = apiProperties;
  }

  private Error buildError(String message, String key, JsonLocation location) {
    Error error = new Error();
    error.setMessage(message);
    error.setKey(key);
    if (error.getContext() == null) {
      error.setContext(new HashMap<>());
    }
    error.getContext().put("line", String.valueOf(location.getLineNr()));
    error.getContext().put("column", String.valueOf(location.getColumnNr()));
    return error;
  }

  @ExceptionHandler({HttpMessageConversionException.class})
  public ResponseEntity<String> onError(HttpMessageConversionException exception) {
    BadRequestException badRequestException = new BadRequestException();
    badRequestException.setMessage(this.apiProperties.getErrors().getMessage400());
    if (exception.getCause() instanceof JsonParseException) {
      JsonParseException cause = (JsonParseException) exception.getCause();
      JsonLocation location = cause.getLocation();
      Error error = buildError("Invalid Message", "api.parse.error", location);
      badRequestException.getErrors().add(error);
    }
    if (exception.getCause() instanceof JsonMappingException) {
      JsonMappingException cause = (JsonMappingException) exception.getCause();
      JsonLocation location = cause.getLocation();
      Error error = buildError("Invalid Message", "api.parse.error", location);
      badRequestException.getErrors().add(error);
    }
    logException((Exception) exception, HttpStatus.BAD_REQUEST);
    return createApiExceptionResponse((Exception) badRequestException, HttpStatus.BAD_REQUEST);
  }
}
