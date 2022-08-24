package com.value.buildingblocks.multitenancy.caching;

import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;
import org.springframework.cache.Cache;
import org.springframework.cache.interceptor.CacheOperationInvocationContext;
import org.springframework.cache.interceptor.CacheResolver;

public class TenantAwareCacheResolver implements CacheResolver {
  private final CacheResolver delegate;
  
  public TenantAwareCacheResolver(CacheResolver delegate) {
    this.delegate = Objects.<CacheResolver>requireNonNull(delegate);
  }
  
  public final Collection<? extends Cache> resolveCaches(CacheOperationInvocationContext<?> context) {
    return (Collection<? extends Cache>)this.delegate.resolveCaches(context).stream().map(TenantAwareCacheKeyDecorator::new)
      .collect(Collectors.toList());
  }
}
