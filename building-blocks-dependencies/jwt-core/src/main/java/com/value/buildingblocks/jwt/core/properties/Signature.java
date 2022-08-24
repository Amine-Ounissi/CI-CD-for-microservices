package com.value.buildingblocks.jwt.core.properties;

import com.nimbusds.jose.JWSAlgorithm;
import java.util.List;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

public class Signature extends ActiveKeys {
  private static final String DEFAULT_ALGORITHM = JWSAlgorithm.HS256.getName();
  
  private String algorithm = DEFAULT_ALGORITHM;
  
  @NestedConfigurationProperty
  private List<TokenKey> keys;
  
  @NestedConfigurationProperty
  private TokenKey key = new TokenKey();
  
  public JWSAlgorithm getAlgorithm() {
    return JWSAlgorithm.parse(this.algorithm);
  }
  
  public void setAlgorithm(String algorithm) {
    this.algorithm = algorithm;
  }
  
  public TokenKey getKey() {
    return this.key;
  }
  
  public void setKey(TokenKey key) {
    this.key = key;
  }
  
  public List<TokenKey> getKeys() {
    return this.keys;
  }
  
  public void setKeys(List<TokenKey> keys) {
    this.keys = keys;
  }
}
