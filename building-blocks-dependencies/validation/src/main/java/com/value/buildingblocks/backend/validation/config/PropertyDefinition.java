package com.value.buildingblocks.backend.validation.config;

import javax.validation.constraints.NotBlank;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class PropertyDefinition {
  @NotBlank
  private String propertyName;
  
  private PropertyType type = PropertyType.STRING;
  
  public String getPropertyName() {
    return this.propertyName;
  }
  
  public void setPropertyName(String propertyName) {
    this.propertyName = propertyName;
  }
  
  public PropertyType getType() {
    return this.type;
  }
  
  public void setType(PropertyType type) {
    this.type = type;
  }
  
  public String toString() {
    return ToStringBuilder.reflectionToString(this, ConfigToStringStyle.getInstance());
  }
}
