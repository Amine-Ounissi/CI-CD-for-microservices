package com.value.buildingblocks.backend.communication.http;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Objects;
import javax.servlet.http.HttpServletRequest;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

public class CopyHeaderInterceptor implements ClientHttpRequestInterceptor {
  private final List<String> blacklistedHeaders = new ArrayList<>();
  
  public CopyHeaderInterceptor(List<String> blacklistedHeaders) {
    blacklistedHeaders.forEach(this::addBlacklistedHeader);
  }
  
  public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
    RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
    if (requestAttributes instanceof ServletRequestAttributes) {
      HttpServletRequest inboundRequest = ((ServletRequestAttributes)requestAttributes).getRequest();
      Enumeration<String> headerNames = inboundRequest.getHeaderNames();
      while (headerNames.hasMoreElements()) {
        String key = headerNames.nextElement();
        String value = inboundRequest.getHeader(key);
        if (!request.getHeaders().containsKey(key) && 
          !isKeyBlacklisted(key))
          request.getHeaders().add(key, value); 
      } 
    } 
    return execution.execute(request, body);
  }
  
  private boolean isKeyBlacklisted(String key) {
    return this.blacklistedHeaders.stream()
      .anyMatch(blacklist -> key.toLowerCase().startsWith(blacklist));
  }
  
  public void addBlacklistedHeader(String header) {
    Objects.requireNonNull(header, "BlacklistedHeader");
    this.blacklistedHeaders.add(header.toLowerCase());
  }
  
  public List<String> getBlacklistedHeaders() {
    return this.blacklistedHeaders;
  }
}
