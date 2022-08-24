package com.value.buildingblocks.configuration.filter;

import org.springframework.boot.autoconfigure.AutoConfigurationImportFilter;
import org.springframework.boot.autoconfigure.AutoConfigurationMetadata;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;

public abstract class AbstractAutoConfigurationImportFilter implements AutoConfigurationImportFilter, EnvironmentAware {
  private final String excludedConfig;
  
  protected Environment environment;
  
  public AbstractAutoConfigurationImportFilter(String excludedConfig) {
    this.excludedConfig = excludedConfig;
  }
  
  public boolean[] match(String[] autoConfigurationClasses, AutoConfigurationMetadata autoConfigurationMetadata) {
    boolean[] match = new boolean[autoConfigurationClasses.length];
    for (int i = 0; i < autoConfigurationClasses.length; i++)
      match[i] = (!filterCondition() || !matchingAutoConfig(autoConfigurationClasses[i])); 
    return match;
  }
  
  protected abstract boolean filterCondition();
  
  private boolean matchingAutoConfig(String className) {
    return (className != null && className.equalsIgnoreCase(this.excludedConfig));
  }
  
  public void setEnvironment(Environment environment) {
    this.environment = environment;
  }
  
  public Environment getEnvironment() {
    return this.environment;
  }
}
