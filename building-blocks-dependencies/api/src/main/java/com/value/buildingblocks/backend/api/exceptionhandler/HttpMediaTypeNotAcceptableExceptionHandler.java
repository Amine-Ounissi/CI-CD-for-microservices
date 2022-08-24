package com.value.buildingblocks.backend.api.exceptionhandler;

import com.value.buildingblocks.configuration.filter.ExcludeFromComponentScan;
import com.value.buildingblocks.presentation.errors.NotAcceptableException;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.util.MimeType;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@ConditionalOnProperty(prefix = "value.api.errors", name = {
  "handle-http-media-type-not-acceptable-exception"}, havingValue = "true", matchIfMissing = true)
@Order(5)
@ExcludeFromComponentScan
public class HttpMediaTypeNotAcceptableExceptionHandler extends AbstractApiExceptionHandler {

  @ExceptionHandler({HttpMediaTypeNotAcceptableException.class})
  public ResponseEntity<String> onError(HttpMediaTypeNotAcceptableException exception) {
    NotAcceptableException notAcceptableException = (new NotAcceptableException())
      .withMessage(exception.getMessage());
    if (!CollectionUtils.isEmpty(exception.getSupportedMediaTypes())) {
      notAcceptableException.setSupportedMediaTypes((List) exception.getSupportedMediaTypes()
        .stream()
        .map(MimeType::toString)
        .collect(Collectors.toList()));
    }
    logException((Exception) exception, HttpStatus.NOT_ACCEPTABLE);
    return createApiExceptionResponse((Exception) notAcceptableException,
      HttpStatus.NOT_ACCEPTABLE);
  }
}
