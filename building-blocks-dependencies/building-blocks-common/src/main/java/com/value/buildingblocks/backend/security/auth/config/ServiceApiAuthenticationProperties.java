package com.value.buildingblocks.backend.security.auth.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("value.security.internal.authentication")
public class ServiceApiAuthenticationProperties {
  public static final String DEFAULT_REQUIRED_SCOPE = "api:service";
  
  private String requiredScope = "api:service";
  
  public String getRequiredScope() {
    return this.requiredScope;
  }
  
  public void setRequiredScope(String requiredScope) {
    this.requiredScope = requiredScope;
  }
}
