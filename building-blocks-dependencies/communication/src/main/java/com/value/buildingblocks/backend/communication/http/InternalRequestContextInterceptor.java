package com.value.buildingblocks.backend.communication.http;

import com.value.buildingblocks.backend.internalrequest.InternalRequestContext;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;

public class InternalRequestContextInterceptor implements ClientHttpRequestInterceptor {
  private final InternalRequestContext internalRequestContext;
  
  private static final Logger log = LoggerFactory.getLogger(InternalRequestContextInterceptor.class);
  
  @Autowired
  public InternalRequestContextInterceptor(InternalRequestContext internalRequestContext) {
    this.internalRequestContext = internalRequestContext;
  }
  
  public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
    HttpHeaders headers = request.getHeaders();
    if (RequestContextHolder.getRequestAttributes() != null) {
      addHeaderIfNotSetOrEmpty(headers, "X-CXT-User-Token", this.internalRequestContext
          .getUserToken());
      addHeaderIfNotSetOrEmpty(headers, "X-CXT-Remote-User", this.internalRequestContext
          .getRemoteUser());
      addHeaderIfNotSetOrEmpty(headers, "x-forwarded-for", this.internalRequestContext
          .getRemoteAddress());
      addHeaderIfNotSetOrEmpty(headers, "X-CXT-RequestTime", 
          String.valueOf(this.internalRequestContext.getRequestTime()));
      addHeaderIfNotSetOrEmpty(headers, "X-CXT-UserAgent", this.internalRequestContext
          .getUserAgent());
      addHeaderIfNotSetOrEmpty(headers, "X-CXT-ChannelID", this.internalRequestContext
          .getChannelId());
      addHeaderIfNotSetOrEmpty(headers, "X-CXT-RequestUUID", this.internalRequestContext
          .getRequestUuid());
      addHeaderIfNotSetOrEmpty(headers, "X-CXT-AuthStatus", 
          String.valueOf(this.internalRequestContext.getAuthStatus()));
    } else {
      log.debug("Not sending InternalRequestContext because there is no RequestContext");
    } 
    return execution.execute(request, body);
  }
  
  private void addHeaderIfNotSetOrEmpty(HttpHeaders headers, String name, String value) {
    if (!headers.containsKey(name) && !StringUtils.isEmpty(value))
      headers.add(name, value); 
  }
}
