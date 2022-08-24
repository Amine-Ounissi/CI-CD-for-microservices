package com.value.buildingblocks.backend.api.exceptionhandler;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.value.buildingblocks.configuration.filter.ExcludeFromComponentScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@ControllerAdvice
@ConditionalOnProperty(prefix = "value.api.errors", name = {
  "handle-non-api-exceptions"}, havingValue = "true", matchIfMissing = true)
@Order(40)
@ExcludeFromComponentScan
public class NonApiExceptionHandler extends AbstractApiExceptionHandler {

  private static final Logger log = LoggerFactory.getLogger(NonApiExceptionHandler.class);

  @ExceptionHandler({Exception.class})
  public ResponseEntity<ErrorResponse> onError(Exception exception) throws Exception {
    ErrorResponse error = getErrorResponse(exception);
    if (error == null) {
      log.debug("Not handling exception of type {}", exception.getClass());
      throw exception;
    }
    log.debug("Responding with {} for exception of type {}", error.getHttpStatus(),
      exception.getClass());
    logException(exception, error.getHttpStatus());
    return new ResponseEntity(error, error.getHttpStatus());
  }

  private ErrorResponse getErrorResponse(Exception ex) throws Exception {
    ResponseStatus annotatedStatus = findAnnotatedResponseStatus(ex);
    if (annotatedStatus != null) {
      String message = annotatedStatus.reason();
      if (StringUtils.isEmpty(message)) {
        message = ex.getMessage();
      }
      return new ErrorResponse(annotatedStatus.value(), message);
    }
    return determineStatusByType(ex);
  }

  private ResponseStatus findAnnotatedResponseStatus(Exception ex) {
    ResponseStatus status = (ResponseStatus) AnnotatedElementUtils
      .findMergedAnnotation(ex.getClass(), ResponseStatus.class);
    if (status != null) {
      return status;
    }
    if (ex.getCause() instanceof Exception) {
      return findAnnotatedResponseStatus((Exception) ex.getCause());
    }
    return null;
  }

  private ErrorResponse determineStatusByType(Exception ex) throws Exception {
    if (ex instanceof org.springframework.security.access.AccessDeniedException
      || ex instanceof org.springframework.security.core.AuthenticationException) {
      throw ex;
    }
    if (ex instanceof org.springframework.web.HttpRequestMethodNotSupportedException) {
      return new ErrorResponse(HttpStatus.METHOD_NOT_ALLOWED, ex.getMessage());
    }
    if (ex instanceof org.springframework.web.HttpMediaTypeNotSupportedException) {
      return new ErrorResponse(HttpStatus.UNSUPPORTED_MEDIA_TYPE, ex.getMessage());
    }
    if (ex instanceof org.springframework.web.HttpMediaTypeNotAcceptableException) {
      return new ErrorResponse(HttpStatus.NOT_ACCEPTABLE, ex.getMessage());
    }
    if (ex instanceof org.springframework.web.bind.MissingPathVariableException) {
      return new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
    }
    if (ex instanceof org.springframework.web.bind.MissingServletRequestParameterException) {
      return new ErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }
    if (ex instanceof org.springframework.web.bind.ServletRequestBindingException) {
      return new ErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }
    if (ex instanceof org.springframework.beans.ConversionNotSupportedException) {
      return new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
    }
    if (ex instanceof MethodArgumentTypeMismatchException) {
      return new ErrorResponse(HttpStatus.BAD_REQUEST,
        "Invalid value for " + ((MethodArgumentTypeMismatchException) ex)
          .getName());
    }
    if (ex instanceof org.springframework.beans.TypeMismatchException) {
      return new ErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }
    if (ex instanceof org.springframework.http.converter.HttpMessageNotReadableException) {
      return new ErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }
    if (ex instanceof org.springframework.http.converter.HttpMessageNotWritableException) {
      return new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
    }
    if (ex instanceof org.springframework.web.bind.MethodArgumentNotValidException) {
      return new ErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }
    if (ex instanceof org.springframework.web.multipart.support.MissingServletRequestPartException) {
      return new ErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }
    if (ex instanceof org.springframework.validation.BindException) {
      return new ErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }
    if (ex instanceof org.springframework.web.servlet.NoHandlerFoundException) {
      return new ErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage());
    }
    if (ex instanceof org.springframework.web.context.request.async.AsyncRequestTimeoutException) {
      return new ErrorResponse(HttpStatus.SERVICE_UNAVAILABLE, ex.getMessage());
    }
    if (ex.getCause() instanceof Exception) {
      return determineStatusByType((Exception) ex.getCause());
    }
    return new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR,
      HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
  }

  @JsonInclude(JsonInclude.Include.NON_NULL)
  @JsonPropertyOrder({"message"})
  public static class ErrorResponse {

    private final HttpStatus httpStatus;

    private final String message;

    public ErrorResponse(HttpStatus httpStatus, String message) {
      this.httpStatus = httpStatus;
      this.message = StringUtils.isEmpty(message) ? httpStatus.getReasonPhrase() : message;
    }

    @JsonIgnore
    public HttpStatus getHttpStatus() {
      return this.httpStatus;
    }

    public String getMessage() {
      return this.message;
    }
  }
}
