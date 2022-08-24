package com.value.buildingblocks.jwt.core.authenticaton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityContextHolderHelper {
  private static final Logger logger = LoggerFactory.getLogger(SecurityContextHolderHelper.class);
  
  public static void setAuthentication(Authentication authentication) {
    if (authentication == null) {
      logger.debug("Failed attempt to set null Spring Security Context");
      return;
    }
    String username = authentication.getName();
    if (authentication.isAuthenticated()) {
      SecurityContextHolder.getContext().setAuthentication(authentication);
      logger.debug("User '{}' is authenticated and has just set a valid authentication under security context", username);
    } else {
      logger.debug("User '{}' is not authenticated", username);
    } 
  }
}
