package com.value.buildingblocks.backend.api;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "value.api")
public class MvcApiProperties {
  boolean registerFormatters = true;
  
  boolean zonedDateTimeDeserializerEnabled = true;
  
  boolean offsetDateTimeDeserializerEnabled = true;
  
  public boolean isRegisterFormatters() {
    return this.registerFormatters;
  }
  
  public void setRegisterFormatters(boolean registerFormatters) {
    this.registerFormatters = registerFormatters;
  }
  
  public boolean isZonedDateTimeDeserializerEnabled() {
    return this.zonedDateTimeDeserializerEnabled;
  }
  
  public void setZonedDateTimeDeserializerEnabled(boolean zonedDateTimeDeserializerEnabled) {
    this.zonedDateTimeDeserializerEnabled = zonedDateTimeDeserializerEnabled;
  }
  
  public boolean isOffsetDateTimeDeserializerEnabled() {
    return this.offsetDateTimeDeserializerEnabled;
  }
  
  public void setOffsetDateTimeDeserializerEnabled(boolean offsetDateTimeDeserializerEnabled) {
    this.offsetDateTimeDeserializerEnabled = offsetDateTimeDeserializerEnabled;
  }
}
