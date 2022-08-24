package com.value.buildingblocks.access;

import com.value.buildingblocks.common.OriginatingUserJwtHolder;
import com.value.buildingblocks.jwt.internal.token.InternalJwt;
import java.util.Optional;
import org.springframework.security.access.expression.SecurityExpressionOperations;
import org.springframework.security.authentication.AuthenticationTrustResolver;
import org.springframework.security.authentication.AuthenticationTrustResolverImpl;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.expression.DefaultWebSecurityExpressionHandler;

public class UserScopeExpressionHandler extends DefaultWebSecurityExpressionHandler {
  protected SecurityExpressionOperations createSecurityExpressionRoot(Authentication authentication, FilterInvocation fi) {
    Optional<InternalJwt> originatingUserJwt = OriginatingUserJwtHolder.getOriginatingUserJwt();
    ScopedAccessSecurityExpressionRoot root = new ScopedAccessSecurityExpressionRoot(authentication, fi, originatingUserJwt.orElse(null));
    root.setPermissionEvaluator(getPermissionEvaluator());
    root.setTrustResolver((AuthenticationTrustResolver)new AuthenticationTrustResolverImpl());
    root.setRoleHierarchy(getRoleHierarchy());
    return (SecurityExpressionOperations)root;
  }
}
