package com.value.buildingblocks.jwt.core.type;

import com.value.buildingblocks.jwt.core.exception.JsonWebTokenException;
import com.value.buildingblocks.jwt.core.token.JsonWebTokenClaimsSet;
import com.nimbusds.jose.EncryptionMethod;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWEAlgorithm;
import com.nimbusds.jose.JWEEncrypter;
import com.nimbusds.jose.JWEHeader;
import com.nimbusds.jose.JWEObject;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.KeyLengthException;
import com.nimbusds.jose.Payload;
import com.nimbusds.jose.crypto.DirectEncrypter;
import com.nimbusds.jwt.SignedJWT;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SignedEncryptedJsonWebTokenProducerType extends SignedJsonWebTokenProducerType {
  private static final Logger LOGGER = LoggerFactory.getLogger(SignedEncryptedJsonWebTokenProducerType.class);
  
  private final EncryptionMethod encryptionMethod;
  
  private final JWEAlgorithm encryptionAlgorithm;
  
  private final JWEEncrypter jweEncrypter;
  
  private final String encryptionKeyId;
  
  public SignedEncryptedJsonWebTokenProducerType(JWSAlgorithm signatureAlgorithm, JWSSigner jwsSigner, String signatureKeyId, JWEAlgorithm encryptionAlgorithm, EncryptionMethod encryptionMethod, String encryptionKeyId, byte[] encryptionKey) throws JsonWebTokenException {
    super(signatureAlgorithm, jwsSigner, signatureKeyId);
    this.encryptionAlgorithm = encryptionAlgorithm;
    this.encryptionMethod = encryptionMethod;
    this.encryptionKeyId = encryptionKeyId;
    try {
      this.jweEncrypter = (JWEEncrypter)new DirectEncrypter(encryptionKey);
    } catch (KeyLengthException e) {
      throw new JsonWebTokenException("Can't create token encrypter", e);
    } 
    LOGGER.debug("Setup JWE producer with KID: {}", this.encryptionKeyId);
  }
  
  public String createToken(JsonWebTokenClaimsSet claims) throws JsonWebTokenException {
    SignedJWT signedJwt = getSignedJwt(claims);
    JWEObject jweObject = new JWEObject((new JWEHeader.Builder(this.encryptionAlgorithm, this.encryptionMethod)).contentType("JWT").keyID(this.encryptionKeyId).build(), new Payload(signedJwt));
    try {
      jweObject.encrypt(this.jweEncrypter);
    } catch (JOSEException e) {
      throw new JsonWebTokenException("Can't encrypt the token", e);
    } 
    String token = jweObject.serialize();
    if (LOGGER.isTraceEnabled())
      LOGGER.trace("Create token: {}", token); 
    return token;
  }
}
