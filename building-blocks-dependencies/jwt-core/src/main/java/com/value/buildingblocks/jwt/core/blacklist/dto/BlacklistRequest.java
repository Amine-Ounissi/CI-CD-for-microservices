package com.value.buildingblocks.jwt.core.blacklist.dto;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

public class BlacklistRequest {
  protected String value;
  
  protected Type type;
  
  protected String reason;
  
  protected String issuer;
  
  protected long created;
  
  protected long expiry;
  
  protected BlacklistRequest() {}
  
  public BlacklistRequest(String value, Type type, String reason, String issuer) {
    this.value = value;
    this.type = type;
    this.reason = reason;
    this.issuer = issuer;
  }
  
  public BlacklistRequest withCreatedTime(ZonedDateTime created) {
    this.created = created.withZoneSameInstant(ZoneOffset.UTC).toEpochSecond();
    return this;
  }
  
  public BlacklistRequest withExpiryTime(ZonedDateTime expiry) {
    this.expiry = expiry.withZoneSameInstant(ZoneOffset.UTC).toEpochSecond();
    return this;
  }
  
  public String getValue() {
    return this.value;
  }
  
  public Type getType() {
    return this.type;
  }
  
  public String getIssuer() {
    return this.issuer;
  }
  
  public String getReason() {
    return this.reason;
  }
  
  public ZonedDateTime getCreatedTime() {
    return Instant.ofEpochSecond(this.created).atZone(ZoneOffset.UTC);
  }
  
  public ZonedDateTime getExpiryTime() {
    return Instant.ofEpochSecond(this.expiry).atZone(ZoneOffset.UTC);
  }
  
  public enum Type {
    JTI("token"),
    USERNAME("username");
    
    private String name;
    
    Type(String name) {
      this.name = name;
    }
  }
}
