package com.value.authentication.tokenconverter.cache;

import com.value.buildingblocks.multitenancy.Tenant;
import com.value.buildingblocks.multitenancy.TenantProvider;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class TokenConverterTenantProvider implements TenantProvider {
  private final Map<String, Tenant> map = new ConcurrentHashMap<>();
  
  public Optional<Tenant> findTenantById(String id) {
    if (!this.map.containsKey(id)) {
      Tenant tenant = new Tenant();
      tenant.setId(id);
      this.map.put(id, tenant);
    } 
    return Optional.ofNullable(this.map.get(id));
  }
  
  public Collection<Tenant> getTenants() {
    return Collections.unmodifiableCollection(this.map.values());
  }
}
