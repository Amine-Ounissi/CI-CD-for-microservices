package com.value.buildingblocks.backend.security.auth;

public enum AuthLevel {
  APPROVED("vds-auth-approved"),
  REJECTED("vds-auth-rejected"),
  STEP_UP("vds-auth-stepup"),
  RE_AUTH("vds-auth-reauth");
  
  private final String text;
  
  AuthLevel(String text) {
    this.text = text;
  }
  
  public String toString() {
    return this.text;
  }
}
