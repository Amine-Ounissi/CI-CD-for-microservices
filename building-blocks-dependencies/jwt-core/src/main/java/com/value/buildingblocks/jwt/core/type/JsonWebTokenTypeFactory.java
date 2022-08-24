package com.value.buildingblocks.jwt.core.type;

import com.nimbusds.jose.EncryptionMethod;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWEDecrypter;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.KeyLengthException;
import com.nimbusds.jose.crypto.DirectDecrypter;
import com.nimbusds.jose.crypto.ECDSASigner;
import com.nimbusds.jose.crypto.ECDSAVerifier;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jose.crypto.impl.ECDSA;
import com.nimbusds.jose.crypto.impl.ECDSAProvider;
import com.nimbusds.jose.crypto.impl.MACProvider;
import com.nimbusds.jose.crypto.impl.RSAKeyUtils;
import com.nimbusds.jose.crypto.impl.RSASSAProvider;
import com.nimbusds.jose.jca.JCASupport;
import com.value.buildingblocks.jwt.core.JsonWebTokenConsumerType;
import com.value.buildingblocks.jwt.core.JsonWebTokenProducerType;
import com.value.buildingblocks.jwt.core.exception.JsonWebTokenException;
import com.value.buildingblocks.jwt.core.properties.Encryption;
import com.value.buildingblocks.jwt.core.properties.JsonWebTokenProperties;
import com.value.buildingblocks.jwt.core.properties.Signature;
import com.value.buildingblocks.jwt.core.properties.TokenKey;
import com.value.buildingblocks.jwt.core.properties.TokenKeyInput;
import java.security.InvalidParameterException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.LinkedHashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

public final class JsonWebTokenTypeFactory {

  private static final Logger logger = LoggerFactory.getLogger(JsonWebTokenTypeFactory.class);

  public static final String SIGNED_TOKEN_TYPE = "signed";

  public static final String SIGNED_AND_ENCRYPTED_TOKEN_TYPE = "signedEncrypted";

  public static JsonWebTokenProducerType getProducer(JsonWebTokenProperties properties)
    throws JsonWebTokenException {
    String type = properties.getType();
    if ("signedEncrypted".equalsIgnoreCase(type)) {
      Signature signature = properties.getSignature();
      Encryption encryption = properties.getEncryption();
      verifySignatureAlgorithm(signature.getAlgorithm());
      verifyEncryptionMethod(encryption.getMethod());
      String sigKeyId = signature.getActiveKey().getId();
      byte[] signKey = getKey(signature.getActiveKey());
      verifyKeySize(signature, signKey.length);
      JWSSigner jwsSigner = getJwsSigner(signature, signKey);
      String encKeyId = encryption.getActiveKey().getId();
      byte[] encKey = getKey(encryption.getActiveKey());
      verifyKeySize(encryption, encKey.length);
      return new SignedEncryptedJsonWebTokenProducerType(signature
        .getAlgorithm(), jwsSigner, sigKeyId, encryption

        .getAlgorithm(), encryption
        .getMethod(), encKeyId, encKey);
    }
    if (StringUtils.isEmpty(type) || "signed"
      .equalsIgnoreCase(type)) {
      Signature signature = properties.getSignature();
      verifySignatureAlgorithm(signature.getAlgorithm());
      String sigKeyId = signature.getActiveKey().getId();
      byte[] signKey = getKey(signature.getActiveKey());
      verifyKeySize(signature, signKey.length);
      JWSSigner jwsSigner = getJwsSigner(signature, signKey);
      return new SignedJsonWebTokenProducerType(signature
        .getAlgorithm(), jwsSigner, sigKeyId);
    }
    throw new JsonWebTokenException(
      String.format("Unsupported configuration for token type %s", new Object[]{properties
        .getType()}));
  }

  private static void verifyKeySize(Signature signature, int length) throws JsonWebTokenException {
    int requiredLength;
    JWSAlgorithm algorithm = signature.getAlgorithm();
    int keyLength = length;
    try {
      if (MACProvider.SUPPORTED_ALGORITHMS.contains(algorithm)) {
        requiredLength = getByteLength(MACSigner.getMinRequiredSecretLength(algorithm));
      } else if (RSASSAProvider.SUPPORTED_ALGORITHMS.contains(algorithm)) {
        requiredLength = getByteLength(2048);
        KeySpec spec = new PKCS8EncodedKeySpec(getKey(signature.getActiveKey()));
        KeyFactory rsaFact = KeyFactory.getInstance("RSA");
        RSAPrivateKey rsaKey = (RSAPrivateKey) rsaFact.generatePrivate(spec);
        keyLength = getByteLength(RSAKeyUtils.keyBitLength(rsaKey));
      } else if (ECDSAProvider.SUPPORTED_ALGORITHMS.contains(algorithm)) {
        requiredLength = ECDSA.getSignatureByteArrayLength(algorithm) / 2;
        KeySpec spec = new PKCS8EncodedKeySpec(getKey(signature.getActiveKey()));
        KeyFactory keyFactory = KeyFactory.getInstance("EC");
        ECPrivateKey ecKey = (ECPrivateKey) keyFactory.generatePrivate(spec);
        keyLength = getByteLength(ecKey.getParams().getCurve().getField().getFieldSize());
      } else {
        throw new JOSEException("Unsupported JWS algorithm: " + algorithm);
      }
    } catch (JOSEException | NoSuchAlgorithmException | InvalidKeySpecException x) {
      throw new JsonWebTokenException(x);
    }
    if (requiredLength > keyLength) {
      throw new InvalidParameterException(String.format(
        "Insufficient key length for signature algorithm %s, expecting a key of at least %s bytes, current key size is %s bytes",
        new Object[]{algorithm

          .getName(), Integer.valueOf(requiredLength), Integer.valueOf(keyLength)}));
    }
    if (requiredLength < keyLength && !RSASSAProvider.SUPPORTED_ALGORITHMS.contains(algorithm)) {
      logger.warn(
        "Key length exceeding length needed for signature algorithm {}, expecting a key of {} bytes, current key size is {} bytes",
        new Object[]{algorithm
          .getName(), Integer.valueOf(requiredLength), Integer.valueOf(keyLength)});
    }
  }

  private static void verifyKeySize(Encryption encryption, int length) {
    EncryptionMethod encryptionMethod = encryption.getMethod();
    int requiredLength = getByteLength(encryptionMethod.cekBitLength());
    if (requiredLength > length) {
      throw new InvalidParameterException(String.format(
        "Insufficient key length for encryption method %s, expecting a key of %s bytes, current key size is %s bytes",
        new Object[]{encryptionMethod

          .getName(), Integer.valueOf(requiredLength), Integer.valueOf(length)}));
    }
    if (requiredLength < length) {
      logger.warn(
        "Key length exceeding length needed for encryption method {}, expecting a key of {} bytes, current key size is {} bytes",
        new Object[]{encryptionMethod
          .getName(), Integer.valueOf(requiredLength), Integer.valueOf(length)});
    }
  }

  public static JsonWebTokenConsumerType getConsumer(JsonWebTokenProperties properties)
    throws JsonWebTokenException {
    String type = properties.getType();
    if ("signedEncrypted".equalsIgnoreCase(type)) {
      Signature signature = properties.getSignature();
      Encryption encryption = properties.getEncryption();
      verifySignatureAlgorithm(signature.getAlgorithm());
      verifyEncryptionMethod(encryption.getMethod());
      Map<String, JWSVerifier> jwsKidAndVerifiers = getJwsVerifiers(signature);
      Map<String, JWEDecrypter> jweKidAndDecrypters = getJweDecrypters(encryption);
      return new SignedEncryptedJsonWebTokenConsumerType(signature
        .getAlgorithm(), jwsKidAndVerifiers, encryption

        .getAlgorithm(), encryption
        .getMethod(), jweKidAndDecrypters);
    }
    if (StringUtils.isEmpty(type) || "signed".equalsIgnoreCase(type)) {
      Signature signature = properties.getSignature();
      verifySignatureAlgorithm(signature.getAlgorithm());
      Map<String, JWSVerifier> jwsKidAndVerifiers = getJwsVerifiers(signature);
      return new SignedJsonWebTokenConsumerType(signature.getAlgorithm(), jwsKidAndVerifiers);
    }
    throw new JsonWebTokenException(String.format("Unsupported configuration for token type %s",
      properties.getType()));
  }

  public static JWSVerifier getJwsVerifier(JWSAlgorithm jwsAlgorithm, byte[] verificationKey)
    throws JsonWebTokenException {
    try {
      return getVerifierForAlgorithm(jwsAlgorithm, verificationKey);
    } catch (JOSEException | NoSuchAlgorithmException e) {
      throw new JsonWebTokenException("Can't create token verifier", e);
    } catch (InvalidKeySpecException e) {
      throw new JsonWebTokenException(
        "Can't create token verifier, verify that public key is configured", e);
    }
  }

  public static JWEDecrypter getJweDecrypter(byte[] encryptionKey) throws JsonWebTokenException {
    try {
      return (JWEDecrypter) new DirectDecrypter(encryptionKey);
    } catch (KeyLengthException e) {
      throw new JsonWebTokenException("Can't create token decrypter", e);
    }
  }

  public static JWSVerifier getVerifierForAlgorithm(JWSAlgorithm jwsAlgorithm,
    byte[] verificationKey)
    throws JOSEException, NoSuchAlgorithmException, InvalidKeySpecException, JsonWebTokenException {
    if (JWSAlgorithm.Family.HMAC_SHA.contains(jwsAlgorithm)) {
      return (JWSVerifier) new MACVerifier(verificationKey);
    }
    if (JWSAlgorithm.Family.RSA.contains(jwsAlgorithm)) {
      X509EncodedKeySpec spec = new X509EncodedKeySpec(verificationKey);
      KeyFactory rsaFact = KeyFactory.getInstance("RSA");
      RSAPublicKey key = (RSAPublicKey) rsaFact.generatePublic(spec);
      return (JWSVerifier) new RSASSAVerifier(key);
    }
    if (JWSAlgorithm.Family.EC.contains(jwsAlgorithm)) {
      X509EncodedKeySpec keySpec = new X509EncodedKeySpec(verificationKey);
      KeyFactory ecFact = KeyFactory.getInstance("EC");
      ECPublicKey key = (ECPublicKey) ecFact.generatePublic(keySpec);
      return (JWSVerifier) new ECDSAVerifier(key);
    }
    throw new JsonWebTokenException(String
      .format("Unsupported '%s' JWS verification algorithm", new Object[]{jwsAlgorithm.getName()}));
  }

  public static JWSSigner getSignerForAlgorithm(JWSAlgorithm jwsAlgorithm, byte[] signingKey)
    throws JOSEException, NoSuchAlgorithmException, InvalidKeySpecException, JsonWebTokenException {
    if (JWSAlgorithm.Family.HMAC_SHA.contains(jwsAlgorithm)) {
      return (JWSSigner) new MACSigner(signingKey);
    }
    if (JWSAlgorithm.Family.RSA.contains(jwsAlgorithm)) {
      KeySpec spec = new PKCS8EncodedKeySpec(signingKey);
      KeyFactory rsaFact = KeyFactory.getInstance("RSA");
      RSAPrivateKey signKey = (RSAPrivateKey) rsaFact.generatePrivate(spec);
      return (JWSSigner) new RSASSASigner(signKey);
    }
    if (JWSAlgorithm.Family.EC.contains(jwsAlgorithm)) {
      KeySpec spec = new PKCS8EncodedKeySpec(signingKey);
      KeyFactory keyFactory = KeyFactory.getInstance("EC");
      ECPrivateKey signKey = (ECPrivateKey) keyFactory.generatePrivate(spec);
      return (JWSSigner) new ECDSASigner(signKey);
    }
    throw new JsonWebTokenException(String
      .format("Unsupported '%s' JWS signature algorithm", new Object[]{jwsAlgorithm.getName()}));
  }

  private static byte[] getKey(TokenKey tokenKey) throws JsonWebTokenException {
    if (StringUtils.isEmpty(tokenKey.getValue())) {
      throw new JsonWebTokenException(String
        .format("Token key value property for the '%s' type is not defined.", new Object[]{tokenKey
          .getType()}));
    }
    TokenKeyInput tokenKeyInput = tokenKey.getInput();
    byte[] keyBytes = TokenKeyReader.readKey(tokenKey);
    return tokenKeyInput.convert(keyBytes);
  }

  private static JWSSigner getJwsSigner(Signature signature, byte[] signKey)
    throws JsonWebTokenException {
    try {
      return getSignerForAlgorithm(signature.getAlgorithm(), signKey);
    } catch (JOSEException | NoSuchAlgorithmException | InvalidKeySpecException e) {
      throw new JsonWebTokenException("Can't create signer", e);
    }
  }

  private static void verifySignatureAlgorithm(JWSAlgorithm algorithm) {
    if (!JCASupport.isSupported(algorithm)) {
      throw new InvalidParameterException(String
        .format("JWS Algorithm '%s' is not supported by currently available JCA",
          new Object[]{algorithm
            .getName()}));
    }
  }

  private static void verifyEncryptionMethod(EncryptionMethod encryptionMethod) {
    if (!JCASupport.isSupported(encryptionMethod)) {
      throw new InvalidParameterException(String
        .format("JWE encryptionMethod '%s' is not supported by currently available JCA",
          new Object[]{encryptionMethod
            .getName()}));
    }
  }

  private static Map<String, JWSVerifier> getJwsVerifiers(Signature signature)
    throws JsonWebTokenException {
    if (signature.getAllKeys().isEmpty()) {
      throw new JsonWebTokenException("Missing configuration for the token signature key(s).");
    }
    Map<String, JWSVerifier> jwsKidAndVerifier = new LinkedHashMap<>();
    for (TokenKey signatureKey : signature.getAllKeys()) {
      byte[] key = getKey(signatureKey);
      JWSVerifier verifier = getJwsVerifier(signature.getAlgorithm(), key);
      jwsKidAndVerifier.put(signatureKey.getId(), verifier);
      logger.debug("Create token signature verifier for KID: {}", signatureKey.getId());
    }
    return jwsKidAndVerifier;
  }

  private static Map<String, JWEDecrypter> getJweDecrypters(Encryption encryption)
    throws JsonWebTokenException {
    if (encryption.getAllKeys().isEmpty()) {
      throw new JsonWebTokenException("Missing configuration for the token encryption key(s).");
    }
    Map<String, JWEDecrypter> jweKidAndDecrypter = new LinkedHashMap<>();
    for (TokenKey encryptionKey : encryption.getAllKeys()) {
      byte[] key = getKey(encryptionKey);
      JWEDecrypter decrypter = getJweDecrypter(key);
      jweKidAndDecrypter.put(encryptionKey.getId(), decrypter);
      logger.debug("Create token decrypter for KID: {}", encryptionKey.getId());
    }
    return jweKidAndDecrypter;
  }

  private static int getByteLength(int bitLength) {
    return (int) Math.ceil(bitLength / 8.0D);
  }
}
