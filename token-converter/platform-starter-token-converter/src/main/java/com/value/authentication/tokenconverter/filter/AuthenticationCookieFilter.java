package com.value.authentication.tokenconverter.filter;

import com.value.authentication.tokenconverter.configuration.TokenConverterProperties;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import lombok.Generated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;

@Configuration
@Order(-2147483648)
public class AuthenticationCookieFilter extends GenericFilterBean {
  @Generated
  private static final Logger log = LoggerFactory.getLogger(AuthenticationCookieFilter.class);
  
  @Autowired
  private TokenConverterProperties tokenConverterProperties;
  
  public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
    if (this.tokenConverterProperties.getEnableCookieFilter()) {
      handleFilter(req, res, chain);
    } else {
      chain.doFilter(req, res);
    } 
  }
  
  private void handleFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
    HttpServletRequest request = (HttpServletRequest)req;
    Cookie[] cookies = request.getCookies();
    Optional<Cookie> cookieValue;
    cookieValue = getCookieValue(cookies, this.tokenConverterProperties.getAuthorizationCookieName());
    boolean authHeaderNotSet = StringUtils.isEmpty(request.getHeader(this.tokenConverterProperties.getAuthorizationRequestHeaderName()));
    if (cookieValue.isPresent() && !authHeaderNotSet)
      log.debug("Both Authorization cookie and header detected.  Header will be used."); 
    boolean localSet = false;
    if (cookieValue.isPresent() && authHeaderNotSet) {
      String value = cookieValue.get().getValue();
      HeaderMapRequestWrapper wrapper = new HeaderMapRequestWrapper(request);
      if (StringUtils.isEmpty(request.getHeader(this.tokenConverterProperties.getAuthorizationRequestHeaderName()))) {
        log.debug("Authorization cookie detected and converted to header");
        wrapper.addHeader(this.tokenConverterProperties
            .getAuthorizationRequestHeaderName(), "Bearer " + value);
        localSet = true;
        chain.doFilter(wrapper, res);
      } 
    } 
    if (!localSet)
      chain.doFilter(request, res);
  }
  
  private Optional<Cookie> getCookieValue(Cookie[] cookies, String cookieName) {
    Stream<Cookie> stream = Objects.nonNull(cookies) ? Arrays.stream(cookies) : Stream.empty();
    return stream.filter(cookie -> cookieName.equals(cookie.getName()))
      .findFirst();
  }
  
  public static class HeaderMapRequestWrapper extends HttpServletRequestWrapper {
    private Map<String, String> headerMap = new HashMap<>();
    
    public HeaderMapRequestWrapper(HttpServletRequest request) {
      super(request);
    }
    
    public void addHeader(String name, String value) {
      this.headerMap.put(name, value);
    }
    
    public String getHeader(String name) {
      String headerValue = super.getHeader(name);
      if (this.headerMap.containsKey(name))
        headerValue = this.headerMap.get(name); 
      return headerValue;
    }
    
    public Enumeration<String> getHeaderNames() {
      List<String> names = Collections.list(super.getHeaderNames());
      names.addAll(this.headerMap.keySet());
      return Collections.enumeration(names);
    }
    
    public Enumeration<String> getHeaders(String name) {
      List<String> values = Collections.list(super.getHeaders(name));
      if (this.headerMap.containsKey(name))
        values.add(this.headerMap.get(name)); 
      return Collections.enumeration(values);
    }
  }
}
