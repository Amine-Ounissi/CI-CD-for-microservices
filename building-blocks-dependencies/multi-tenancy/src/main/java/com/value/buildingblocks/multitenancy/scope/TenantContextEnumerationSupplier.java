package com.value.buildingblocks.multitenancy.scope;

import com.value.buildingblocks.context.ContextEnumerationSupplier;
import com.value.buildingblocks.context.ContextQualifier;
import com.value.buildingblocks.multitenancy.Tenant;
import com.value.buildingblocks.multitenancy.TenantProvider;
import java.util.ArrayList;
import java.util.List;

public class TenantContextEnumerationSupplier implements ContextEnumerationSupplier {
  private final TenantProvider tenantProvider;
  
  public TenantContextEnumerationSupplier(TenantProvider tenantProvider) {
    this.tenantProvider = tenantProvider;
  }
  
  public List<ContextQualifier> getContexts() {
    List<ContextQualifier> contexts = new ArrayList<>();
    for (Tenant tenant : this.tenantProvider.getTenants())
      contexts.add(new TenantContextQualifier(tenant.getId())); 
    return contexts;
  }
}
