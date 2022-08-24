package com.value.buildingblocks.idempotency;

import java.io.IOException;
import java.util.Set;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

public class DoubleSubmissionFilter extends OncePerRequestFilter {
  private static final Logger log = LoggerFactory.getLogger(DoubleSubmissionFilter.class);
  
  public static final String REQUEST_ID_HEADER = "X-Request-Id";
  
  private final Set<String> methods;
  
  private RequestIdCache idempotencyRequestIdCache;
  
  public DoubleSubmissionFilter(Set<String> methods, RequestIdCache idempotencyRequestIdCache) {
    this.methods = methods;
    this.idempotencyRequestIdCache = idempotencyRequestIdCache;
  }
  
  protected boolean shouldNotFilter(HttpServletRequest request) {
    return !this.methods.contains(request.getMethod());
  }
  
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
    String requestId = request.getHeader("X-Request-Id");
    try {
      validateRequestId(requestId);
      filterChain.doFilter((ServletRequest)request, (ServletResponse)response);
    } catch (DoubleSubmissionFilterException ex) {
      log.warn("Double-submission exception: {}", ex.getMessage());
      response.sendError(ex.getHttpStatus().value(), ex.getMessage());
    } 
  }
  
  private void validateRequestId(String requestId) {
    if (StringUtils.isEmpty(requestId))
      throw new MissingRequestIdHeaderException(); 
    CacheMissIndicator indicator = new CacheMissIndicator();
    this.idempotencyRequestIdCache.cacheRequestId(requestId, indicator);
    if (!indicator.isCacheMissed())
      throw new DuplicateRequestException(requestId); 
  }
}
