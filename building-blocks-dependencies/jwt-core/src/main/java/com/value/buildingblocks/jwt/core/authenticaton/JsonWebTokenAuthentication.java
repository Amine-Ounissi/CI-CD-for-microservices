package com.value.buildingblocks.jwt.core.authenticaton;

import com.value.buildingblocks.jwt.core.token.JsonWebTokenClaimsSet;
import org.springframework.security.authentication.AbstractAuthenticationToken;

public abstract class JsonWebTokenAuthentication extends AbstractAuthenticationToken {
  private final JsonWebTokenUserDetails principal;
  
  private final String serializedToken;
  
  public JsonWebTokenAuthentication(JsonWebTokenUserDetails principal, JsonWebTokenClaimsSet details, String serializedToken) {
    super(principal.getAuthorities());
    this.principal = principal;
    this.serializedToken = serializedToken;
    setDetails(details);
  }
  
  public JsonWebTokenUserDetails getPrincipal() {
    return this.principal;
  }
  
  public JsonWebTokenClaimsSet getDetails() {
    return (JsonWebTokenClaimsSet)super.getDetails();
  }
  
  public String getCredentials() {
    return this.serializedToken;
  }
  
  public void setDetails(JsonWebTokenClaimsSet details) {
    super.setDetails(details);
  }
  
  public boolean equals(Object obj) {
    if (this == obj)
      return true; 
    if (obj == null || getClass() != obj.getClass())
      return false; 
    if (!super.equals(obj))
      return false; 
    JsonWebTokenAuthentication that = (JsonWebTokenAuthentication)obj;
    if ((this.principal != null) ? !this.principal.equals(that.principal) : (that.principal != null))
      return false; 
    return (this.serializedToken != null) ? this.serializedToken.equals(that.serializedToken) : ((that.serializedToken == null));
  }
  
  public int hashCode() {
    int result = super.hashCode();
    result = 31 * result + ((this.principal != null) ? this.principal.hashCode() : 0);
    result = 31 * result + ((this.serializedToken != null) ? this.serializedToken.hashCode() : 0);
    return result;
  }
}
