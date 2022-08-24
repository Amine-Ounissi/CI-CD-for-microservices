package com.value.buildingblocks.jwt.external.impl;

import com.value.buildingblocks.jwt.core.JsonWebTokenConsumerType;
import com.value.buildingblocks.jwt.core.exception.JsonWebTokenException;
import com.value.buildingblocks.jwt.external.ExternalJwtConsumer;
import com.value.buildingblocks.jwt.external.exception.ExternalJwtException;
import com.value.buildingblocks.jwt.external.token.ExternalJwt;
import com.value.buildingblocks.jwt.external.token.ExternalJwtClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import java.text.ParseException;
import java.util.Map;

public class ExternalJwtConsumerImpl implements ExternalJwtConsumer {
  private final JsonWebTokenConsumerType<String, SignedJWT> tokenConsumerType;
  
  public ExternalJwtConsumerImpl(JsonWebTokenConsumerType<String, SignedJWT> tokenConsumerType) {
    this.tokenConsumerType = tokenConsumerType;
  }
  
  public ExternalJwt parseToken(String token) throws JsonWebTokenException, ExternalJwtException {
    SignedJWT signedJwt = (SignedJWT)this.tokenConsumerType.parseToken(token);
    Map<String, Object> claims = getClaims(signedJwt);
    ExternalJwtClaimsSet externalJwtClaimsSet = new ExternalJwtClaimsSet(claims);
    return new ExternalJwt(token, externalJwtClaimsSet);
  }
  
  private Map<String, Object> getClaims(SignedJWT signedJwt) throws ExternalJwtException {
    try {
      return signedJwt.getJWTClaimsSet().getClaims();
    } catch (ParseException e) {
      throw new ExternalJwtException("Can't parse token", e);
    } 
  }
}
