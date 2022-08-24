package com.value.buildingblocks.multitenancy;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

public class TenantFilter extends OncePerRequestFilter {
  private static final Logger log = LoggerFactory.getLogger(TenantFilter.class);
  
  private final TenantProvider tenantProvider;
  
  private final List<TenantIdentifierStrategy> tenantIdentifierStrategies;
  
  private List<AntPathRequestMatcher> shouldNotFilterPaths = new ArrayList<>();
  
  public TenantFilter(TenantProvider tenantProvider, List<TenantIdentifierStrategy> tenantIdentifierStrategies) {
    this.tenantProvider = tenantProvider;
    this.tenantIdentifierStrategies = tenantIdentifierStrategies;
  }
  
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
    try {
      Optional<Tenant> tenant = identifyTenant(request);
      if (tenant.isPresent()) {
        log.debug("Found Tenant {} ", tenant.get());
        setContext(tenant.get());
        filterChain.doFilter((ServletRequest)request, (ServletResponse)response);
      } else {
        log.warn("Could not identify Tenant using available strategies {} and {}. Supply a tenant identifier for the available strategies. Configure the available tenants.", this.tenantIdentifierStrategies, this.tenantProvider);
        response.sendError(400, "Could not identify Tenant using available strategies.");
      } 
    } finally {
      clearContext();
    } 
  }
  
  public List<AntPathRequestMatcher> getShouldNotFilterPaths() {
    return this.shouldNotFilterPaths;
  }
  
  public void setShouldNotFilterPaths(List<AntPathRequestMatcher> shouldNotFilterPaths) {
    this.shouldNotFilterPaths = shouldNotFilterPaths;
  }
  
  protected void clearContext() {
    TenantContext.clear();
  }
  
  protected void setContext(Tenant tenant) {
    TenantContext.setTenant(tenant);
  }
  
  protected Optional<Tenant> identifyTenant(HttpServletRequest httpRequest) {
    for (TenantIdentifierStrategy tenantIdentifierStrategy : this.tenantIdentifierStrategies) {
      Optional<Tenant> tenant = tenantIdentifierStrategy.identifyTenant(httpRequest).<Optional<Tenant>>map(this.tenantProvider::findTenantById).orElse(Optional.empty());
      if (tenant.isPresent())
        return tenant; 
    } 
    return Optional.empty();
  }
  
  protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
    log.debug("should Not Filter Paths {}", this.shouldNotFilterPaths);
    for (AntPathRequestMatcher antPathRequestMatcher : this.shouldNotFilterPaths) {
      if (antPathRequestMatcher.matches(request)) {
        if (log.isDebugEnabled())
          log.debug("Not filtering request URL {} due to match with {}", request.getRequestURL(), antPathRequestMatcher); 
        return true;
      } 
    } 
    return false;
  }
}
