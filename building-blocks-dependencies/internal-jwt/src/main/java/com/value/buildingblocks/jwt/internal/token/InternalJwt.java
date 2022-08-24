package com.value.buildingblocks.jwt.internal.token;

import com.value.buildingblocks.jwt.core.token.JsonWebToken;
import java.util.Objects;

public class InternalJwt implements JsonWebToken<String, InternalJwtClaimsSet> {
  private final String token;
  
  private final InternalJwtClaimsSet claimsSet;
  
  public InternalJwt(String token, InternalJwtClaimsSet claimsSet) {
    Objects.requireNonNull(token, "Token need to be provided");
    Objects.requireNonNull(claimsSet, "Token claimsSet need to be provided");
    this.token = token;
    this.claimsSet = claimsSet;
  }
  
  public String getSerializedToken() {
    return this.token;
  }
  
  public InternalJwtClaimsSet getClaimsSet() {
    return this.claimsSet;
  }
}
