package com.value.buildingblocks.multitenancy.scope;

import com.value.buildingblocks.context.ContextConstants;
import com.value.buildingblocks.context.ContextQualifier;
import com.value.buildingblocks.context.ContextSupplier;
import com.value.buildingblocks.multitenancy.Tenant;
import com.value.buildingblocks.multitenancy.TenantContext;
import java.util.Optional;

public class TenantContextSupplier implements ContextSupplier {
  public ContextQualifier getContext() {
    Optional<Tenant> tenant = TenantContext.getTenant();
    if (tenant.isPresent())
      return new TenantContextQualifier(((Tenant)tenant.get()).getId()); 
    return ContextConstants.EMPTY_CONTEXT_QUALIFIER;
  }
}
