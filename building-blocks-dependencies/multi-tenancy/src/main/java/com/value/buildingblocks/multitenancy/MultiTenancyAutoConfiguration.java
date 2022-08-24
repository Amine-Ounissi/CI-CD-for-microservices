package com.value.buildingblocks.multitenancy;

import com.value.buildingblocks.backend.communication.http.CopyHeaderInterceptor;
import com.value.buildingblocks.backend.security.auth.config.ServiceApiAuthenticationProperties;
import com.value.buildingblocks.context.ContextEnumerationSupplier;
import com.value.buildingblocks.context.ContextScoped;
import com.value.buildingblocks.context.ContextSetter;
import com.value.buildingblocks.context.ContextSupplier;
import com.value.buildingblocks.extensions.BehaviorExtensionConfiguration;
import com.value.buildingblocks.multitenancy.caching.TenantAwareCacheConfigurer;
import com.value.buildingblocks.multitenancy.caching.TenantAwareCacheResolverBeanPostProcessor;
import com.value.buildingblocks.multitenancy.scope.TenantContextEnumerationSupplier;
import com.value.buildingblocks.multitenancy.scope.TenantContextSetter;
import com.value.buildingblocks.multitenancy.scope.TenantContextSupplier;
import java.util.List;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.cache.CacheAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;

@Configuration
@EnableConfigurationProperties({MultiTenancyConfigurationProperties.class, ServiceApiAuthenticationProperties.class})
@AutoConfigureAfter({CacheAutoConfiguration.class})
@ConditionalOnProperty(name = {"value.multi-tenancy.enabled"}, havingValue = "true", matchIfMissing = false)
@PropertySource({"classpath:multi-tenancy.properties"})
public class MultiTenancyAutoConfiguration {
  @Deprecated
  public TenantProvider defaultTenantProvider(MultiTenancyConfigurationProperties properties) {
    return new DefaultTenantProvider(properties.getTenants());
  }
  
  @ConditionalOnMissingBean({TenantProvider.class})
  @Bean
  public TenantProvider defaultTenantProvider(Environment environment) {
    MultiTenancyConfigurationProperties properties = (MultiTenancyConfigurationProperties)Binder.get(environment).bindOrCreate("value.multi-tenancy", MultiTenancyConfigurationProperties.class);
    return new DefaultTenantProvider(properties.getTenants());
  }
  
  @Order(10)
  @ConditionalOnClass(name = {"com.value.buildingblocks.jwt.internal.token.InternalJwtClaimsSet"})
  @Bean
  @ConditionalOnProperty(name = {"value.multi-tenancy.jwt-claim-strategy-enabled"}, havingValue = "true", matchIfMissing = true)
  public TenantIdentifierStrategy tenantIdentifierJwtClaimStrategy() {
    return new TenantIdentifierJwtClaimStrategy();
  }
  
  @Order(20)
  @Bean
  @ConditionalOnProperty(name = {"value.multi-tenancy.http-header-strategy-enabled"}, havingValue = "true", matchIfMissing = true)
  public TenantIdentifierStrategy tenantIdentifierHttpHeaderStrategy(MultiTenancyConfigurationProperties properties) {
    return new TenantIdentifierHttpHeaderStrategy(properties.getTenantIdHttpHeader(), properties
        .getHeaderPaths().<String>toArray(new String[0]));
  }
  
  @Order(30)
  @Bean
  @ConditionalOnProperty(name = {"value.multi-tenancy.service-request-strategy-enabled"}, havingValue = "true", matchIfMissing = true)
  @ConditionalOnClass(name = {"com.value.buildingblocks.jwt.core.authenticaton.JsonWebTokenAuthentication"})
  public TenantIdentifierStrategy tenantIdentifierServiceRequestStrategy(ServiceApiAuthenticationProperties serviceApiProperties, MultiTenancyConfigurationProperties m10yProperties) {
    return new TenantIdentifierServiceRequestStrategy(m10yProperties.getTenantIdHttpHeader(), serviceApiProperties
        .getRequiredScope());
  }
  
  @Bean({"tenantFilter"})
  @ConditionalOnMissingBean
  @ConfigurationProperties("value.multi-tenancy.tenant-filter")
  public TenantFilter tenantFilter(TenantProvider tenantProvider, List<TenantIdentifierStrategy> tenantIdentifierReaders) {
    return new TenantFilter(tenantProvider, tenantIdentifierReaders);
  }
  
  @Bean
  @Qualifier("interServiceClientHttpRequestInterceptor")
  @Order(400)
  public TenantHeaderRequestInterceptor tenantHeaderRequestInterceptor(MultiTenancyConfigurationProperties multiTenancyConfigurationProperties) {
    return new TenantHeaderRequestInterceptor(multiTenancyConfigurationProperties.getTenantIdHttpHeader());
  }
  
  @Bean
  public TenantAwareCacheResolverBeanPostProcessor tenantAwareCacheResolverBeanPostProcessor(MultiTenancyConfigurationProperties properties) {
    return new TenantAwareCacheResolverBeanPostProcessor(properties);
  }
  
  @Bean
  @ConditionalOnBean({CacheManager.class})
  public CachingConfigurer tenantAwareCacheConfigurer(CacheManager cacheManager) {
    return (CachingConfigurer)new TenantAwareCacheConfigurer(cacheManager);
  }
  
  @Bean
  @ContextScoped
  public BehaviorExtensionConfiguration behaviorExtensionConfiguration() {
    return new BehaviorExtensionConfiguration();
  }
  
  @Bean
  @ConditionalOnMissingBean({ContextSupplier.class})
  public ContextSupplier contextSupplier() {
    return new TenantContextSupplier();
  }
  
  @Bean
  @ConditionalOnMissingBean({ContextSetter.class})
  public ContextSetter contextSetter(TenantProvider tenantProvider) {
    return new TenantContextSetter(tenantProvider);
  }
  
  @Bean
  @ConditionalOnMissingBean({ContextEnumerationSupplier.class})
  public ContextEnumerationSupplier contextEnumerationSupplier(TenantProvider tenantProvider) {
    return new TenantContextEnumerationSupplier(tenantProvider);
  }
  
  @ConditionalOnBean({CopyHeaderInterceptor.class})
  @Configuration
  static class CopyHeaderInterceptorConfig implements InitializingBean {
    private CopyHeaderInterceptor copyHeaderInterceptor;
    
    private MultiTenancyConfigurationProperties properties;
    
    public CopyHeaderInterceptorConfig(CopyHeaderInterceptor copyHeaderInterceptor, MultiTenancyConfigurationProperties properties) {
      this.copyHeaderInterceptor = copyHeaderInterceptor;
      this.properties = properties;
    }
    
    public void afterPropertiesSet() throws Exception {
      this.copyHeaderInterceptor.addBlacklistedHeader(this.properties.getTenantIdHttpHeader());
    }
    
    public CopyHeaderInterceptor getCopyHeaderInterceptor() {
      return this.copyHeaderInterceptor;
    }
    
    public void setCopyHeaderInterceptor(CopyHeaderInterceptor copyHeaderInterceptor) {
      this.copyHeaderInterceptor = copyHeaderInterceptor;
    }
  }
}
