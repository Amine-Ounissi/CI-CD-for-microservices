package com.value.buildingblocks.jwt.core.token;

import java.util.Map;
import java.util.Optional;

public interface JsonWebTokenClaimsSet {
  Map<String, Object> getClaims();
  
  Optional<Object> getClaim(String paramString);
}
