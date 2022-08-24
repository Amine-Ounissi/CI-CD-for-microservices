package com.value.buildingblocks.jwt.core.type;

import com.value.buildingblocks.jwt.core.exception.TokenKeyException;
import com.value.buildingblocks.jwt.core.properties.TokenKey;
import com.value.buildingblocks.jwt.core.properties.TokenKeyType;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.jwk.AsymmetricJWK;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.KeyType;
import com.nimbusds.jose.jwk.OctetSequenceKey;
import com.nimbusds.jose.jwk.PasswordLookup;
import java.io.File;
import java.io.FileInputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.Provider;
import org.springframework.util.StringUtils;

public final class TokenKeyReader {
  private static final String PROBLEM_READING_KEY_ERROR = "Problem reading key from '%s'";
  
  public static byte[] readKey(TokenKey tokenKey) {
    switch (tokenKey.getType()) {
      case FILE:
        return readKeyFromFile(tokenKey);
      case ENV:
        return readKeyFromEnv(tokenKey);
      case VALUE:
        return readKeyFromProperty(tokenKey);
      case JKS:
        return readKeyFromJks(tokenKey);
      case CONF:
        return readKeyFromConfigurationFile(tokenKey);
    } 
    throw new TokenKeyException(String.format("Unsupported token type '%s'", new Object[] { tokenKey.getType() }));
  }
  
  private static byte[] readKeyFromFile(TokenKey tokenKey) {
    try {
      return Files.readAllBytes(Paths.get(new URI(tokenKey.getValue())));
    } catch (URISyntaxException|java.io.IOException e) {
      throw new TokenKeyException(String.format("Problem reading key from '%s'", new Object[] { tokenKey.getValue() }), e);
    } 
  }
  
  private static byte[] readKeyFromEnv(TokenKey tokenKey) {
    String value = tokenKey.getValue();
    String envValue = System.getenv(value);
    if (envValue == null)
      envValue = System.getProperty(value); 
    if (StringUtils.isEmpty(envValue))
      throw new TokenKeyException("Undefined environment variable name: " + value); 
    return envValue.getBytes();
  }
  
  private static byte[] readKeyFromProperty(TokenKey tokenKey) {
    String value = tokenKey.getValue();
    if (StringUtils.isEmpty(value))
      throw new TokenKeyException("Undefined property token key value: " + value); 
    return value.getBytes();
  }
  
  private static byte[] readKeyFromJks(TokenKey tokenKey) {
    try (FileInputStream inputStream = new FileInputStream(new File(new URI(tokenKey.getValue())))) {
      KeyStore keyStore = KeyStore.getInstance("JCEKS");
      keyStore.load(inputStream, tokenKey.getPassword().toCharArray());
      return readKeyFromKeystore(tokenKey, keyStore);
    } catch (GeneralSecurityException|URISyntaxException|java.io.IOException e) {
      throw new TokenKeyException(String.format("Problem reading key from '%s'", new Object[] { tokenKey.getValue() }), e);
    } 
  }
  
  private static byte[] readKeyFromConfigurationFile(TokenKey tokenKey) {
    String configFile = tokenKey.getValue();
    try {
      Provider hsmProvider = SecurityProviderFactory.getProviderPkcs11(configFile);
      KeyStore keyStore = KeyStore.getInstance("PKCS11", hsmProvider);
      keyStore.load(null, tokenKey.getPassword().toCharArray());
      return readKeyFromKeystore(tokenKey, keyStore);
    } catch (GeneralSecurityException|java.io.IOException e) {
      throw new TokenKeyException(String.format("Problem reading key from '%s'", new Object[] { tokenKey.getValue() }), e);
    } 
  }
  
  private static byte[] readKeyFromKeystore(TokenKey tokenKey, KeyStore keyStore) {
    try {
      String keyAlias = tokenKey.getAlias();
      PasswordLookup pwLookup = name -> {
          if (name.equals(keyAlias)) {
            String aliasPwd = tokenKey.getAliasPassword();
            return (aliasPwd == null) ? new char[0] : aliasPwd.toCharArray();
          } 
          return new char[0];
        };
      JWKSet jwkSet = JWKSet.load(keyStore, pwLookup);
      return getJwkEncoded((JWK)jwkSet
          .getKeys()
          .stream()
          .filter(jwk1 -> jwk1.getKeyID().equals(tokenKey.getAlias()))
          .findFirst()
          .orElseThrow(() -> new TokenKeyException(String.format("No supported key with given password and alias '%s' found in keystore", new Object[] { keyAlias }))));
    } catch (JOSEException|GeneralSecurityException e) {
      throw new TokenKeyException(String.format("Problem reading key from '%s'", new Object[] { tokenKey.getValue() }), e);
    } 
  }
  
  private static byte[] getJwkEncoded(JWK jwk) throws JOSEException {
    String keyType = jwk.getKeyType().getValue();
    if (KeyType.RSA.getValue().equals(keyType) || KeyType.EC.getValue().equals(keyType)) {
      AsymmetricJWK asymmetricJwk = (AsymmetricJWK)jwk;
      if (jwk.isPrivate())
        return asymmetricJwk.toPrivateKey().getEncoded(); 
      return asymmetricJwk.toPublicKey().getEncoded();
    } 
    OctetSequenceKey octetSequenceKey = (OctetSequenceKey)jwk;
    return octetSequenceKey.toByteArray();
  }
}
