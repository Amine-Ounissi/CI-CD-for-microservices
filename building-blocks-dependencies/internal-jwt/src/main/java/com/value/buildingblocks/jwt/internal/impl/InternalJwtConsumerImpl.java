package com.value.buildingblocks.jwt.internal.impl;

import com.value.buildingblocks.jwt.core.JsonWebTokenConsumerType;
import com.value.buildingblocks.jwt.core.exception.JsonWebTokenException;
import com.value.buildingblocks.jwt.internal.InternalJwtConsumer;
import com.value.buildingblocks.jwt.internal.exception.InternalJwtException;
import com.value.buildingblocks.jwt.internal.token.InternalJwt;
import com.value.buildingblocks.jwt.internal.token.InternalJwtClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import java.text.ParseException;
import java.util.Map;

public class InternalJwtConsumerImpl implements InternalJwtConsumer {
  private final JsonWebTokenConsumerType<String, SignedJWT> tokenConsumerType;
  
  public InternalJwtConsumerImpl(JsonWebTokenConsumerType<String, SignedJWT> tokenConsumerType) {
    this.tokenConsumerType = tokenConsumerType;
  }
  
  public InternalJwt parseToken(String token) throws JsonWebTokenException, InternalJwtException {
    SignedJWT signedJwt = this.tokenConsumerType.parseToken(token);
    Map<String, Object> claims = getClaims(signedJwt);
    InternalJwtClaimsSet claimsSet = new InternalJwtClaimsSet(claims);
    return new InternalJwt(token, claimsSet);
  }
  
  private Map<String, Object> getClaims(SignedJWT signedJwt) throws InternalJwtException {
    try {
      return signedJwt.getJWTClaimsSet().getClaims();
    } catch (ParseException e) {
      throw new InternalJwtException("Can't parse token", e);
    } 
  }
}
