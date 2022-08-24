package com.value.authentication.tokenconverter.internaltoken.model;

import java.util.Map;
import lombok.Generated;

public class InternalTokenResponse {

  private String internalToken;

  private Map<String, Object> claims;

  @Generated
  public void setInternalToken(String internalToken) {
    this.internalToken = internalToken;
  }

  @Generated
  public void setClaims(Map<String, Object> claims) {
    this.claims = claims;
  }

  @Generated
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (!(o instanceof InternalTokenResponse)) {
      return false;
    }
    InternalTokenResponse other = (InternalTokenResponse) o;
    if (!other.canEqual(this)) {
      return false;
    }
    Object this$internalToken = getInternalToken(), other$internalToken = other.getInternalToken();
    if ((this$internalToken == null) ? (other$internalToken != null)
      : !this$internalToken.equals(other$internalToken)) {
      return false;
    }
    Object this$claims = getClaims(), other$claims = other.getClaims();
    return !((this$claims == null) ? (other$claims != null) : !this$claims.equals(other$claims));
  }

  @Generated
  protected boolean canEqual(Object other) {
    return other instanceof InternalTokenResponse;
  }

  @Generated
  public int hashCode() {
    int PRIME = 59;
    int result = 1;
    Object $internalToken = getInternalToken();
    result = result * 59 + (($internalToken == null) ? 43 : $internalToken.hashCode());
    Object $claims = getClaims();
    return result * 59 + (($claims == null) ? 43 : $claims.hashCode());
  }

  @Generated
  public String toString() {
    return "InternalTokenResponse(internalToken=" + getInternalToken() + ", claims=" + getClaims()
      + ")";
  }

  @Generated
  public InternalTokenResponse(String internalToken, Map<String, Object> claims) {
    this.internalToken = internalToken;
    this.claims = claims;
  }

  @Generated
  public String getInternalToken() {
    return this.internalToken;
  }

  @Generated
  public Map<String, Object> getClaims() {
    return this.claims;
  }
}
