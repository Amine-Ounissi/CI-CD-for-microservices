package com.value.buildingblocks.multitenancy;

import com.value.buildingblocks.jwt.core.token.JsonWebTokenClaimsSet;
import com.value.buildingblocks.jwt.core.token.JsonWebTokenClaimsSetUtils;
import java.util.Collection;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import org.springframework.security.core.context.SecurityContextHolder;

public class TenantIdentifierServiceRequestStrategy extends TenantIdentifierHttpHeaderStrategy {
  private final String serviceApiScope;
  
  public TenantIdentifierServiceRequestStrategy(String tenantIdHttpHeader, String serviceApiScope) {
    super(tenantIdHttpHeader, new String[0]);
    this.serviceApiScope = serviceApiScope;
  }
  
  public Optional<String> identifyTenant(HttpServletRequest httpRequest) {
    if (containsServiceScope())
      return super.identifyTenant(httpRequest); 
    return Optional.empty();
  }
  
  public String toString() {
    return "TenantIdentifierServiceRequestStrategy [serviceApiScope=" + this.serviceApiScope + ", tenantIdHttpHeader=" + this.tenantIdHttpHeader + "]";
  }
  
  private boolean containsServiceScope() {
    Optional<Object> scopes = JsonWebTokenClaimsSetUtils.getClaim(SecurityContextHolder.getContext().getAuthentication(), claimsSet -> claimsSet.getClaim("scope"));
    if (scopes.isPresent() && scopes.get() instanceof Collection)
      return ((Collection)scopes.get()).contains(this.serviceApiScope); 
    return false;
  }
}
