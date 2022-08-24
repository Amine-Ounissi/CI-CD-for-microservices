package com.value.buildingblocks.jwt.core.properties;

public class JsonWebTokenInputTypeProperties {
  private String name;
  
  public JsonWebTokenInputTypeProperties(String name) {
    this.name = name;
  }
  
  public String getName() {
    return this.name;
  }
  
  public void setName(String name) {
    this.name = name;
  }
}
