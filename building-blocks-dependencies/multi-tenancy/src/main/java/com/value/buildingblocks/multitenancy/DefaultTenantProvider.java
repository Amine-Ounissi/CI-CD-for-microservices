package com.value.buildingblocks.multitenancy;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultTenantProvider implements TenantProvider {
  private static final Logger log = LoggerFactory.getLogger(DefaultTenantProvider.class);
  
  private final Map<String, Tenant> map;
  
  public DefaultTenantProvider(List<Tenant> tenants) {
    Objects.requireNonNull(tenants, "Tenants are required");
    this.map = (Map<String, Tenant>)tenants.stream().collect(Collectors.toMap(Tenant::getId, Function.identity()));
  }
  
  public Optional<Tenant> findTenantById(String id) {
    Optional<Tenant> tenant = Optional.ofNullable(this.map.get(id));
    if (!tenant.isPresent()) {
      log.debug("Tenant not configured for identifier \"{}\"", id);
      log.debug("Available tenants are {}", this.map);
    } 
    return tenant;
  }
  
  public String toString() {
    return String.format("DefaultTenantProvider%s", new Object[] { this.map.keySet() });
  }
  
  public Collection<Tenant> getTenants() {
    return Collections.unmodifiableCollection(this.map.values());
  }
}
