package com.value.authentication.tokenconverter.crypto.impl;

import com.value.authentication.tokenconverter.crypto.JsonWebAlgorithm;
import com.value.authentication.tokenconverter.crypto.SignerAndVerifier;
import com.value.authentication.tokenconverter.exception.JwsGenerationException;
import com.value.authentication.tokenconverter.exception.TokenConverterConfigurationException;
import java.security.GeneralSecurityException;
import java.security.Signature;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.jwt.crypto.sign.InvalidSignatureException;
import org.springframework.util.Assert;

public class RsaSignerAndVerifier implements SignerAndVerifier {
  private static final Logger log = LoggerFactory.getLogger(RsaSignerAndVerifier.class);
  
  private final JsonWebAlgorithm alg;
  
  private final RSAPrivateKey privateKey;
  
  private final RSAPublicKey publicKey;
  
  public RsaSignerAndVerifier(JsonWebAlgorithm alg, RSAPrivateKey privateKey, RSAPublicKey publicKey) {
    this.alg = alg;
    this.privateKey = privateKey;
    this.publicKey = publicKey;
    validateKeys();
  }
  
  private void validateKeys() {
    Assert.notNull(this.alg, "JSON web algorithm must not be null.");
    Assert.notNull(this.privateKey, "RSA private key must not be null.");
    Assert.notNull(this.publicKey, "RSA public key must not be null.");
    if (this.privateKey.getModulus().bitLength() < 2048)
      throw new TokenConverterConfigurationException("Required RSA Private Key length for " + this.alg
          .name() + " is at least 2048 bits."); 
  }
  
  public byte[] sign(byte[] bytes) {
    try {
      Signature instance = Signature.getInstance(algorithm());
      instance.initSign(this.privateKey);
      instance.update(bytes);
      return instance.sign();
    } catch (GeneralSecurityException e) {
      throw new JwsGenerationException(e);
    } 
  }
  
  public void verify(byte[] content, byte[] signature) {
    try {
      Signature instance = Signature.getInstance(algorithm());
      instance.initVerify(this.publicKey);
      instance.update(content);
      if (!instance.verify(signature)) {
        log.debug("Signature verification failed. Expected and provided signatures don't match.");
        throw new InvalidSignatureException("RSA signature verification failed.");
      } 
    } catch (GeneralSecurityException e) {
      throw new JwsGenerationException(e);
    } 
  }
  
  public String algorithm() {
    return this.alg.getJcaAlg();
  }
}
