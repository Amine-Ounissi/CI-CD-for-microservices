package com.value.buildingblocks.backend.security.auth.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class CustomPermissionEvaluatorSecurityConfig extends GlobalMethodSecurityConfiguration {

  @Autowired
  @Lazy
  private FunctionalAccessControl functionalAccessControl;

  protected MethodSecurityExpressionHandler createExpressionHandler() {
    return new CustomSecurityExpressionHandler(this.functionalAccessControl);
  }
}
