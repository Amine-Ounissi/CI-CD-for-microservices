package com.value.buildingblocks.access;

import com.value.buildingblocks.jwt.internal.authentication.InternalJwtAuthentication;
import com.value.buildingblocks.security.HttpSecurityConfigurer;
import com.value.buildingblocks.security.OriginatingUserFilterConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationPropertiesBinding;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.ObjectPostProcessor;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;

@Configuration
@EnableConfigurationProperties({ScopedAccessProperties.class})
@ConditionalOnProperty(value = {"value.security.rules-enabled"}, havingValue = "true", matchIfMissing = false)
@ConditionalOnClass({InternalJwtAuthentication.class})
@AutoConfigureAfter(name = {"com.value.buildingblocks.backend.security.auth.config.InternalJwtConsumerAutoConfiguration"})
@Import({OriginatingUserFilterConfiguration.class})
public class ScopedAccessConfiguration {
  @Bean
  @Order(25)
  public HttpSecurityConfigurer scopedAccessHttpSecurityFilterConfigurer(ScopedAccessProperties properties, ObjectPostProcessor<Object> objectPostProcessor) throws Exception {
    ScopedAccessSecurityAdaptor adaptor = new ScopedAccessSecurityAdaptor(properties);
    objectPostProcessor.postProcess(adaptor);
    HttpSecurity httpSecurity = adaptor.getHttpSecurity();
    httpSecurity.build();
    FilterSecurityInterceptor f = (FilterSecurityInterceptor)httpSecurity.getSharedObject(FilterSecurityInterceptor.class);
    f.setObserveOncePerRequest(false);
    ScopedAccessSecurityFilter filter = new ScopedAccessSecurityFilter(f);
    return new ScopedAccessHttpSecurityFilterConfigurer(filter);
  }
  
  @Bean
  @ConfigurationPropertiesBinding
  public AccessRuleConverter scopedAccessPropertiesConverter() {
    return new AccessRuleConverter();
  }
}
