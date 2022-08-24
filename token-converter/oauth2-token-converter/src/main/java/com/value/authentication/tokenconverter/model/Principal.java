package com.value.authentication.tokenconverter.model;

import java.util.ArrayList;
import java.util.List;

public class Principal {
  private String userName;
  
  private List<String> authorities = new ArrayList<>();
  
  public String getUserName() {
    return this.userName;
  }
  
  public void setUserName(String userName) {
    this.userName = userName;
  }
  
  public List<String> getAuthorities() {
    return this.authorities;
  }
  
  public void setAuthorities(List<String> authorities) {
    this.authorities = authorities;
  }
}
