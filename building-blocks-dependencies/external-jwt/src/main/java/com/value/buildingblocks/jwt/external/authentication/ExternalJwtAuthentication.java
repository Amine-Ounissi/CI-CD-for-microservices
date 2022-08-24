package com.value.buildingblocks.jwt.external.authentication;

import com.value.buildingblocks.jwt.core.authenticaton.JsonWebTokenAuthentication;
import com.value.buildingblocks.jwt.core.authenticaton.JsonWebTokenUserDetails;
import com.value.buildingblocks.jwt.external.token.ExternalJwt;
import com.value.buildingblocks.jwt.external.token.ExternalJwtClaimsSet;
import java.util.ArrayList;
import java.util.List;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

public class ExternalJwtAuthentication extends JsonWebTokenAuthentication {
  public static final String DEFAULT_USER_NAME = "anonymous";
  
  public ExternalJwtAuthentication(ExternalJwt token) {
    super(getUserDetails(token.getClaimsSet()), token.getClaimsSet(), token.getSerializedToken());
    setAuthenticated(true);
  }
  
  private static JsonWebTokenUserDetails getUserDetails(ExternalJwtClaimsSet claimsSet) {
    return new JsonWebTokenUserDetails(claimsSet
        .getSubject().orElse("anonymous"), 
        getUserGrantedAuthorities(claimsSet), ((Boolean)claimsSet
        .isAccountNonExpired().orElse(Boolean.valueOf(false))).booleanValue(), ((Boolean)claimsSet
        .isAccountNonLocked().orElse(Boolean.valueOf(false))).booleanValue(), ((Boolean)claimsSet
        .isCredentialsNonExpired().orElse(Boolean.valueOf(false))).booleanValue(), ((Boolean)claimsSet
        .isAccountEnabled().orElse(Boolean.valueOf(true))).booleanValue());
  }
  
  private static List<GrantedAuthority> getUserGrantedAuthorities(ExternalJwtClaimsSet claimsSet) {
    List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
    claimsSet.getRoles().ifPresent(roles -> {
          for (String role : roles)
            grantedAuthorities.add(new SimpleGrantedAuthority(role)); 
        });
    return grantedAuthorities;
  }
}
