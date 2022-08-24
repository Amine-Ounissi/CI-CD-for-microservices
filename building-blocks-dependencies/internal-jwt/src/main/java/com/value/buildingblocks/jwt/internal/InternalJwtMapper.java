package com.value.buildingblocks.jwt.internal;

import com.value.buildingblocks.jwt.internal.token.InternalJwtClaimsSet;
import java.util.Optional;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;

public interface InternalJwtMapper {
  default Optional<InternalJwtClaimsSet> claimSet(Authentication authentication,
    HttpServletRequest request) {
    return Optional.empty();
  }
  
  default void updateAuthorities(Set<String> roles, Authentication authentication) {}
  
  default Optional<String> getUserName(Authentication authentication) {
    return Optional.empty();
  }
}
