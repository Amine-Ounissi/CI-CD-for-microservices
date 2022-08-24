package com.value.buildingblocks.multitenancy.datasource;

import com.value.buildingblocks.multitenancy.Tenant;

public interface TenantCatalogProvider {
  String getCatalogForTenant(Tenant paramTenant);
}
