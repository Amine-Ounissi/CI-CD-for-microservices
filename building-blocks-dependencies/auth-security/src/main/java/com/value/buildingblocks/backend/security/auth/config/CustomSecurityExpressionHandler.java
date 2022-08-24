package com.value.buildingblocks.backend.security.auth.config;

import org.aopalliance.intercept.MethodInvocation;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionOperations;
import org.springframework.security.core.Authentication;

public class CustomSecurityExpressionHandler extends
  DefaultMethodSecurityExpressionHandler implements MethodSecurityExpressionHandler {

  private final FunctionalAccessControl functionalAccessControl;

  public CustomSecurityExpressionHandler(FunctionalAccessControl functionalAccessControl) {
    this.functionalAccessControl = functionalAccessControl;
  }

  protected MethodSecurityExpressionOperations createSecurityExpressionRoot(
    Authentication authentication, MethodInvocation invocation) {
    CustomSecurityExpressionRoot root = new CustomSecurityExpressionRoot(authentication,
      this.functionalAccessControl);
    root.setThis(invocation.getThis());
    root.setPermissionEvaluator(getPermissionEvaluator());
    return root;
  }
}
