package com.value.buildingblocks.jwt.core.util;

import com.value.buildingblocks.jwt.core.exception.JsonWebTokenAuthenticationException;
import java.util.Enumeration;
import java.util.Optional;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.ApplicationContext;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

public final class JsonWebTokenUtils {
  public static Authentication getSecurityContextAuthentication() {
    SecurityContext context = SecurityContextHolder.getContext();
    Authentication authentication = context.getAuthentication();
    if (authentication == null)
      throw new JsonWebTokenAuthenticationException("Authentication object is not available"); 
    return authentication;
  }
  
  public static boolean isHeaderBasedAuth(String headerName, HttpServletRequest request) {
    String headerSchema = "Bearer";
    Optional<String> header = getHeaderValue(headerName, request);
    if (!header.isPresent())
      return false; 
    String value = header.get();
    if (!value.startsWith("Bearer"))
      return false; 
    value = value.substring("Bearer".length());
    return !StringUtils.isEmpty(value.trim());
  }
  
  public static Optional<String> getHeaderValue(String headerName, HttpServletRequest httpRequest) {
    Enumeration<String> headerNames = httpRequest.getHeaderNames();
    while (headerNames.hasMoreElements()) {
      String header = headerNames.nextElement();
      if (headerName.equalsIgnoreCase(header))
        return Optional.ofNullable(httpRequest.getHeader(header)); 
    } 
    return Optional.empty();
  }
  
  public static boolean isCookieBasedAuth(String tokenCookieName, HttpServletRequest request) {
    return getCookieValue(tokenCookieName, request).isPresent();
  }
  
  public static Optional<String> getCookieValue(String name, HttpServletRequest httpRequest) {
    Optional<Cookie> cookie = getCookie(name, httpRequest);
    if (cookie.isPresent())
      return Optional.ofNullable(((Cookie)cookie.get()).getValue()); 
    return Optional.empty();
  }
  
  public static Optional<Cookie> getCookie(String name, HttpServletRequest httpRequest) {
    Cookie[] cookies = httpRequest.getCookies();
    if (cookies == null || cookies.length == 0)
      return Optional.empty(); 
    for (Cookie cookie : cookies) {
      if (cookie.getName().equals(name) && StringUtils.isNotBlank(cookie.getValue()))
        return Optional.of(cookie); 
    } 
    return Optional.empty();
  }
  
  public static <T extends com.value.buildingblocks.jwt.core.properties.JsonWebTokenProperties> Optional<T> bind(T properties, ApplicationContext applicationContext, String propertyNamespace1, String propertyNamespace2) {
    Binder binder = Binder.get(applicationContext.getEnvironment());
    Bindable<T> bindable = Bindable.ofInstance(properties);
    binder.bind(propertyNamespace1, bindable);
    binder.bind(propertyNamespace2, bindable);
    return Optional.of(properties);
  }
}
