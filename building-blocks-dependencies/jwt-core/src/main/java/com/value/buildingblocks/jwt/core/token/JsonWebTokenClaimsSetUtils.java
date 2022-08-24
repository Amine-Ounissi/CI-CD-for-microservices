package com.value.buildingblocks.jwt.core.token;

import com.value.buildingblocks.jwt.core.authenticaton.JsonWebTokenAuthentication;
import java.util.Optional;
import java.util.function.Function;
import org.springframework.security.core.Authentication;

public final class JsonWebTokenClaimsSetUtils {
  public static Optional getClaim(Authentication authentication, String claim) {
    if (!JsonWebTokenAuthentication.class.isInstance(authentication))
      return Optional.empty(); 
    JsonWebTokenAuthentication jwtAuth = (JsonWebTokenAuthentication)authentication;
    Object details = jwtAuth.getDetails();
    if (details == null || !JsonWebTokenClaimsSet.class.isAssignableFrom(details.getClass()))
      return Optional.empty(); 
    return ((JsonWebTokenClaimsSet)details).getClaim(claim);
  }
  
  public static <V extends JsonWebTokenClaimsSet, R extends Optional> R getClaim(Authentication authentication, Function<V, R> getClaimFunction) {
    if (!JsonWebTokenAuthentication.class.isInstance(authentication))
      return (R)Optional.empty(); 
    JsonWebTokenAuthentication jwtAuth = (JsonWebTokenAuthentication)authentication;
    Object details = jwtAuth.getDetails();
    if (details == null || !JsonWebTokenClaimsSet.class.isAssignableFrom(details.getClass()))
      return (R)Optional.empty(); 
    return getClaimFunction.apply((V)details);
  }
}
