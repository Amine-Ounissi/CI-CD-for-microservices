package com.value.buildingblocks.multitenancy.caching;

import com.value.buildingblocks.multitenancy.MultiTenancyConfigurationProperties;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.cache.interceptor.CacheResolver;

public class TenantAwareCacheResolverBeanPostProcessor implements BeanPostProcessor {
  private static final Logger log = LoggerFactory.getLogger(TenantAwareCacheResolverBeanPostProcessor.class);
  
  private final Set<String> tenantAgnosticCacheResolvers;
  
  public TenantAwareCacheResolverBeanPostProcessor(MultiTenancyConfigurationProperties properties) {
    this

      
      .tenantAgnosticCacheResolvers = (Set<String>)properties.getCaching().getTenantAgnosticCacheResolvers().entrySet().stream().filter(Map.Entry::getValue).map(Map.Entry::getKey).collect(Collectors.toSet());
    log.debug("Tenant-agnostic CacheResolvers: {}", this.tenantAgnosticCacheResolvers);
  }
  
  public Object postProcessBeforeInitialization(Object bean, String beanName) {
    return bean;
  }
  
  public Object postProcessAfterInitialization(Object bean, String beanName) {
    if (bean instanceof CacheResolver && !(bean instanceof TenantAwareCacheResolver) && 
      !this.tenantAgnosticCacheResolvers.contains(beanName)) {
      log.debug("Making CacheResolver {} tenant-aware", beanName);
      bean = new TenantAwareCacheResolver((CacheResolver)bean);
    } 
    return bean;
  }
}
