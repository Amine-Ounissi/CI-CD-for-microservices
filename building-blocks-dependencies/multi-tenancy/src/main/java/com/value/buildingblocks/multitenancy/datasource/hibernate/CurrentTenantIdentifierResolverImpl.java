package com.value.buildingblocks.multitenancy.datasource.hibernate;

import com.value.buildingblocks.multitenancy.Tenant;
import com.value.buildingblocks.multitenancy.TenantContext;
import org.hibernate.context.spi.CurrentTenantIdentifierResolver;

class CurrentTenantIdentifierResolverImpl implements CurrentTenantIdentifierResolver {
  public String resolveCurrentTenantIdentifier() {
    return TenantContext.getTenant()
      .<String>map(Tenant::getId)
      .orElse("");
  }
  
  public boolean validateExistingCurrentSessions() {
    return true;
  }
}
