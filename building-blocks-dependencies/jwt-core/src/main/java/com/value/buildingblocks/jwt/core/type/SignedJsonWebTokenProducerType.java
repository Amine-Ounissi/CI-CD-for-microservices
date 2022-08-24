package com.value.buildingblocks.jwt.core.type;

import com.value.buildingblocks.jwt.core.JsonWebTokenProducerType;
import com.value.buildingblocks.jwt.core.exception.JsonWebTokenException;
import com.value.buildingblocks.jwt.core.token.JsonWebTokenClaimsSet;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

public class SignedJsonWebTokenProducerType implements JsonWebTokenProducerType<JsonWebTokenClaimsSet, String> {
  private static final Logger logger = LoggerFactory.getLogger(SignedJsonWebTokenProducerType.class);
  
  private final JWSAlgorithm signatureAlgorithm;
  
  private final JWSSigner signer;
  
  private final String signatureKeyId;
  
  public SignedJsonWebTokenProducerType(JWSAlgorithm signatureAlgorithm, JWSSigner jwsSigner, String signatureKeyId) {
    this.signatureAlgorithm = signatureAlgorithm;
    this.signer = jwsSigner;
    this.signatureKeyId = signatureKeyId;
    logger.debug("Setup JWS producer with KID: {}", this.signatureKeyId);
  }
  
  public String createToken(JsonWebTokenClaimsSet claims) throws JsonWebTokenException {
    SignedJWT signedJwt = getSignedJwt(claims);
    String token = signedJwt.serialize();
    if (logger.isTraceEnabled())
      logger.trace("Create token: {}", token); 
    return token;
  }
  
  protected SignedJWT getSignedJwt(JsonWebTokenClaimsSet tokenClaimsSet) throws JsonWebTokenException {
    try {
      JWSHeader.Builder headerBuilder = new JWSHeader.Builder(this.signatureAlgorithm);
      if (StringUtils.hasText(this.signatureKeyId))
        headerBuilder.keyID(this.signatureKeyId); 
      JWSHeader header = headerBuilder.build();
      JWTClaimsSet.Builder claimBuilder = new JWTClaimsSet.Builder();
      tokenClaimsSet.getClaims().entrySet()
        .forEach(entry -> claimBuilder.claim((String)entry.getKey(), entry.getValue()));
      JWTClaimsSet claimsSet = claimBuilder.build();
      SignedJWT signedJwt = new SignedJWT(header, claimsSet);
      signedJwt.sign(this.signer);
      return signedJwt;
    } catch (JOSEException e) {
      throw new JsonWebTokenException("Can't sign the token", e);
    } 
  }
}
