package com.value.authentication.tokenconverter.crypto.impl;

import com.value.authentication.tokenconverter.crypto.JsonWebAlgorithm;
import com.value.authentication.tokenconverter.crypto.SignerAndVerifier;
import com.value.authentication.tokenconverter.exception.JwsGenerationException;
import com.value.authentication.tokenconverter.exception.TokenConverterConfigurationException;
import java.security.GeneralSecurityException;
import java.security.Signature;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.jwt.crypto.sign.InvalidSignatureException;
import org.springframework.util.Assert;

public class EcdsaSignerAndVerifier implements SignerAndVerifier {
  private static final Logger log = LoggerFactory.getLogger(EcdsaSignerAndVerifier.class);
  
  private static final String INVALID_SIGNATURE_FORMAT = "Invalid ECDSA signature format";
  
  private static final String INVALID_PRIVATE_KEY = "Invalid Private Key. Curve used is incorrect. %s requires a NIST CURVE %s ASN1 OID: %s. Private key size found is %d bits long.";
  
  private final JsonWebAlgorithm alg;
  
  private final ECPrivateKey privateKey;
  
  private final ECPublicKey publicKey;
  
  public EcdsaSignerAndVerifier(JsonWebAlgorithm alg, ECPrivateKey privateKey, ECPublicKey publicKey) {
    this.alg = alg;
    this.privateKey = privateKey;
    this.publicKey = publicKey;
    validateKeys();
  }
  
  private void validateKeys() {
    Assert.notNull(this.alg, "JSON web algorithm must not be null.");
    Assert.notNull(this.privateKey, "EC private key must not be null.");
    Assert.notNull(this.publicKey, "EC public key must not be null.");
    int size = getCurveSizeFromPrivateKey(this.privateKey);
    if (this.alg == JsonWebAlgorithm.ES256 && size != 256)
      throw new TokenConverterConfigurationException(
          String.format("Invalid Private Key. Curve used is incorrect. %s requires a NIST CURVE %s ASN1 OID: %s. Private key size found is %d bits long.", new Object[] { this.alg.name(), "P-256", "prime256v1", Integer.valueOf(size) })); 
    if (this.alg == JsonWebAlgorithm.ES384 && size != 384)
      throw new TokenConverterConfigurationException(
          String.format("Invalid Private Key. Curve used is incorrect. %s requires a NIST CURVE %s ASN1 OID: %s. Private key size found is %d bits long.", new Object[] { this.alg.name(), "P-384", "secp384r1", Integer.valueOf(size) })); 
    if (this.alg == JsonWebAlgorithm.ES512 && size != 521)
      throw new TokenConverterConfigurationException(
          String.format("Invalid Private Key. Curve used is incorrect. %s requires a NIST CURVE %s ASN1 OID: %s. Private key size found is %d bits long.", new Object[] { this.alg.name(), "P-521", "secp521r1", Integer.valueOf(size) })); 
  }
  
  private int getCurveSizeFromPrivateKey(ECPrivateKey privateKey) {
    return privateKey.getParams().getOrder().bitLength();
  }
  
  public byte[] sign(byte[] bytes) {
    try {
      Signature instance = Signature.getInstance(this.alg.getJcaAlg());
      instance.initSign(this.privateKey);
      instance.update(bytes);
      byte[] signature = instance.sign();
      return convertSignatureToJws(signature, getSignatureSize());
    } catch (GeneralSecurityException e) {
      throw new JwsGenerationException(e);
    } 
  }
  
  public void verify(byte[] content, byte[] signature) {
    try {
      Signature instance = Signature.getInstance(this.alg.getJcaAlg());
      instance.initVerify(this.publicKey);
      instance.update(content);
      if (!instance.verify(convertSignatureToJca(signature))) {
        log.debug("Signature verification failed. Expected and provided signatures don't match.");
        throw new InvalidSignatureException("EC Signature did not match content");
      } 
    } catch (GeneralSecurityException e) {
      throw new JwsGenerationException(e);
    } 
  }
  
  public String algorithm() {
    return this.alg.getJcaAlg();
  }
  
  private int getSignatureSize() {
    switch (this.alg) {
      case ES256:
        return 64;
      case ES384:
        return 96;
      case ES512:
        return 132;
    } 
    throw new IllegalStateException("Invalid combination of algorithm and provider. ECDSA + " + this.alg
        .getJcaAlg());
  }
  
  private byte[] convertSignatureToJws(byte[] der, int outputLength) throws GeneralSecurityException {
    int offset;
    if (der.length < 8 || der[0] != 48)
      throw new GeneralSecurityException("Invalid ECDSA signature format"); 
    if (der[1] > 0) {
      offset = 2;
    } else if (der[1] == -127) {
      offset = 3;
    } else {
      throw new GeneralSecurityException("Invalid ECDSA signature format");
    } 
    byte lengthOfR = der[offset + 1];
    byte lengthOfS = der[offset + 2 + lengthOfR + 1];
    int i = lengthOfR;
    while (i > 0 && der[offset + 2 + lengthOfR - i] == 0)
      i--; 
    int j = lengthOfS;
    while (j > 0 && der[offset + 2 + lengthOfR + 2 + lengthOfS - j] == 0)
      j--; 
    int rawLen = Math.max(Math.max(i, j), outputLength / 2);
    if ((der[offset - 1] & 0xFF) != der.length - offset || (der[offset - 1] & 0xFF) != 2 + lengthOfR + 2 + lengthOfS || der[offset] != 2 || der[offset + 2 + lengthOfR] != 2)
      throw new GeneralSecurityException("Invalid ECDSA signature format"); 
    byte[] concatSignature = new byte[2 * rawLen];
    System.arraycopy(der, offset + 2 + lengthOfR - i, concatSignature, rawLen - i, i);
    System.arraycopy(der, offset + 2 + lengthOfR + 2 + lengthOfS - j, concatSignature, 2 * rawLen - j, j);
    return concatSignature;
  }
  
  private byte[] convertSignatureToJca(byte[] jwsSignature) {
    int offset;
    byte[] jcaSignature;
    int rawLen = jwsSignature.length / 2;
    int i = rawLen;
    while (i > 0 && jwsSignature[rawLen - i] == 0)
      i--; 
    int j = i;
    if (jwsSignature[rawLen - i] < 0)
      j++; 
    int k = rawLen;
    while (k > 0 && jwsSignature[2 * rawLen - k] == 0)
      k--; 
    int l = k;
    if (jwsSignature[2 * rawLen - k] < 0)
      l++; 
    int len = 2 + j + 2 + l;
    if (len > 255)
      throw new InvalidSignatureException("Invalid ECDSA signature format"); 
    if (len < 128) {
      jcaSignature = new byte[4 + j + 2 + l];
      offset = 1;
    } else {
      jcaSignature = new byte[5 + j + 2 + l];
      jcaSignature[1] = -127;
      offset = 2;
    } 
    jcaSignature[0] = 48;
    jcaSignature[offset++] = (byte)len;
    jcaSignature[offset++] = 2;
    jcaSignature[offset++] = (byte)j;
    System.arraycopy(jwsSignature, rawLen - i, jcaSignature, offset + j - i, i);
    offset += j;
    jcaSignature[offset++] = 2;
    jcaSignature[offset++] = (byte)l;
    System.arraycopy(jwsSignature, 2 * rawLen - k, jcaSignature, offset + l - k, k);
    return jcaSignature;
  }
}
