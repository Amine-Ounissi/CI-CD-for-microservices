package com.value.buildingblocks.backend.validation.config;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class PropertySet {
  private List<PropertyDefinition> properties = new ArrayList<>();
  
  public List<PropertyDefinition> getProperties() {
    return this.properties;
  }
  
  public void setProperties(List<PropertyDefinition> properties) {
    this.properties = properties;
  }
  
  public String toString() {
    return ToStringBuilder.reflectionToString(this, ConfigToStringStyle.getInstance());
  }
}
