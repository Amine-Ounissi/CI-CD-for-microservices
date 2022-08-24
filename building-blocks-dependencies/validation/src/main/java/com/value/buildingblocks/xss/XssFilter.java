package com.value.buildingblocks.xss;

import com.value.org.owasp.esapi.codecs.DefaultEncoder;
import com.value.org.owasp.esapi.codecs.IntrusionException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Pattern;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.web.util.matcher.RequestMatcher;

public class XssFilter implements Filter {
  private static final Logger log = LoggerFactory.getLogger(XssFilter.class);
  
  private static final HashSet<String> ALLOWED_METHODS = new HashSet<>(
      Arrays.asList(new String[] { "GET", "HEAD", "TRACE", "OPTIONS" }));
  
  private List<Pattern> patterns = new ArrayList<>();
  
  private boolean useEsapi = true;
  
  private Pattern htmlColonPattern = Pattern.compile("&colon;", 2);
  
  private Pattern nullCharacterPattern = Pattern.compile("\000");
  
  private RequestMatcher excludeRequestMatcher;
  
  public List<Pattern> getPatterns() {
    return this.patterns;
  }
  
  public void setPatterns(List<Pattern> patterns) {
    log.trace("setPatterns({})", patterns);
    this.patterns = patterns;
  }
  
  public boolean isUseEsapi() {
    return this.useEsapi;
  }
  
  public void setUseEsapi(boolean useEsapi) {
    this.useEsapi = useEsapi;
  }
  
  public RequestMatcher getExcludeRequestMatcher() {
    return this.excludeRequestMatcher;
  }
  
  public void setExcludeRequestMatcher(RequestMatcher excludeRequestMatcher) {
    this.excludeRequestMatcher = excludeRequestMatcher;
  }
  
  public void destroy() {
    log.trace("destroy()");
  }
  
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
    HttpServletRequest httpServletRequest = (HttpServletRequest)request;
    HttpServletResponse httpServletResponse = (HttpServletResponse)response;
    if (requestNeedsFiltering(httpServletRequest)) {
      doHttpRequestFilter(httpServletRequest, httpServletResponse, chain);
    } else {
      chain.doFilter(request, response);
    } 
  }
  
  private void doHttpRequestFilter(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain chain) throws IOException, ServletException {
    XssFilterRequestWrapper wrappedRequest = new XssFilterRequestWrapper(httpServletRequest);
    String requestCharacterEncoding = wrappedRequest.getCharacterEncoding();
    String originalContent = new String(wrappedRequest.getRawData(), (requestCharacterEncoding == null) ? StandardCharsets.UTF_8.name() : requestCharacterEncoding);
    String content = originalContent;
    if (isUseEsapi())
      try {
        content = canonicalizeString(originalContent);
      } catch (IntrusionException intrusionException) {
        String message = intrusionException.getMessage();
        int pos = message.indexOf("detected in");
        if (pos > -1)
          message = message.substring(0, pos + "detected in".length()) + " request body"; 
        log.warn("Possible XSS payload discovered when canonicalising request content: {}", message);
        httpServletResponse.sendError(HttpStatus.BAD_REQUEST.value());
        return;
      }  
    content = this.nullCharacterPattern.matcher(content).replaceAll("");
    content = this.htmlColonPattern.matcher(content).replaceAll(":");
    for (Pattern pattern : this.patterns) {
      if (pattern.matcher(content).find()) {
        log.warn("Request content matched pattern {}", pattern);
        httpServletResponse.sendError(HttpStatus.BAD_REQUEST.value());
        return;
      } 
      log.trace("Not Matched {}", pattern);
    } 
    chain.doFilter((ServletRequest)wrappedRequest, (ServletResponse)httpServletResponse);
  }
  
  private boolean requestNeedsFiltering(HttpServletRequest httpServletRequest) {
    if (this.excludeRequestMatcher != null && this.excludeRequestMatcher.matches(httpServletRequest))
      return false; 
    boolean isJsonContent = isJsonContent(httpServletRequest);
    boolean isAllowedRequestMethod = allowedRequestMethod(httpServletRequest);
    boolean requestNeedsFiltering = (!isAllowedRequestMethod && isJsonContent);
    if (log.isTraceEnabled()) {
      log.trace("Request required XSS filtering?: {}", Boolean.valueOf(requestNeedsFiltering));
      log.trace("Request content: {}", httpServletRequest.getContentType());
      log.trace("Request method: {}", httpServletRequest.getMethod());
    } 
    return requestNeedsFiltering;
  }
  
  private boolean isJsonContent(HttpServletRequest httpServletRequest) {
    String contentType = httpServletRequest.getContentType();
    return StringUtils.containsIgnoreCase(contentType, "application/json");
  }
  
  public void init(FilterConfig config) throws ServletException {
    log.trace("init(FilterConfig config)");
  }
  
  private boolean allowedRequestMethod(HttpServletRequest request) {
    return ALLOWED_METHODS.contains(request.getMethod());
  }
  
  private String canonicalizeString(String input) {
    return DefaultEncoder.getInstance().canonicalize(input, true, true);
  }
}
