package com.value.buildingblocks.multitenancy.datasource;

import com.value.buildingblocks.multitenancy.Tenant;
import com.value.buildingblocks.multitenancy.TenantContext;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

public class MultiTenancyRoutingDataSource extends AbstractRoutingDataSource {
  public MultiTenancyRoutingDataSource() {
    setLenientFallback(false);
  }
  
  protected Object determineCurrentLookupKey() {
    return TenantContext.getTenant().map(Tenant::getId).orElse(null);
  }
}
