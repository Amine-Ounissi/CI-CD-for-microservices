package com.value.buildingblocks.security;

import org.springframework.boot.autoconfigure.AutoConfigurationImportFilter;
import org.springframework.boot.autoconfigure.AutoConfigurationMetadata;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;

public class CsrfImportFilter implements AutoConfigurationImportFilter, EnvironmentAware {
  public static final String VALUE_SECURITY_CSRF_ENABLED = "value.security.csrf.enabled";
  
  private static final String EXCLUDED_CONFIG = "com.value.buildingblocks.security.csrf.AutoCsrfWebSecurityConfiguration";
  
  private Environment environment;
  
  public boolean[] match(String[] autoConfigurationClasses, AutoConfigurationMetadata autoConfigurationMetadata) {
    boolean[] match = new boolean[autoConfigurationClasses.length];
    boolean isCsrfEnabled = isCsrfEnabled();
    for (int i = 0; i < autoConfigurationClasses.length; i++)
      match[i] = (!isCsrfEnabled || !isAutoCsrfWebSecurityConfiguration(autoConfigurationClasses[i])); 
    return match;
  }
  
  private boolean isCsrfEnabled() {
    return "false".equalsIgnoreCase(this.environment.getProperty("value.security.csrf.enabled", "true"));
  }
  
  private boolean isAutoCsrfWebSecurityConfiguration(String className) {
    return "com.value.buildingblocks.security.csrf.AutoCsrfWebSecurityConfiguration".equalsIgnoreCase(className);
  }
  
  public void setEnvironment(Environment environment) {
    this.environment = environment;
  }
}
