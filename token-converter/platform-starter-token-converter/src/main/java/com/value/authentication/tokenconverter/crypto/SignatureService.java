package com.value.authentication.tokenconverter.crypto;

import com.value.authentication.tokenconverter.crypto.impl.MacSignerAndVerifier;
import com.value.authentication.tokenconverter.configuration.TokenConverterProperties;
import com.value.authentication.tokenconverter.crypto.impl.EcdsaSignerAndVerifier;
import com.value.authentication.tokenconverter.crypto.impl.RsaSignerAndVerifier;
import com.value.authentication.tokenconverter.exception.TokenConverterConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Optional;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

public class SignatureService {
  private static final String KEYS_MISSING = "Keystore does not contain both public and private keys.";
  
  private static final Logger log = LoggerFactory.getLogger(SignatureService.class);
  
  private final TokenConverterProperties tokenConverterProperties;
  
  private final SignerAndVerifier signerAndVerifier;
  
  public SignatureService(TokenConverterProperties tokenConverterProperties) {
    this.tokenConverterProperties = tokenConverterProperties;
    this.signerAndVerifier = loadSignerAndVerifier();
  }
  
  private SignerAndVerifier loadSignerAndVerifier() {
    KeyPair ecKeyPair, rsaKeyPair;
    JsonWebAlgorithm signatureAlgorithm = this.tokenConverterProperties.getJwt().getSignatureAlgorithm();
    if (signatureAlgorithm == null) {
      Assert.notNull(this.tokenConverterProperties.getJwt().getSignatureMode(), "Signature mode must not be null if signature algorithm is not set.");
      signatureAlgorithm = this.tokenConverterProperties.getJwt().getSignatureMode().equalsIgnoreCase("asymmetric-key-pair") ? JsonWebAlgorithm.RS256 : JsonWebAlgorithm.HS256;
    } 
    switch (signatureAlgorithm) {
      case HS256:
      case HS384:
      case HS512:
        return new MacSignerAndVerifier(signatureAlgorithm,
            loadSecretKeyFromConfiguration(signatureAlgorithm));
      case ES256:
      case ES384:
      case ES512:
        ecKeyPair = loadKeysFromKeyStore();
        validateEcKeys(ecKeyPair);
        return new EcdsaSignerAndVerifier(signatureAlgorithm, (ECPrivateKey)ecKeyPair
            .getPrivate(), (ECPublicKey)ecKeyPair.getPublic());
      case RS256:
      case RS384:
      case RS512:
        rsaKeyPair = loadKeysFromKeyStore();
        validateRsaKeys(rsaKeyPair);
        return new RsaSignerAndVerifier(signatureAlgorithm, (RSAPrivateKey)rsaKeyPair
            .getPrivate(), (RSAPublicKey)rsaKeyPair.getPublic());
    } 
    throw new TokenConverterConfigurationException("Algorithm not supported: " + signatureAlgorithm.name());
  }
  
  private void validateEcKeys(KeyPair ecKeyPair) {
    if (ecKeyPair == null || ecKeyPair.getPublic() == null || ecKeyPair.getPrivate() == null) {
      log.warn("Keystore does not contain both public and private keys.");
      throw new TokenConverterConfigurationException("Keystore does not contain both public and private keys.");
    } 
    if (!(ecKeyPair.getPrivate() instanceof ECPrivateKey)) {
      String message = "Private key must be a EC private key to use with ECDSA (ESxxx) algorithms. Found is " + ecKeyPair.getPrivate().getClass().getSimpleName();
      log.warn(message);
      throw new TokenConverterConfigurationException(message);
    } 
    if (!(ecKeyPair.getPublic() instanceof ECPublicKey)) {
      String message = "Public key must be a EC public key to use with ECDSA (ESxxx) algorithms. Found is " + ecKeyPair.getPublic().getClass().getSimpleName();
      log.warn(message);
      throw new TokenConverterConfigurationException(message);
    } 
  }
  
  public SignerAndVerifier getSignerAndVerifier() {
    return this.signerAndVerifier;
  }
  
  private void validateRsaKeys(KeyPair keyPair) {
    if (keyPair == null || keyPair.getPublic() == null || keyPair.getPrivate() == null) {
      log.warn("Keystore does not contain both public and private keys.");
      throw new TokenConverterConfigurationException("Keystore does not contain both public and private keys.");
    } 
    if (!(keyPair.getPrivate() instanceof RSAPrivateKey)) {
      String message = "Private key must be a RSA private key to use with RSA algorithms. Found is " + keyPair.getPrivate().getClass().getSimpleName();
      log.warn(message);
      throw new TokenConverterConfigurationException(message);
    } 
    if (!(keyPair.getPublic() instanceof RSAPublicKey)) {
      String message = "Public key must be a RSA public key to use with RSA algorithms. Found is " + keyPair.getPublic().getClass().getSimpleName();
      log.warn(message);
      throw new TokenConverterConfigurationException(message);
    } 
  }
  
  private KeyPair loadKeysFromKeyStore() {
    KeyStore keystore;
    TokenConverterProperties.Jwt.KeyPair keystoreProperties = this.tokenConverterProperties.getJwt().getKeyPair();
    Assert.notNull(keystoreProperties.getLocation(), "Location of the keystore for the token converter must not be null.");
    Assert.notNull(keystoreProperties.getPassword(), "Password of the keystore for the token converter must not be null.");
    Assert.notNull(keystoreProperties.getAlias(), "Alias of the private key for the token converter must not be null.");
    URI uri = URI.create(keystoreProperties.getLocation());
    String scheme = uri.getScheme();
    if (scheme.equals("classpath")) {
      keystore = loadKeyStoreFromClasspath(keystoreProperties.getLocation());
    } else if (scheme.equals("file")) {
      keystore = loadKeyStoreFromFilesystem(uri);
    } else {
      log.error("Failed to load configuration: Keystore location should use the file or classpath schemes. Used: {}", scheme);
      throw new TokenConverterConfigurationException("Failed to load configuration: Keystore location should use the file or classpath schemes.");
    } 
    try {
      if (!keystore.containsAlias(keystoreProperties.getAlias()))
        throw new TokenConverterConfigurationException("Private key with alias " + keystoreProperties
            .getAlias() + " couldn't be found."); 
      char[] privateKeyPassword = ((String)Optional.<String>ofNullable(keystoreProperties.getAliasPassword()).orElse(keystoreProperties.getPassword())).toCharArray();
      PublicKey publicKey = keystore.getCertificate(keystoreProperties.getAlias()).getPublicKey();
      PrivateKey privateKey = (PrivateKey)keystore.getKey(keystoreProperties.getAlias(), privateKeyPassword);
      return new KeyPair(publicKey, privateKey);
    } catch (GeneralSecurityException e) {
      throw new TokenConverterConfigurationException(e);
    } 
  }
  
  private KeyStore loadKeyStoreFromClasspath(String path) {
    InputStream is = null;
    try {
      String resourcePath = path.replace("classpath:", "");
      if (Thread.currentThread().getContextClassLoader() != null)
        is = Thread.currentThread().getContextClassLoader().getResourceAsStream(resourcePath); 
      if (is == null)
        is = SignatureService.class.getResourceAsStream(resourcePath); 
      if (is == null) {
        log.error("Unable to find keystore in classpath");
        throw new TokenConverterConfigurationException("Unable to find keystore in classpath");
      } 
      KeyStore keystore = KeyStore.getInstance("jks");
      keystore.load(is, this.tokenConverterProperties.getJwt().getKeyPair().getPassword().toCharArray());
      return keystore;
    } catch (GeneralSecurityException e) {
      throw new TokenConverterConfigurationException(e);
    } catch (IOException e) {
      log.warn("Unable to load location of the keystore: {}", this.tokenConverterProperties.getJwt().getKeyPair()
          .getLocation());
      throw new TokenConverterConfigurationException(e);
    } finally {
      if (is != null)
        try {
          is.close();
        } catch (IOException e) {
          log.debug("Exception thrown during loading of keystore.", e);
        }  
    } 
  }
  
  private KeyStore loadKeyStoreFromFilesystem(URI uri) {
    try (InputStream inputStream = Files.newInputStream(Paths.get(uri), new java.nio.file.OpenOption[0])) {
      KeyStore keystore = KeyStore.getInstance("jks");
      keystore.load(inputStream, this.tokenConverterProperties.getJwt().getKeyPair().getPassword().toCharArray());
      return keystore;
    } catch (GeneralSecurityException e) {
      throw new TokenConverterConfigurationException(e);
    } catch (IOException e) {
      log.warn("Unable to load location of the keystore: {}", this.tokenConverterProperties.getJwt().getKeyPair()
          .getLocation());
      throw new TokenConverterConfigurationException(e);
    } 
  }
  
  private SecretKey loadSecretKeyFromConfiguration(JsonWebAlgorithm signatureAlgorithm) {
    byte[] key;
    String signingKey = this.tokenConverterProperties.getJwt().getSigningKey();
    if (this.tokenConverterProperties.getJwt().isSigningKeyBase64Encoded()) {
      if (!Base64.isBase64(signingKey))
        throw new TokenConverterConfigurationException("Token Converter properties: `value.token-converter.jwt.signing-key` is not Base64 encoded.");
      key = Base64.decodeBase64(signingKey);
    } else {
      key = signingKey.getBytes(StandardCharsets.UTF_8);
    } 
    return new SecretKeySpec(key, signatureAlgorithm.getJcaAlg());
  }
}
