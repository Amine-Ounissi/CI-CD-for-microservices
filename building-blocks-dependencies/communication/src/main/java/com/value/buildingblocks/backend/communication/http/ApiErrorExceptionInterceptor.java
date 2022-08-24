package com.value.buildingblocks.backend.communication.http;

import com.value.buildingblocks.presentation.errors.ApiErrorException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.client.HttpMessageConverterExtractor;

public class ApiErrorExceptionInterceptor implements ClientHttpRequestInterceptor {
  private static final Logger log = LoggerFactory.getLogger(ApiErrorExceptionInterceptor.class);
  
  private final HttpStatus status;
  
  private final Class<? extends ApiErrorException> exceptionClass;
  
  private List<HttpMessageConverter<?>> messageConverters;
  
  public ApiErrorExceptionInterceptor(HttpStatus status, Class<? extends ApiErrorException> exceptionClass, List<HttpMessageConverter<?>> messageConverters) {
    this.status = status;
    this.exceptionClass = exceptionClass;
    this.messageConverters = messageConverters;
  }
  
  public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
    boolean enabled = Boolean.parseBoolean(request
        .getHeaders().getFirst("X-Intercept-Errors"));
    ClientHttpResponse response = execution.execute(request, body);
    if (enabled && response.getStatusCode() == this.status) {
      log.debug("{} is intercepting response", this);
      ApiErrorException exception = null;
      try {
        exception = extract(this.exceptionClass, response);
        if (exception == null) {
          log.debug("Unable to extract response, using default message for {}({})", this.exceptionClass, this.status);
          exception = this.exceptionClass.getConstructor(new Class[] { String.class }).newInstance(new Object[] { this.status.getReasonPhrase() });
        } 
      } catch (InstantiationException|IllegalAccessException|IllegalArgumentException|InvocationTargetException|NoSuchMethodException|SecurityException reflectionError) {
        throw new IllegalStateException("Couldn't create exception for HTTP response", reflectionError);
      } 
      throw exception;
    } 
    log.debug("{} is not enabled, set the {} header using HttpCommunicationConfiguration.INTERCEPTORS_ENABLED_HEADER to \"true\"", this, "X-Intercept-Errors");
    return response;
  }
  
  private ApiErrorException extract(Class<? extends ApiErrorException> exceptionClass, ClientHttpResponse response) throws IOException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
    try {
      HttpMessageConverterExtractor<? extends ApiErrorException> extractor = new HttpMessageConverterExtractor(exceptionClass, this.messageConverters);
      return (ApiErrorException)extractor.extractData(response);
    } catch (Exception e) {
      return exceptionClass.getConstructor(new Class[] { String.class, Throwable.class }).newInstance(new Object[] { this.status.getReasonPhrase(), e });
    } 
  }
  
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("ApiErrorExceptionInterceptor [");
    builder.append(this.status);
    builder.append("->");
    builder.append(this.exceptionClass);
    builder.append("]");
    return builder.toString();
  }
}
