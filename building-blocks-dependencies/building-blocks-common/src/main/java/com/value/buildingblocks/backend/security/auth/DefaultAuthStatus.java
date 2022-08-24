package com.value.buildingblocks.backend.security.auth;

public class DefaultAuthStatus implements AuthStatus {
  private AuthLevel authLevel;
  
  private String message;
  
  public DefaultAuthStatus() {}
  
  public DefaultAuthStatus(AuthLevel authLevel) {
    this.authLevel = authLevel;
    this.message = "";
  }
  
  public DefaultAuthStatus(AuthLevel authLevel, String message) {
    this.authLevel = authLevel;
    this.message = message;
  }
  
  public AuthLevel getAuthLevel() {
    return this.authLevel;
  }
  
  public void setAuthLevel(AuthLevel authLevel) {
    this.authLevel = authLevel;
  }
  
  public String getMessage() {
    return this.message;
  }
  
  public void setMessage(String message) {
    this.message = message;
  }
  
  public String toString() {
    return String.format("DefaultAuthStatus{authLevel=%s, message='%s'}", new Object[] { this.authLevel, this.message });
  }
  
  public int hashCode() {
    int prime = 31;
    int result = 1;
    result = 31 * result + ((this.authLevel == null) ? 0 : this.authLevel.hashCode());
    result = 31 * result + ((this.message == null) ? 0 : this.message.hashCode());
    return result;
  }
  
  public boolean equals(Object obj) {
    if (this == obj)
      return true; 
    if (obj == null)
      return false; 
    if (getClass() != obj.getClass())
      return false; 
    DefaultAuthStatus other = (DefaultAuthStatus)obj;
    if (this.authLevel != other.authLevel)
      return false; 
    if (this.message == null) {
      if (other.message != null)
        return false; 
    } else if (!this.message.equals(other.message)) {
      return false;
    } 
    return true;
  }
}
