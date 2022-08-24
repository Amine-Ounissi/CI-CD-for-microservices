package com.value.buildingblocks.jwt.core.type;

import com.value.buildingblocks.jwt.core.JsonWebTokenConsumerType;
import com.value.buildingblocks.jwt.core.exception.JsonWebTokenException;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jwt.SignedJWT;
import java.text.ParseException;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

public class SignedJsonWebTokenConsumerType implements JsonWebTokenConsumerType<String, SignedJWT> {
  private static final Logger LOGGER = LoggerFactory.getLogger(SignedJsonWebTokenConsumerType.class);
  
  private final Map<String, JWSVerifier> jwsKidAndVerifier;
  
  private final JWSVerifier defaultVerifier;
  
  private final JWSAlgorithm signatureAlgorithm;
  
  public SignedJsonWebTokenConsumerType(JWSAlgorithm signatureAlgorithm, Map<String, JWSVerifier> jwsKidAndVerifier) {
    this.signatureAlgorithm = signatureAlgorithm;
    this.jwsKidAndVerifier = jwsKidAndVerifier;
    JWSVerifier lastVerifier = null;
    for (JWSVerifier verifier : jwsKidAndVerifier.values())
      lastVerifier = verifier; 
    this.defaultVerifier = lastVerifier;
    LOGGER.debug("Setup JWS consumer with KIDs: {}", jwsKidAndVerifier.keySet());
  }
  
  public SignedJWT parseToken(String token) throws JsonWebTokenException {
    if (LOGGER.isTraceEnabled())
      LOGGER.trace("Parse token: {}", token); 
    if (StringUtils.isEmpty(token))
      throw new JsonWebTokenException("Invalid Json Web Token"); 
    return getSignedJwt(token);
  }
  
  protected SignedJWT getSignedJwt(String token) throws JsonWebTokenException {
    SignedJWT signedJwt;
    try {
      signedJwt = SignedJWT.parse(token);
      validateSignature(signedJwt);
      validateSignatureAlgorithm(signedJwt);
    } catch (ParseException e) {
      throw new JsonWebTokenException("Can't parse the token", e);
    } 
    return signedJwt;
  }
  
  protected void validateSignature(SignedJWT signedJwt) throws JsonWebTokenException {
    JWSVerifier verifier = getJwsVerifier(signedJwt);
    try {
      if (verifier == null)
        throw new JsonWebTokenException(String.format("Json Web Token verifier doesn't exist (for kid: %s)",
          signedJwt
                  .getHeader().getKeyID()));
      if (!signedJwt.verify(verifier))
        throw new JsonWebTokenException("Invalid Json Web Token signature"); 
    } catch (JOSEException e) {
      throw new JsonWebTokenException("Invalid Json Web Token signature", e);
    } 
  }
  
  private JWSVerifier getJwsVerifier(SignedJWT signedJwt) {
    String keyId = signedJwt.getHeader().getKeyID();
    if (keyId == null || this.jwsKidAndVerifier.isEmpty()) {
      LOGGER.debug("Using default token signature verifier");
      return this.defaultVerifier;
    } 
    return this.jwsKidAndVerifier.get(keyId);
  }
  
  protected void validateSignatureAlgorithm(SignedJWT signedJwt) throws JsonWebTokenException {
    JWSAlgorithm jwsAlgorithm = signedJwt.getHeader().getAlgorithm();
    if (!this.signatureAlgorithm.equals(jwsAlgorithm))
      throw new JsonWebTokenException(String.format("Received token signature algorithm (%s) doesn't match configured signature algorithm (%s)", new Object[] { jwsAlgorithm
              .getName(), this.signatureAlgorithm.getName() })); 
  }
}
