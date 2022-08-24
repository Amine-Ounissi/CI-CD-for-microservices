package com.value.buildingblocks.security.csrf;

import java.util.Arrays;
import java.util.HashSet;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.web.util.matcher.RequestMatcher;

public class CsrfRequestMatcher implements RequestMatcher {
  private static final Logger logger = LoggerFactory.getLogger(CsrfRequestMatcher.class);
  
  private static final String GATEWAY_REQUEST_HEADER = "X-Forwarded-Prefix";
  
  private static final HashSet<String> ALLOWED_METHODS = new HashSet<>(
      Arrays.asList(new String[] { "GET", "HEAD", "TRACE", "OPTIONS" }));
  
  private final boolean enabled;
  
  public CsrfRequestMatcher() {
    this(true);
  }
  
  public CsrfRequestMatcher(boolean enabled) {
    this.enabled = enabled;
  }
  
  public boolean matches(HttpServletRequest request) {
    if (!isRequestFromTheGateway(request))
      return false; 
    if (allowedRequestMethod(request))
      return false; 
    if (this.enabled)
      return true; 
    String message = "The current request on the service ({} {}) is applicable to a CSRF check, however CSRF is disabled! To enable CSRF check 'buildingblocks.security.csrf.enabled' property.";
    logger.warn(message, request
        
        .getMethod(), request
        .getRequestURI());
    return false;
  }
  
  private boolean isRequestFromTheGateway(HttpServletRequest request) {
    return (request.getHeader("X-Forwarded-Prefix") != null);
  }
  
  private boolean allowedRequestMethod(HttpServletRequest request) {
    return ALLOWED_METHODS.contains(request.getMethod());
  }
}
