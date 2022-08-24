package com.value.buildingblocks.backend.communication.context;

import com.value.buildingblocks.backend.security.auth.AuthStatus;

public class OriginatorContext {
  private String securityContext;
  
  private String location;
  
  private LocationType locationType;
  
  private Long creationTime;
  
  private String userAgent;
  
  private String requestUuid;
  
  private AuthStatus authenticationStatus;
  
  public String getSecurityContext() {
    return this.securityContext;
  }
  
  public void setSecurityContext(String securityContext) {
    this.securityContext = securityContext;
  }
  
  public String getLocation() {
    return this.location;
  }
  
  public void setLocation(String location) {
    this.location = location;
  }
  
  public LocationType getLocationType() {
    return this.locationType;
  }
  
  public void setLocationType(LocationType locationType) {
    this.locationType = locationType;
  }
  
  public Long getCreationTime() {
    return this.creationTime;
  }
  
  public void setCreationTime(Long creationTime) {
    this.creationTime = creationTime;
  }
  
  public String getUserAgent() {
    return this.userAgent;
  }
  
  public void setUserAgent(String userAgent) {
    this.userAgent = userAgent;
  }
  
  public String getRequestUuid() {
    return this.requestUuid;
  }
  
  public void setRequestUuid(String requestUuid) {
    this.requestUuid = requestUuid;
  }
  
  public AuthStatus getAuthenticationStatus() {
    return this.authenticationStatus;
  }
  
  public void setAuthenticationStatus(AuthStatus authenticationStatus) {
    this.authenticationStatus = authenticationStatus;
  }
  
  public enum LocationType {
    HOSTNAME, IPV4, IPV6, UNKNOWN;
  }
}
