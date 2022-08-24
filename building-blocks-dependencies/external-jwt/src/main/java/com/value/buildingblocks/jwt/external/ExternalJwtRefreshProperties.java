package com.value.buildingblocks.jwt.external;

public class ExternalJwtRefreshProperties {
  private String ignorePattern = "/**/refresh";
  
  private String headerName = "X-ORIGINAL-URI";
  
  public String getIgnorePattern() {
    return this.ignorePattern;
  }
  
  public void setIgnorePattern(String ignorePattern) {
    this.ignorePattern = ignorePattern;
  }
  
  public String getHeaderName() {
    return this.headerName;
  }
  
  public void setHeaderName(String headerName) {
    this.headerName = headerName;
  }
}
