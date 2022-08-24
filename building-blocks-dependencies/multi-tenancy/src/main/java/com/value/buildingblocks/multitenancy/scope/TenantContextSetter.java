package com.value.buildingblocks.multitenancy.scope;

import com.value.buildingblocks.context.ContextQualifier;
import com.value.buildingblocks.context.ContextSetter;
import com.value.buildingblocks.multitenancy.Tenant;
import com.value.buildingblocks.multitenancy.TenantContext;
import com.value.buildingblocks.multitenancy.TenantProvider;
import java.util.Optional;

public class TenantContextSetter implements ContextSetter {
  private final TenantProvider tenantProvider;
  
  public TenantContextSetter(TenantProvider tenantProvider) {
    this.tenantProvider = tenantProvider;
  }
  
  public void setContext(ContextQualifier context) {
    if (!(context instanceof TenantContextQualifier)) {
      TenantContext.clear();
    } else {
      Optional<Tenant> tenant = this.tenantProvider.findTenantById(((TenantContextQualifier)context).getTid());
      TenantContext.setTenant(tenant.orElse(null));
    } 
  }
}
