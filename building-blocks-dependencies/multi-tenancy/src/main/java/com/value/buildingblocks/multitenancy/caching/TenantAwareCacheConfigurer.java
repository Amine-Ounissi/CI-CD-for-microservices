package com.value.buildingblocks.multitenancy.caching;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.interceptor.CacheResolver;
import org.springframework.cache.interceptor.SimpleCacheResolver;

public class TenantAwareCacheConfigurer extends CachingConfigurerSupport {
  private final CacheManager cacheManager;
  
  public TenantAwareCacheConfigurer(CacheManager cacheManager) {
    this.cacheManager = cacheManager;
  }
  
  public CacheResolver cacheResolver() {
    return new TenantAwareCacheResolver((CacheResolver)new SimpleCacheResolver(this.cacheManager));
  }
}
