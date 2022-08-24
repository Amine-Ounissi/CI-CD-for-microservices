package com.value.buildingblocks.context;

import com.value.buildingblocks.multitenancy.MultiTenancyAutoConfiguration;
import java.util.Collections;
import org.springframework.beans.factory.config.CustomScopeConfigurer;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@AutoConfigureAfter({MultiTenancyAutoConfiguration.class})
@EnableConfigurationProperties({ContextProperties.class})
public class ContextScopeAutoConfiguration {
  @Bean
  public static CustomScopeConfigurer contextScopeConfigurer(ContextScope scope) {
    CustomScopeConfigurer scopeConfigurer = new CustomScopeConfigurer();
    scopeConfigurer.addScope("context", scope);
    return scopeConfigurer;
  }
  
  @Bean
  public ContextScope contextScope(ContextSupplier contextSupplier) {
    return new ContextScope(contextSupplier);
  }
  
  @Bean
  public ContextEnvironmentConfigurer contextEnvironmentConfigurer(ContextSupplier contextSupplier, ContextEnumerationSupplier contextEnumerationSupplier, ContextSetter contextSetter, ContextProperties contextProperties) {
    return new ContextEnvironmentConfigurer(contextSupplier, contextEnumerationSupplier, contextSetter, contextProperties);
  }
  
  @ConditionalOnMissingBean
  @Bean
  public ContextSupplier contextSupplier() {
    return () -> ContextConstants.EMPTY_CONTEXT_QUALIFIER;
  }
  
  @ConditionalOnMissingBean
  @Bean
  public ContextSetter contextSetter() {
    return context -> {
      
      };
  }
  
  @ConditionalOnMissingBean
  @Bean
  public ContextEnumerationSupplier contextEnumerationSupplier() {
    return Collections::emptyList;
  }
}
