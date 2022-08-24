package com.value.buildingblocks.jwt.core.properties;

import java.util.Objects;

public class TokenKey {
  private TokenKeyType type = TokenKeyType.ENV;
  
  private TokenKeyInput input = TokenKeyInput.RAW;
  
  private String password;
  
  private String alias;
  
  private String aliasPassword;
  
  private String id;
  
  private String value;
  
  public String getId() {
    return this.id;
  }
  
  public void setId(String keyId) {
    this.id = keyId;
  }
  
  public TokenKeyType getType() {
    return this.type;
  }
  
  public void setType(TokenKeyType type) {
    this.type = type;
  }
  
  public String getValue() {
    return this.value;
  }
  
  public TokenKeyInput getInput() {
    return this.input;
  }
  
  public void setInput(TokenKeyInput input) {
    this.input = input;
  }
  
  public void setValue(String value) {
    this.value = value;
  }
  
  public String getPassword() {
    return this.password;
  }
  
  public void setPassword(String password) {
    this.password = password;
  }
  
  public String getAlias() {
    return this.alias;
  }
  
  public void setAlias(String alias) {
    this.alias = alias;
  }
  
  public String getAliasPassword() {
    return this.aliasPassword;
  }
  
  public void setAliasPassword(String aliasPassword) {
    this.aliasPassword = aliasPassword;
  }
  
  public boolean equals(Object o) {
    if (this == o)
      return true; 
    if (o == null || getClass() != o.getClass())
      return false; 
    TokenKey tokenKey = (TokenKey)o;
    return (this.type == tokenKey.type && this.input == tokenKey.input && Objects.equals(this.password, tokenKey.password) && 
      Objects.equals(this.alias, tokenKey.alias) && Objects.equals(this.aliasPassword, tokenKey.aliasPassword) && 
      Objects.equals(this.id, tokenKey.id) && Objects.equals(this.value, tokenKey.value));
  }
  
  public int hashCode() {
    return Objects.hash(new Object[] { this.type, this.input, this.password, this.alias, this.aliasPassword, this.id, this.value });
  }
}
