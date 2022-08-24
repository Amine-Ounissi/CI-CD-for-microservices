package com.value.buildingblocks.multitenancy;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

public class TenantIdentifierHttpHeaderStrategy implements TenantIdentifierStrategy {
  private static final Logger log = LoggerFactory.getLogger(TenantIdentifierHttpHeaderStrategy.class);
  
  protected final String tenantIdHttpHeader;
  
  private List<RequestMatcher> antMatchers = new ArrayList<>(1);
  
  public TenantIdentifierHttpHeaderStrategy(String tenantIdHttpHeader, String... antPatterns) {
    this.tenantIdHttpHeader = tenantIdHttpHeader;
    for (String pattern : antPatterns)
      this.antMatchers.add(new AntPathRequestMatcher(pattern)); 
  }
  
  public Optional<String> identifyTenant(HttpServletRequest httpRequest) {
    String tenantId = null;
    if (this.antMatchers.isEmpty() || matches(httpRequest)) {
      tenantId = extractTenantIdFromRequest(httpRequest);
    } else {
      log.debug("Not a matched path in {}", this.antMatchers);
    } 
    log.debug("Found tenant identifier {}", tenantId);
    return Optional.ofNullable(tenantId);
  }
  
  private boolean matches(HttpServletRequest httpRequest) {
    for (RequestMatcher matcher : this.antMatchers) {
      if (matcher.matches(httpRequest)) {
        log.debug("matched with {}", matcher);
        return true;
      } 
    } 
    return false;
  }
  
  protected String extractTenantIdFromRequest(HttpServletRequest httpRequest) {
    String tenantId = null;
    Enumeration<String> headers = httpRequest.getHeaders(this.tenantIdHttpHeader);
    while (headers.hasMoreElements()) {
      if (tenantId == null) {
        tenantId = headers.nextElement();
        continue;
      } 
      String newTenantId = headers.nextElement();
      if (!tenantId.equals(newTenantId))
        throw new IllegalStateException("Multiple tenantIds " + tenantId + "," + newTenantId); 
    } 
    return tenantId;
  }
  
  public String toString() {
    return String.format("TenantIdentifierHttpHeaderStrategy[\"%s,%s\"]", new Object[] { this.tenantIdHttpHeader, this.antMatchers });
  }
  
  public List<RequestMatcher> getAntMatchers() {
    return this.antMatchers;
  }
  
  public void setAntMatchers(List<RequestMatcher> antMatchers) {
    this.antMatchers = antMatchers;
  }
}
