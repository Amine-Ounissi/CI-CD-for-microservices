package com.value.buildingblocks.multitenancy.datasource;

import com.value.buildingblocks.multitenancy.Tenant;

public interface TenantSchemaProvider {
  String getSchemaForTenant(Tenant paramTenant);
}
