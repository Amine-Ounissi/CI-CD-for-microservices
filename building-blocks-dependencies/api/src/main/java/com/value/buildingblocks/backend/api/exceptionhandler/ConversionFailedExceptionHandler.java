package com.value.buildingblocks.backend.api.exceptionhandler;

import com.value.buildingblocks.configuration.filter.ExcludeFromComponentScan;
import com.value.buildingblocks.presentation.errors.BadRequestException;
import com.value.buildingblocks.presentation.errors.Error;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.annotation.Order;
import org.springframework.core.convert.ConversionFailedException;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@ConditionalOnProperty(prefix = "value.api.errors", name = {
  "handle-conversion-failed-exception"}, havingValue = "true", matchIfMissing = true)
@Order(7)
@ExcludeFromComponentScan
public class ConversionFailedExceptionHandler extends AbstractApiExceptionHandler {

  @ExceptionHandler({ConversionFailedException.class})
  public ResponseEntity<String> handleTypeConversionErrors(
    ConversionFailedException conversionFailedException) {
    Map<String, String> contextMap = new HashMap<>();
    contextMap.put("rejectedValue", String.valueOf(conversionFailedException.getValue()));
    if (conversionFailedException.getTargetType().hasAnnotation(DateTimeFormat.class)) {
      contextMap.put("pattern", ((DateTimeFormat) conversionFailedException
        .getTargetType().getAnnotation(DateTimeFormat.class)).pattern());
    }
    List<Error> errors = new ArrayList<>();
    errors.add(createError("Invalid Input", "api.parse.error", contextMap));
    BadRequestException badRequestException = new BadRequestException(
      HttpStatus.BAD_REQUEST.getReasonPhrase());
    badRequestException.setErrors(errors);
    logException((Exception) badRequestException, HttpStatus.BAD_REQUEST);
    return createApiExceptionResponse((Exception) badRequestException, HttpStatus.BAD_REQUEST);
  }

  private Error createError(String message, String key, Map<String, String> context) {
    return new Error(key, message, context);
  }
}
