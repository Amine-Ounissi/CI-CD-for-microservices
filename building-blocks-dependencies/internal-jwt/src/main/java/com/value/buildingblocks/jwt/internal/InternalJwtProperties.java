package com.value.buildingblocks.jwt.internal;

import com.value.buildingblocks.jwt.core.properties.JsonWebTokenInputTypeProperties;
import com.value.buildingblocks.jwt.core.properties.JsonWebTokenProperties;

abstract class InternalJwtProperties extends JsonWebTokenProperties {
  public static final String INTERNAL_JWT_PROPERTIES_NAMESPACE = "sso.jwt.internal";
  
  private JsonWebTokenInputTypeProperties header = new JsonWebTokenInputTypeProperties("Authorization");
  
  public InternalJwtProperties() {
    setType("signed");
  }
  
  public JsonWebTokenInputTypeProperties getHeader() {
    return this.header;
  }
  
  public void setHeader(JsonWebTokenInputTypeProperties header) {
    this.header = header;
  }
}
