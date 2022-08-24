package com.value.buildingblocks.backend.communication.http;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.RestClientResponseException;

public class RestClientResponseExceptionClientHttpResponseAdaptor implements ClientHttpResponse {
  private RestClientResponseException exception;
  
  public RestClientResponseExceptionClientHttpResponseAdaptor(RestClientResponseException exception) {
    this.exception = exception;
  }
  
  public HttpStatus getStatusCode() throws IOException {
    return HttpStatus.valueOf(this.exception.getRawStatusCode());
  }
  
  public int getRawStatusCode() throws IOException {
    return this.exception.getRawStatusCode();
  }
  
  public String getStatusText() throws IOException {
    return this.exception.getStatusText();
  }
  
  public void close() {}
  
  public InputStream getBody() throws IOException {
    return new ByteArrayInputStream(this.exception.getResponseBodyAsByteArray());
  }
  
  public HttpHeaders getHeaders() {
    return this.exception.getResponseHeaders();
  }
}
