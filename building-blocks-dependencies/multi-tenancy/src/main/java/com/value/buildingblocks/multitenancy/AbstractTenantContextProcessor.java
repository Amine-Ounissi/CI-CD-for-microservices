package com.value.buildingblocks.multitenancy;

public abstract class AbstractTenantContextProcessor {
  protected void doJoinContext(Tenant tenant) {
    TenantContext.setTenant(tenant);
  }
  
  protected void doLeaveContext() {
    TenantContext.clear();
  }
}
