package com.value.buildingblocks.jwt.external;

import com.value.buildingblocks.jwt.external.token.ExternalJwtClaimsSet;
import java.util.Optional;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;

public interface ExternalJwtMapper {
  default Optional<ExternalJwtClaimsSet> claimSet(Authentication authentication,
    HttpServletRequest request) {
    return Optional.empty();
  }
  
  default void updateAuthorities(Set<String> authorities, Authentication authentication) {}
  
  default Optional<String> getUserName(Authentication authentication) {
    return Optional.empty();
  }
}
