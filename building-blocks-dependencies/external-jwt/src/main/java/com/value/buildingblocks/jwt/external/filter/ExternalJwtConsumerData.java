package com.value.buildingblocks.jwt.external.filter;

import com.value.buildingblocks.jwt.external.authentication.ExternalJwtAuthentication;
import com.value.buildingblocks.jwt.external.token.ExternalJwtCookie;
import com.value.buildingblocks.jwt.external.token.ExternalJwtHttpHeader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.servlet.http.Cookie;
import org.springframework.security.core.Authentication;

public class ExternalJwtConsumerData {
  private Authentication authentication;
  
  private ExternalJwtHttpHeader tokenHeader;
  
  private ExternalJwtCookie tokenCookie;
  
  private Map<String, String> upStreamHeaders = new HashMap<>();
  
  private List<Cookie> upStreamCookies = new ArrayList<>();
  
  private Map<String, String> downStreamHeaders = new HashMap<>();
  
  private List<Cookie> downStreamCookies = new ArrayList<>();
  
  public ExternalJwtConsumerData(Authentication authentication) {
    this.authentication = authentication;
  }
  
  public ExternalJwtConsumerData(ExternalJwtHttpHeader tokenHeader) {
    this.authentication = (Authentication)new ExternalJwtAuthentication(tokenHeader.getToken());
    this.tokenHeader = tokenHeader;
    addUpStreamHeader(tokenHeader.getHeaderName(), tokenHeader.getHeaderValue());
  }
  
  public ExternalJwtConsumerData(ExternalJwtCookie tokenCookie) {
    this.authentication = (Authentication)new ExternalJwtAuthentication(tokenCookie.getToken());
    this.tokenCookie = tokenCookie;
    addUpStreamCookie(tokenCookie);
  }
  
  public Authentication getAuthentication() {
    return this.authentication;
  }
  
  public Optional<ExternalJwtHttpHeader> getTokenHeader() {
    return Optional.ofNullable(this.tokenHeader);
  }
  
  public Optional<ExternalJwtCookie> getTokenCookie() {
    return Optional.ofNullable(this.tokenCookie);
  }
  
  public Map<String, String> getUpStreamHeaders() {
    return this.upStreamHeaders;
  }
  
  public void addUpStreamHeader(String name, String value) {
    this.upStreamHeaders.put(name, value);
  }
  
  public List<Cookie> getUpStreamCookies() {
    return this.upStreamCookies;
  }
  
  public void addUpStreamCookie(Cookie cookie) {
    this.upStreamCookies.add(cookie);
  }
  
  public Map<String, String> getDownStreamHeaders() {
    return this.downStreamHeaders;
  }
  
  public void addDownStreamHeader(String name, String value) {
    this.downStreamHeaders.put(name, value);
  }
  
  public List<Cookie> getDownStreamCookies() {
    return this.downStreamCookies;
  }
  
  public void addDownStreamCookie(Cookie cookie) {
    this.downStreamCookies.add(cookie);
  }
}
