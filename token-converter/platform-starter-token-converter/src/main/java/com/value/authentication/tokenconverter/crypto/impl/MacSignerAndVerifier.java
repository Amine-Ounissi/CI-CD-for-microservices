package com.value.authentication.tokenconverter.crypto.impl;

import com.value.authentication.tokenconverter.crypto.JsonWebAlgorithm;
import com.value.authentication.tokenconverter.crypto.SignerAndVerifier;
import com.value.authentication.tokenconverter.exception.JwsGenerationException;
import com.value.authentication.tokenconverter.exception.TokenConverterConfigurationException;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.jwt.crypto.sign.InvalidSignatureException;
import org.springframework.util.Assert;

public class MacSignerAndVerifier implements SignerAndVerifier {
  private static final Logger log = LoggerFactory.getLogger(MacSignerAndVerifier.class);
  
  private static final String REQUIRED_SECRET = "Required Secret Key length for %s is at least %d bits.";
  
  private final JsonWebAlgorithm alg;
  
  private final SecretKey secretKey;
  
  public MacSignerAndVerifier(JsonWebAlgorithm alg, SecretKey secretKey) {
    this.alg = alg;
    this.secretKey = secretKey;
    validateKey();
  }
  
  private void validateKey() {
    Assert.notNull(this.alg, "JWS algorithm must not be null.");
    Assert.notNull(this.secretKey, "HMAC secret Key must not be null.");
    switch (this.alg) {
      case HS256:
        if ((this.secretKey.getEncoded()).length < 32)
          throw new TokenConverterConfigurationException(String.format("Required Secret Key length for %s is at least %d bits.", new Object[] { this.alg.name(), Integer.valueOf(256) })); 
        return;
      case HS384:
        if ((this.secretKey.getEncoded()).length < 48)
          throw new TokenConverterConfigurationException(String.format("Required Secret Key length for %s is at least %d bits.", new Object[] { this.alg.name(), Integer.valueOf(384) })); 
        return;
      case HS512:
        if ((this.secretKey.getEncoded()).length < 64)
          throw new TokenConverterConfigurationException(String.format("Required Secret Key length for %s is at least %d bits.", new Object[] { this.alg.name(), Integer.valueOf(512) })); 
        return;
    } 
    throw new TokenConverterConfigurationException("Misconfigured token converter algorithm: " + this.alg
        .name());
  }
  
  public byte[] sign(byte[] bytes) {
    try {
      Mac mac = Mac.getInstance(this.secretKey.getAlgorithm());
      mac.init(this.secretKey);
      mac.update(bytes);
      return mac.doFinal();
    } catch (GeneralSecurityException e) {
      throw new JwsGenerationException(e);
    } 
  }
  
  public String algorithm() {
    return this.alg.getJcaAlg();
  }
  
  public void verify(byte[] content, byte[] signature) {
    byte[] expected = sign(content);
    if (!MessageDigest.isEqual(expected, signature)) {
      log.debug("Signature verification failed. Expected and provided signatures don't match.");
      throw new InvalidSignatureException("HMAC signature verification failed.");
    } 
  }
}
