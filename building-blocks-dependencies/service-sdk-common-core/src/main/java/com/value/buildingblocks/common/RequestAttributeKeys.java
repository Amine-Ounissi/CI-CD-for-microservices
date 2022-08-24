package com.value.buildingblocks.common;

public class RequestAttributeKeys {
  public static final String ORIGINATING_USER_JWT = "com.value.buildingblocks.backend.security.auth.config.SecurityContextUtil.ORIGINATING_USER_JWT";
  
  protected RequestAttributeKeys() {
    throw new AssertionError("Private constructor");
  }
}
