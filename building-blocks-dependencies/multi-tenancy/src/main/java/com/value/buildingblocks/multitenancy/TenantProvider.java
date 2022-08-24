package com.value.buildingblocks.multitenancy;

import java.util.Collection;
import java.util.Optional;

public interface TenantProvider {
  Optional<Tenant> findTenantById(String paramString);
  
  Collection<Tenant> getTenants();
}
