package com.value.buildingblocks.backend.security.auth.config;

import com.value.buildingblocks.jwt.internal.authentication.InternalJwtAuthentication;
import java.util.Collection;
import java.util.Date;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.core.Authentication;
import org.springframework.util.StringUtils;

public class DefaultPeerServiceExpressionHandler extends PeerServiceExpressionHandler {

  private static final Logger log = LoggerFactory
    .getLogger(DefaultPeerServiceExpressionHandler.class);

  private final String serviceApiScope;

  public DefaultPeerServiceExpressionHandler(String serviceApiScope) {
    this.serviceApiScope = serviceApiScope;
  }

  protected boolean isPeerService(Authentication auth) {
    boolean peerService = false;
    if (!StringUtils.isEmpty(this.serviceApiScope) && auth instanceof InternalJwtAuthentication) {
      Optional<Object> scope = ((InternalJwtAuthentication) auth).getDetails().getClaim("scope");
      peerService = scope.filter(Collection.class::isInstance)
        .map(Collection.class::cast)
        .map(scopes -> Boolean.valueOf(scopes.contains(this.serviceApiScope)))
        .orElse(Boolean.FALSE).booleanValue();
      if (peerService) {
        Optional<Date> expiry = ((InternalJwtAuthentication) auth).getDetails().getClaim("exp")
          .filter(Date.class::isInstance).map(Date.class::cast);
        if (expiry.isPresent()) {
          Date now = new Date();
          if (((Date) expiry.get()).before(now)) {
            throw new CredentialsExpiredException("token_expired");
          }
        } else {
          log.warn("No expiry date (\"exp\" claim) present in service API token");
          throw new BadCredentialsException("invalid_token");
        }
      }
    }
    return peerService;
  }

  public String toString() {
    return getClass().getSimpleName() + ": scope: \"" + this.serviceApiScope + "\"";
  }
}
