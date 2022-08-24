package com.value.buildingblocks.backend.security.auth.config;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.expression.SecurityExpressionRoot;
import org.springframework.security.access.expression.method.MethodSecurityExpressionOperations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

public class CustomSecurityExpressionRoot extends SecurityExpressionRoot implements
  MethodSecurityExpressionOperations {

  private static final Logger log = LoggerFactory.getLogger(CustomSecurityExpressionRoot.class);

  private FunctionalAccessControl functionalAccessControl;

  private Object filterObject;

  private Object returnObject;

  private Object target;

  public CustomSecurityExpressionRoot(Authentication authentication,
    FunctionalAccessControl functionalAccessControl) {
    super(authentication);
    this.functionalAccessControl = functionalAccessControl;
  }

  public boolean checkPermission(String resource, String function, String[] privileges) {
    String username, privilegesString = createCommaSeparatedStringOfPrivileges(privileges);
    log.debug("Checking permission for resource [{}], function [{}] and privileges [{}].",
      new Object[]{resource, function, privilegesString});
    if (this.authentication.getPrincipal() instanceof UserDetails) {
      UserDetails principal = (UserDetails) this.authentication.getPrincipal();
      username = principal.getUsername();
    } else if (this.authentication.getPrincipal() instanceof String) {
      log.debug("The authentication principal is a String. No user is authenticated.");
      username = (String) this.authentication.getPrincipal();
    } else {
      return false;
    }
    return this.functionalAccessControl
      .checkPermissions(username, resource, function, privilegesString);
  }

  private String createCommaSeparatedStringOfPrivileges(String[] privileges) {
    return StringUtils.join((Object[]) privileges, ",");
  }

  public Object getFilterObject() {
    return this.filterObject;
  }

  public void setFilterObject(Object filterObject) {
    this.filterObject = filterObject;
  }

  public Object getReturnObject() {
    return this.returnObject;
  }

  public void setReturnObject(Object returnObject) {
    this.returnObject = returnObject;
  }

  public Object getThis() {
    return this.target;
  }

  void setThis(Object target) {
    this.target = target;
  }
}
