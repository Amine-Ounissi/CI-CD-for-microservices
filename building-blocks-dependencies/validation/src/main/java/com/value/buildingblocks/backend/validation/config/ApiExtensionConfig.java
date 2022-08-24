package com.value.buildingblocks.backend.validation.config;

import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("value.api.extensions")
public class ApiExtensionConfig {
  private Map<String, PropertySet> propertySets = new HashMap<>();
  
  private Map<String, String> classes = new HashMap<>();
  
  private boolean persistEmptyStringValues = false;
  
  private int maxKeyLength = 50;
  
  private int maxValueLength = 500;
  
  private int maxProperties = 100;
  
  public Map<String, PropertySet> getPropertySets() {
    return this.propertySets;
  }
  
  public void setPropertySets(Map<String, PropertySet> propertySets) {
    this.propertySets = propertySets;
  }
  
  public Map<String, String> getClasses() {
    return this.classes;
  }
  
  public void setClasses(Map<String, String> classes) {
    this.classes = classes;
  }
  
  public boolean isPersistEmptyStringValues() {
    return this.persistEmptyStringValues;
  }
  
  public void setPersistEmptyStringValues(boolean persistEmptyStringValues) {
    this.persistEmptyStringValues = persistEmptyStringValues;
  }
  
  public int getMaxKeyLength() {
    return this.maxKeyLength;
  }
  
  public int getMaxValueLength() {
    return this.maxValueLength;
  }
  
  public int getMaxProperties() {
    return this.maxProperties;
  }
  
  public void setMaxKeyLength(int maxKeyLength) {
    this.maxKeyLength = maxKeyLength;
  }
  
  public void setMaxValueLength(int maxValueLength) {
    this.maxValueLength = maxValueLength;
  }
  
  public void setMaxProperties(int maxProperties) {
    this.maxProperties = maxProperties;
  }
  
  public String toString() {
    return ToStringBuilder.reflectionToString(this, ConfigToStringStyle.getInstance());
  }
}
