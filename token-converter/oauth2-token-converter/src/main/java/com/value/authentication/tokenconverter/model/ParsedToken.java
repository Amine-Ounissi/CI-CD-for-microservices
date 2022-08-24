package com.value.authentication.tokenconverter.model;

import java.util.Map;

public class ParsedToken {
  private Map<String, Object> claims;
  
  private String algorithm;
  
  private String keyId;
  
  public ParsedToken(Map<String, Object> claims, String algorithm, String keyId) {
    this.claims = claims;
    this.algorithm = algorithm;
    this.keyId = keyId;
  }
  
  public ParsedToken() {}
  
  public Map<String, Object> getClaims() {
    return this.claims;
  }
  
  public void setClaims(Map<String, Object> claims) {
    this.claims = claims;
  }
  
  public String getAlgorithm() {
    return this.algorithm;
  }
  
  public void setAlgorithm(String algorithm) {
    this.algorithm = algorithm;
  }
  
  public String getKeyId() {
    return this.keyId;
  }
  
  public void setKeyId(String keyId) {
    this.keyId = keyId;
  }
}
