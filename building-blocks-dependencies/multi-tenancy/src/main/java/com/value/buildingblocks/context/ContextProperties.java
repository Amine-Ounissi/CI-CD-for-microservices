package com.value.buildingblocks.context;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("value.context")
public class ContextProperties {
  private boolean errorOnInvalidPropertyName = true;
  
  public boolean isErrorOnInvalidPropertyName() {
    return this.errorOnInvalidPropertyName;
  }
  
  public void setErrorOnInvalidPropertyName(boolean errorOnInvalidPropertyName) {
    this.errorOnInvalidPropertyName = errorOnInvalidPropertyName;
  }
}
