package com.value.buildingblocks.jwt.core.blacklist.impl;

import com.value.buildingblocks.jwt.core.authenticaton.JsonWebTokenAuthentication;
import com.value.buildingblocks.jwt.core.authenticaton.JsonWebTokenUserDetails;
import com.value.buildingblocks.jwt.core.blacklist.BlacklistStorage;
import com.value.buildingblocks.jwt.core.blacklist.TokenBlacklistService;
import com.value.buildingblocks.jwt.core.blacklist.dto.BlacklistRequest;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class DefaultTokenBlacklistService implements TokenBlacklistService {
  private static final Logger logger = LoggerFactory.getLogger(DefaultTokenBlacklistService.class);
  
  protected static final String TOKEN_ID_CLAIM = "jti";
  
  @Autowired
  private BlacklistStorage blacklistStorage;
  
  public void blacklist(BlacklistRequest blacklistRequest) {
    long ttl = calculateTokenLifeTime(blacklistRequest);
    if (ttl >= 0L) {
      this.blacklistStorage.add(blacklistRequest.withCreatedTime(ZonedDateTime.now()));
      if (logger.isInfoEnabled())
        logger.info("{} blacklisted: {}", blacklistRequest.getType().name(), blacklistRequest.getValue()); 
    } 
  }
  
  public boolean validate(JsonWebTokenAuthentication jwtAuthentication) {
    Optional<String> jti = jwtAuthentication.getDetails().getClaim("jti").map(Object::toString);
    Optional<String> username = Optional.<JsonWebTokenUserDetails>ofNullable(jwtAuthentication.getPrincipal()).map(JsonWebTokenUserDetails::getUsername);
    if (!jti.isPresent() || !username.isPresent()) {
      logger.error("The HTTP request should be authorized and contains all mandatory user session details");
      return false;
    } 
    return !isBlacklistedUserSession(jti.get(), username.get());
  }
  
  private long calculateTokenLifeTime(BlacklistRequest blacklistRequest) {
    long expirationUtcTime = blacklistRequest.getExpiryTime().toEpochSecond();
    return (expirationUtcTime != 0L) ? (expirationUtcTime - Instant.now().getEpochSecond()) : expirationUtcTime;
  }
  
  private boolean isBlacklistedUserSession(String jti, String username) {
    return (this.blacklistStorage.find(jti, BlacklistRequest.Type.JTI).isPresent() || this.blacklistStorage
      .find(username, BlacklistRequest.Type.USERNAME).isPresent());
  }
}
