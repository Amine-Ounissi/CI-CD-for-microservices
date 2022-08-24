package com.value.buildingblocks.jwt.external.token;

import com.value.buildingblocks.jwt.core.token.JsonWebToken;
import java.time.ZonedDateTime;
import java.util.Objects;

public class ExternalJwt implements JsonWebToken<String, ExternalJwtClaimsSet> {
  private final String token;
  
  private final ExternalJwtClaimsSet claimsSet;
  
  public ExternalJwt(String serializedToken, ExternalJwtClaimsSet claimsSet) {
    Objects.requireNonNull(serializedToken, "Serialized token is not provided");
    Objects.requireNonNull(claimsSet, "Token claimsSet are not provided");
    this.token = serializedToken;
    this.claimsSet = claimsSet;
  }
  
  public String getSerializedToken() {
    return this.token;
  }
  
  public ExternalJwtClaimsSet getClaimsSet() {
    return this.claimsSet;
  }
  
  public int getExpiresIn() {
    return ((Integer)ExternalJwtUtils.getExpiresInSec(getClaimsSet()).orElse(Integer.valueOf(0))).intValue();
  }
  
  public boolean isTokenExpired(ZonedDateTime currentTime) throws IllegalArgumentException {
    return ExternalJwtUtils.isTokenExpired(getClaimsSet(), currentTime);
  }
  
  public boolean isReadyForRenew(ZonedDateTime currentTime, long renewPeriodSec) throws IllegalArgumentException {
    return ExternalJwtUtils.isReadyForRenew(getClaimsSet(), currentTime, renewPeriodSec).booleanValue();
  }
}
