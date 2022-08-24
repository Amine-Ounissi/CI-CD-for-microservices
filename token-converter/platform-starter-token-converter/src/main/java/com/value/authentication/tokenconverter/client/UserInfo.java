package com.value.authentication.tokenconverter.client;

public class UserInfo {
  private final String leid;
  
  private final String inuid;
  
  public UserInfo(String inuid, String leid) {
    this.leid = leid;
    this.inuid = inuid;
  }
  
  public String getLeid() {
    return this.leid;
  }
  
  public String getInuid() {
    return this.inuid;
  }
}
