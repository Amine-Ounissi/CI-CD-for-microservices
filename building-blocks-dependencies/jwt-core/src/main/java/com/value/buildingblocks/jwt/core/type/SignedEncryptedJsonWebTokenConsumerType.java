package com.value.buildingblocks.jwt.core.type;

import com.value.buildingblocks.jwt.core.exception.JsonWebTokenException;
import com.nimbusds.jose.EncryptionMethod;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWEAlgorithm;
import com.nimbusds.jose.JWEDecrypter;
import com.nimbusds.jose.JWEObject;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jwt.SignedJWT;
import java.text.ParseException;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

public class SignedEncryptedJsonWebTokenConsumerType extends SignedJsonWebTokenConsumerType {
  private static final Logger LOGGER = LoggerFactory.getLogger(SignedEncryptedJsonWebTokenConsumerType.class);
  
  private final Map<String, JWEDecrypter> jweKidAndDecrypter;
  
  private final JWEDecrypter defaultDecrypter;
  
  private final JWEAlgorithm encryptionAlgorithm;
  
  private final EncryptionMethod encryptionMethod;
  
  public SignedEncryptedJsonWebTokenConsumerType(JWSAlgorithm signatureAlgorithm, Map<String, JWSVerifier> jwsKidAndVerifier, JWEAlgorithm encryptionAlgorithm, EncryptionMethod encryptionMethod, Map<String, JWEDecrypter> jweKidAndDecrypter) {
    super(signatureAlgorithm, jwsKidAndVerifier);
    this.encryptionAlgorithm = encryptionAlgorithm;
    this.encryptionMethod = encryptionMethod;
    this.jweKidAndDecrypter = jweKidAndDecrypter;
    JWEDecrypter lastDecrypter = null;
    for (JWEDecrypter decrypter : jweKidAndDecrypter.values())
      lastDecrypter = decrypter; 
    this.defaultDecrypter = lastDecrypter;
    LOGGER.debug("Setup JWE consumer with KIDs: {}", jweKidAndDecrypter.keySet());
  }
  
  public SignedJWT parseToken(String token) throws JsonWebTokenException {
    if (LOGGER.isTraceEnabled())
      LOGGER.trace("Parse token: {}", token); 
    if (StringUtils.isEmpty(token))
      throw new JsonWebTokenException("Empty Json Web Token"); 
    return getSignedJwt(token);
  }
  
  protected SignedJWT getSignedJwt(String token) throws JsonWebTokenException {
    try {
      JWEObject jweObject = JWEObject.parse(token);
      JWEDecrypter decrypter = getJweDecrypter(jweObject);
      if (decrypter == null)
        throw new JsonWebTokenException(String.format("Json Web Token decrypter doesn't exist (for kid: %s)", new Object[] { jweObject
                .getHeader().getKeyID() })); 
      jweObject.decrypt(decrypter);
      SignedJWT signedJwt = jweObject.getPayload().toSignedJWT();
      validateSignature(signedJwt);
      validateSignatureAlgorithm(signedJwt);
      validateEncryptionAlgorithm(jweObject);
      validateEncryptionMethod(jweObject);
      return signedJwt;
    } catch (ParseException e) {
      throw new JsonWebTokenException("Can't parse the token", e);
    } catch (JOSEException e) {
      throw new JsonWebTokenException("Can't decrypt the token", e);
    } 
  }
  
  private JWEDecrypter getJweDecrypter(JWEObject jweObject) {
    String keyId = jweObject.getHeader().getKeyID();
    if (keyId == null || this.jweKidAndDecrypter.isEmpty()) {
      LOGGER.debug("Using default token decrypter");
      return this.defaultDecrypter;
    } 
    return this.jweKidAndDecrypter.get(keyId);
  }
  
  protected void validateEncryptionAlgorithm(JWEObject jweObject) throws JsonWebTokenException {
    JWEAlgorithm jweAlgorithm = jweObject.getHeader().getAlgorithm();
    if (!this.encryptionAlgorithm.equals(jweAlgorithm))
      throw new JsonWebTokenException(String.format("Received token encryption algorithm (%s) doesn't match configured encryption algorithm (%s)", new Object[] { jweAlgorithm
              .getName(), this.encryptionAlgorithm.getName() })); 
  }
  
  protected void validateEncryptionMethod(JWEObject jweObject) throws JsonWebTokenException {
    EncryptionMethod jweEncryptionMethod = jweObject.getHeader().getEncryptionMethod();
    if (!this.encryptionMethod.equals(jweEncryptionMethod))
      throw new JsonWebTokenException(String.format("Received token encryption method (%s) doesn't match configured encryption method (%s)", new Object[] { jweEncryptionMethod
              .getName(), this.encryptionMethod.getName() })); 
  }
}
