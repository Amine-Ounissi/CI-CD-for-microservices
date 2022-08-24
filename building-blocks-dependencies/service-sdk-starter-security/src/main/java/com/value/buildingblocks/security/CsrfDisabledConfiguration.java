package com.value.buildingblocks.security;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

@Configuration
@ConditionalOnProperty(value = {"value.security.csrf.enabled"}, havingValue = "false", matchIfMissing = false)
public class CsrfDisabledConfiguration {
  public static final int SECURITY_CSRF_ENABLED_ORDER = 101;
  
  @Bean
  @Order(101)
  public HttpSecurityConfigurer disableCsrfConfigurer() {
    return new CsrfDisabledHttpSecurityConfigurer();
  }
}
