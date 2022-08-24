package com.value.buildingblocks.multitenancy;

import com.value.buildingblocks.jwt.core.token.JsonWebTokenClaimsSetUtils;
import com.value.buildingblocks.jwt.internal.token.InternalJwtClaimsSet;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;

public class TenantIdentifierJwtClaimStrategy implements TenantIdentifierStrategy {
  private static final Logger log = LoggerFactory.getLogger(TenantIdentifierJwtClaimStrategy.class);
  
  public Optional<String> identifyTenant(HttpServletRequest httpRequest) {
    Optional<String> tenantId = JsonWebTokenClaimsSetUtils.getClaim(
        SecurityContextHolder.getContext().getAuthentication(), InternalJwtClaimsSet::getTenantId);
    log.debug("Found tenant identifier {}", tenantId);
    return tenantId;
  }
  
  public String toString() {
    return String.format("TenantIdentifierJwtClaimStrategy[\"%s\"]", new Object[] { "tid" });
  }
}
