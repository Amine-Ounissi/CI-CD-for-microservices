package com.value.buildingblocks.multitenancy.caching;

import com.value.buildingblocks.multitenancy.Tenant;
import com.value.buildingblocks.multitenancy.TenantContext;
import java.util.Optional;
import java.util.concurrent.Callable;
import org.springframework.cache.Cache;
import org.springframework.cache.interceptor.SimpleKey;

public class TenantAwareCacheKeyDecorator implements Cache {
  private final Cache cache;
  
  public TenantAwareCacheKeyDecorator(Cache cache) {
    this.cache = cache;
  }
  
  private Object addTenantToKeyIfPresent(Object key) {
    Optional<Tenant> tenant = TenantContext.getTenant();
    if (tenant.isPresent())
      return new SimpleKey(new Object[] { ((Tenant)tenant.get()).getId(), key }); 
    return key;
  }
  
  public String getName() {
    return this.cache.getName();
  }
  
  public Object getNativeCache() {
    return this.cache.getNativeCache();
  }
  
  public Cache.ValueWrapper get(Object key) {
    return this.cache.get(addTenantToKeyIfPresent(key));
  }
  
  public <T> T get(Object key, Class<T> type) {
    return (T)this.cache.get(addTenantToKeyIfPresent(key), type);
  }
  
  public <T> T get(Object key, Callable<T> valueLoader) {
    return (T)this.cache.get(addTenantToKeyIfPresent(key), valueLoader);
  }
  
  public void put(Object key, Object value) {
    this.cache.put(addTenantToKeyIfPresent(key), value);
  }
  
  public Cache.ValueWrapper putIfAbsent(Object key, Object value) {
    return this.cache.putIfAbsent(addTenantToKeyIfPresent(key), value);
  }
  
  public void evict(Object key) {
    this.cache.evict(addTenantToKeyIfPresent(key));
  }
  
  public void clear() {
    this.cache.clear();
  }
}
