package com.value.buildingblocks.multitenancy;

import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

public final class TenantContext {
  private static final Logger log = LoggerFactory.getLogger(TenantContext.class);
  
  public static final String MDC_TID = "TID";
  
  private static final ThreadLocal<Tenant> tenant = new InheritableThreadLocal<>();
  
  private TenantContext() {
    throw new AssertionError("TenantContext cannot be instantiated.");
  }
  
  public static void setTenant(Tenant tenant) {
    if (tenant == null) {
      log.info("null value provided to setTenant method; no tenant will be bound to the current thread.");
      clear();
    } else {
      TenantContext.tenant.set(tenant);
      MDC.put("TID", tenant.getId());
    } 
  }
  
  public static Optional<Tenant> getTenant() {
    return Optional.ofNullable(tenant.get());
  }
  
  public static void clear() {
    tenant.remove();
    MDC.remove("TID");
  }
}
