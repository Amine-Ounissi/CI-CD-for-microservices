package com.value.buildingblocks.backend.security.auth.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FunctionalAccessControlDefault implements FunctionalAccessControl {

  private static final Logger log = LoggerFactory.getLogger(FunctionalAccessControlDefault.class);

  public boolean checkPermissions(String username, String resource, String function,
    String privileges) {
    log.debug(
      "Checking user permission for user [{}], resource [{}], function [{}] and privileges [{}]",
      username, resource, function, privileges);
    return true;
  }
}
