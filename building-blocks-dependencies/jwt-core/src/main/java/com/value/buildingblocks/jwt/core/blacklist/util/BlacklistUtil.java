package com.value.buildingblocks.jwt.core.blacklist.util;

import com.value.buildingblocks.jwt.core.authenticaton.JsonWebTokenAuthentication;
import com.value.buildingblocks.jwt.core.blacklist.dto.BlacklistRequest;
import com.value.buildingblocks.jwt.core.exception.BlacklistException;
import com.value.buildingblocks.jwt.core.token.JsonWebTokenClaimsSet;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BlacklistUtil {
  private static final Logger logger = LoggerFactory.getLogger(BlacklistUtil.class);
  
  private static final String EXPIRATION_TIME = "exp";
  
  private static final String TOKEN_ID_CLAIM = "jti";
  
  public static BlacklistRequest prepareJtiBlackListRequest(JsonWebTokenAuthentication authentication, String reason) throws BlacklistException {
    JsonWebTokenClaimsSet claims = authentication.getDetails();
    String jwtId = (String)claims.getClaim("jti").map(Object::toString).orElseThrow(() -> new BlacklistException("Json Web Token id (JTI) couldn't be obtained from \"jti\" claim"));
    logger.debug("Established token (id={}) is going to be blacklisted", jwtId);
    ZonedDateTime expTime = (ZonedDateTime)claims.getClaim("exp").map(Date.class::cast).map(Date::toInstant).map(expirationTime -> ZonedDateTime.ofInstant(expirationTime, ZoneId.systemDefault())).orElseThrow(() -> new BlacklistException("Token expiration time couldn't be obtained from \"exp\" claim"));
    String issuer = authentication.getPrincipal().getUsername();
    return (new BlacklistRequest(jwtId, BlacklistRequest.Type.JTI, reason, issuer)).withExpiryTime(expTime);
  }
}
