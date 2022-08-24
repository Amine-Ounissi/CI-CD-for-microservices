package com.value.buildingblocks.backend.communication.http;

import java.io.IOException;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;

class NoopResponseErrorHandler implements ResponseErrorHandler {
  private ClientHttpResponse response;
  
  public NoopResponseErrorHandler(ClientHttpResponse response) {
    this.response = response;
  }
  
  public boolean hasError(ClientHttpResponse response) throws IOException {
    return false;
  }
  
  public void handleError(ClientHttpResponse response) throws IOException {
    this.response = response;
  }
  
  public ClientHttpResponse getResponse() {
    return this.response;
  }
}
