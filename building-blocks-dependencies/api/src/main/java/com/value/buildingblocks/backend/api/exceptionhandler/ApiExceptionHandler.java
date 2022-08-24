package com.value.buildingblocks.backend.api.exceptionhandler;

import com.value.buildingblocks.configuration.filter.ExcludeFromComponentScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
@ConditionalOnProperty(prefix = "value.api.errors", name = {
  "handle-api-exceptions"}, havingValue = "true", matchIfMissing = true)
@Order(30)
@ExcludeFromComponentScan
public class ApiExceptionHandler extends AbstractApiExceptionHandler {

  private static final Logger log = LoggerFactory.getLogger(ApiExceptionHandler.class);

  @Autowired(required = false)
  private NonApiExceptionHandler nonApiExceptionHandler;

  @ExceptionHandler({Exception.class})
  public ResponseEntity onError(Exception exception) throws Exception {
    ResponseStatus status = (ResponseStatus) AnnotatedElementUtils
      .findMergedAnnotation(exception.getClass(), ResponseStatus.class);
    log.debug("ResponseStatus for exception of type {} = {}", exception.getClass().getName(),
      status);
    if (status == null) {
      if (this.nonApiExceptionHandler != null) {
        return handleNonApiException(exception);
      }
      log.debug("Exception not handled due to configuration and being re-thrown");
      throw exception;
    }
    HttpStatus statusValue = status.value();
    logException(exception, statusValue);
    return createApiExceptionResponse(exception, statusValue);
  }

  protected ResponseEntity handleNonApiException(Exception exception) throws Exception {
    return this.nonApiExceptionHandler.onError(exception);
  }

  public NonApiExceptionHandler getNonApiExceptionHandler() {
    return this.nonApiExceptionHandler;
  }

  public void setNonApiExceptionHandler(NonApiExceptionHandler nonApiExceptionHandler) {
    this.nonApiExceptionHandler = nonApiExceptionHandler;
  }
}
