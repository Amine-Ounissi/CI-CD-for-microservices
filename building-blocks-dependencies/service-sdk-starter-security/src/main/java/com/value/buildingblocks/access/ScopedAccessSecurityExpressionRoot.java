package com.value.buildingblocks.access;

import com.value.buildingblocks.jwt.internal.authentication.InternalJwtAuthentication;
import com.value.buildingblocks.jwt.internal.token.InternalJwt;
import com.value.buildingblocks.jwt.internal.token.InternalJwtClaimsSet;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.expression.WebSecurityExpressionRoot;
import org.springframework.util.CollectionUtils;

public class ScopedAccessSecurityExpressionRoot extends WebSecurityExpressionRoot {
  private final InternalJwt originatingUserJwt;
  
  private final InternalJwt clientJwt;
  
  public ScopedAccessSecurityExpressionRoot(Authentication a, FilterInvocation fi, InternalJwt originatingUserJwt) {
    super(a, fi);
    this.originatingUserJwt = originatingUserJwt;
    if (a instanceof InternalJwtAuthentication) {
      InternalJwtAuthentication internalJwtAuthentication = (InternalJwtAuthentication)this.authentication;
      InternalJwtClaimsSet claims = (InternalJwtClaimsSet)internalJwtAuthentication.getDetails();
      this.clientJwt = new InternalJwt(internalJwtAuthentication.getCredentials(), claims);
    } else {
      this.clientJwt = null;
    } 
  }
  
  public boolean hasClientScopes(String... scopes) {
    return hasAllScopes(this.clientJwt, scopes);
  }
  
  public boolean hasAnyUserScopes(String... scopes) {
    if (this.originatingUserJwt == null)
      return false; 
    List<String> claimScopesList = getClaimScopes(this.originatingUserJwt);
    return CollectionUtils.containsAny(Arrays.asList(scopes), claimScopesList);
  }
  
  public boolean hasUserScopes(String... scopes) {
    return hasAllScopes(this.originatingUserJwt, scopes);
  }
  
  private List<String> getClaimScopes(InternalJwt jwt) {
    return (List<String>)jwt.getClaimsSet().getClaim("scope").orElse(Collections.emptyList());
  }
  
  private boolean hasAllScopes(InternalJwt jwt, String[] scopes) {
    if (jwt == null)
      return false; 
    List<String> claimScopesList = getClaimScopes(jwt);
    return claimScopesList.containsAll(Arrays.asList((Object[])scopes));
  }
}
