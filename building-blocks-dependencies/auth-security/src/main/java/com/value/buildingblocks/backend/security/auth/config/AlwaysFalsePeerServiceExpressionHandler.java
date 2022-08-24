package com.value.buildingblocks.backend.security.auth.config;

import org.springframework.security.core.Authentication;

public class AlwaysFalsePeerServiceExpressionHandler extends PeerServiceExpressionHandler {

  protected boolean isPeerService(Authentication auth) {
    return false;
  }

  public String toString() {
    return getClass().getSimpleName() + ": considers all requests to be not from a peer service";
  }
}
