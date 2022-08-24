package com.value.buildingblocks.idempotency;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import javax.servlet.Filter;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.CacheResolver;
import org.springframework.cache.interceptor.NamedCacheResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.validation.annotation.Validated;

@Configuration
@ConfigurationProperties("value.idempotency.double-submission-filter")
@ConditionalOnProperty(prefix = "value.idempotency.double-submission-filter", name = {"enabled"}, havingValue = "true", matchIfMissing = false)
@EnableCaching
@PropertySource({"classpath:double-submission-filter.properties"})
@Validated
public class DoubleSubmissionFilterConfig {
  private boolean enabled = false;
  
  @Size(min = 1)
  @NotNull
  private String[] filteredPaths;
  
  private Set<String> filteredHttpRequestMethods = new HashSet<>(Arrays.asList(new String[] { "POST", "PATCH" }));
  
  private String cacheName = "unknown-double-submission";
  
  private int timeToLive = 30;
  
  public boolean isEnabled() {
    return this.enabled;
  }
  
  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }
  
  public String[] getFilteredPaths() {
    return this.filteredPaths;
  }
  
  public void setFilteredPaths(String[] filteredPaths) {
    this.filteredPaths = filteredPaths;
  }
  
  public Set<String> getFilteredHttpRequestMethods() {
    return this.filteredHttpRequestMethods;
  }
  
  public void setFilteredHttpRequestMethods(Set<String> filteredHttpRequestMethods) {
    this.filteredHttpRequestMethods = filteredHttpRequestMethods;
  }
  
  public String getCacheName() {
    return this.cacheName;
  }
  
  public void setCacheName(String cacheName) {
    this.cacheName = sanitise(cacheName);
  }
  
  private String sanitise(String value) {
    return value.toLowerCase().replace(" ", "-");
  }
  
  public int getTimeToLive() {
    return this.timeToLive;
  }
  
  public void setTimeToLive(int timeToLive) {
    this.timeToLive = timeToLive;
  }
  
  @Bean({"idempotencyCacheResolver"})
  @ConditionalOnMissingBean(name = {"idempotencyCacheResolver"})
  public CacheResolver idempotencyRequestIdCacheResolver(@Autowired CacheManager cacheManager) {
    return (CacheResolver)new NamedCacheResolver(cacheManager, new String[] { getCacheName() });
  }
  
  @Bean
  public RequestIdCache idempotencyRequestIdCache() {
    return new RequestIdCacheImpl();
  }
  
  @Bean
  public DoubleSubmissionFilter doubleSubmissionFilter(@Autowired RequestIdCache idempotencyRequestIdCache) {
    return new DoubleSubmissionFilter(this.filteredHttpRequestMethods, idempotencyRequestIdCache);
  }
  
  @Bean
  public FilterRegistrationBean idempotencyFilterRegistration(@Autowired DoubleSubmissionFilter doubleSubmissionFilter) {
    FilterRegistrationBean registration = new FilterRegistrationBean();
    registration.setFilter((Filter)doubleSubmissionFilter);
    registration.addUrlPatterns(this.filteredPaths);
    registration.setName("doubleSubmissionFilter");
    registration.setOrder(1);
    return registration;
  }
}
