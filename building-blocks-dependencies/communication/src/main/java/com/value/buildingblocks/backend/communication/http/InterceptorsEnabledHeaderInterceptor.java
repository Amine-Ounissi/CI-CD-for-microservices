package com.value.buildingblocks.backend.communication.http;

import java.io.IOException;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

public class InterceptorsEnabledHeaderInterceptor implements ClientHttpRequestInterceptor {
  private static final Logger log = LoggerFactory.getLogger(InterceptorsEnabledHeaderInterceptor.class);
  
  public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
    List<String> value = request.getHeaders().remove("X-Intercept-Errors");
    log.debug("Removed {}: {}", "X-Intercept-Errors", value);
    try {
      return execution.execute(request, body);
    } finally {
      if (value != null) {
        log.debug("Restored {}: {}", "X-Intercept-Errors", value);
        request.getHeaders().addAll("X-Intercept-Errors", value);
      } 
    } 
  }
}
