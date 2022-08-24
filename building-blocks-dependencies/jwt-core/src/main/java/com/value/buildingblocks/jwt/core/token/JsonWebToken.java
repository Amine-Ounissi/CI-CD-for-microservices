package com.value.buildingblocks.jwt.core.token;

public interface JsonWebToken<T, C extends JsonWebTokenClaimsSet> {
  T getSerializedToken();
  
  C getClaimsSet();
}
