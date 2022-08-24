package com.value.buildingblocks.jwt.core.properties;

import javax.annotation.PostConstruct;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

public abstract class JsonWebTokenProperties {
  public static final String JWT_PROPERTY_NAMESPACE = "sso.jwt";
  
  private static final int DEFAULT_EXPIRATION = 300;
  
  private static final int DEFAULT_TOKEN_THRESHOLD_SIZE = 2048;
  
  private boolean enabled = true;
  
  private String type;
  
  private int tokenHeaderWarnSize = 2048;
  
  private int expiration = 300;
  
  @NestedConfigurationProperty
  private Signature signature = new Signature();
  
  @NestedConfigurationProperty
  private Encryption encryption = new Encryption();
  
  public String getType() {
    return this.type;
  }
  
  public void setType(String type) {
    this.type = type;
  }
  
  public int getTokenHeaderWarnSize() {
    return this.tokenHeaderWarnSize;
  }
  
  public void setTokenHeaderWarnSize(int tokenHeaderWarnSize) {
    this.tokenHeaderWarnSize = tokenHeaderWarnSize;
  }
  
  public int getExpiration() {
    return this.expiration;
  }
  
  public void setExpiration(int expiration) {
    this.expiration = expiration;
  }
  
  public Signature getSignature() {
    return this.signature;
  }
  
  public void setSignature(Signature signature) {
    this.signature = signature;
  }
  
  public Encryption getEncryption() {
    return this.encryption;
  }
  
  public void setEncryption(Encryption encryption) {
    this.encryption = encryption;
  }
  
  public boolean isEnabled() {
    return this.enabled;
  }
  
  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }
  
  @PostConstruct
  private void postConstruct() {
    this.signature.loadKeys(this.signature.getKey(), this.signature.getKeys());
    this.encryption.loadKeys(this.encryption.getKey(), this.encryption.getKeys());
  }
}
