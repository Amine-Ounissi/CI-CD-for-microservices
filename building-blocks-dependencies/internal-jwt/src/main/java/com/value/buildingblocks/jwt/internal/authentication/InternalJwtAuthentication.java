package com.value.buildingblocks.jwt.internal.authentication;

import com.value.buildingblocks.jwt.core.authenticaton.JsonWebTokenAuthentication;
import com.value.buildingblocks.jwt.core.authenticaton.JsonWebTokenUserDetails;
import com.value.buildingblocks.jwt.internal.token.InternalJwt;
import com.value.buildingblocks.jwt.internal.token.InternalJwtClaimsSet;
import java.util.ArrayList;
import java.util.List;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

public class InternalJwtAuthentication extends JsonWebTokenAuthentication {

  public static final String DEFAULT_USER_NAME = "vds";

  public InternalJwtAuthentication(InternalJwt token) {
    super(getUserDetails(token.getClaimsSet()), token.getClaimsSet(), token.getSerializedToken());
    setAuthenticated(true);
  }

  private static JsonWebTokenUserDetails getUserDetails(InternalJwtClaimsSet claimsSet) {
    return new JsonWebTokenUserDetails(claimsSet
      .getSubject().orElse(DEFAULT_USER_NAME),
      getUserGrantedAuthorities(claimsSet), claimsSet.isAccountNonExpired().orElse(Boolean.FALSE),
      claimsSet.isAccountNonLocked().orElse(Boolean.FALSE),
      claimsSet.isCredentialsNonExpired().orElse(Boolean.FALSE),
      claimsSet.isAccountEnabled().orElse(Boolean.TRUE));
  }

  private static List<GrantedAuthority> getUserGrantedAuthorities(InternalJwtClaimsSet claimsSet) {
    List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
    claimsSet.getRoles().ifPresent(roles -> {
      for (String role : roles) {
        grantedAuthorities.add(new SimpleGrantedAuthority(role));
      }
    });
    claimsSet.getAuthorities().ifPresent(authorities -> {
      for (String authority : authorities) {
        grantedAuthorities.add(new SimpleGrantedAuthority(authority));
      }
    });
    return grantedAuthorities;
  }
}
