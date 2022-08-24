package com.value.buildingblocks.jwt.core.properties;

import com.nimbusds.jose.EncryptionMethod;
import com.nimbusds.jose.JWEAlgorithm;
import java.util.List;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

public class Encryption extends ActiveKeys {
  private String encryptionMethod = EncryptionMethod.A128CBC_HS256.getName();
  
  private String algorithm = JWEAlgorithm.DIR.getName();
  
  @NestedConfigurationProperty
  private List<TokenKey> keys;
  
  @NestedConfigurationProperty
  private TokenKey key = new TokenKey();
  
  public EncryptionMethod getMethod() {
    return EncryptionMethod.parse(this.encryptionMethod);
  }
  
  public void setMethod(String method) {
    this.encryptionMethod = method;
  }
  
  public JWEAlgorithm getAlgorithm() {
    return JWEAlgorithm.parse(this.algorithm);
  }
  
  public void setAlgorithm(String algo) {
    this.algorithm = algo;
  }
  
  public TokenKey getKey() {
    return this.key;
  }
  
  public void setKey(TokenKey tokenKey) {
    this.key = tokenKey;
  }
  
  public List<TokenKey> getKeys() {
    return this.keys;
  }
  
  public void setKeys(List<TokenKey> tokenKeys) {
    this.keys = tokenKeys;
  }
}
