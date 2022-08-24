package com.value.authentication.tokenconverter.service.impl;

import com.value.authentication.tokenconverter.config.Oauth2TokenConverterConfig;
import com.value.authentication.tokenconverter.exception.InvalidAlgorithmException;
import com.value.authentication.tokenconverter.exception.JwtException;
import com.value.authentication.tokenconverter.service.JwsVerificationService;
import com.github.benmanes.caffeine.cache.Cache;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.jwk.source.RemoteJWKSet;
import com.nimbusds.jose.proc.JWSKeySelector;
import com.nimbusds.jose.proc.JWSVerificationKeySelector;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.proc.ConfigurableJWTProcessor;
import java.net.URL;
import java.text.ParseException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class JwsVerificationServiceImpl implements JwsVerificationService {
  private static final Logger log = LoggerFactory.getLogger(JwsVerificationServiceImpl.class);
  
  private final List<JWSAlgorithm> supportedAlgorithms = Arrays.asList(JWSAlgorithm.HS256,
    JWSAlgorithm.HS384, JWSAlgorithm.HS512, JWSAlgorithm.RS256, JWSAlgorithm.RS384, JWSAlgorithm.RS512,
    JWSAlgorithm.ES256, JWSAlgorithm.ES384, JWSAlgorithm.ES512, JWSAlgorithm.PS256,
    JWSAlgorithm.PS384, JWSAlgorithm.PS512);
  
  private final Cache<String, JWKSource<SecurityContext>> keyCache;
  
  private final ConfigurableJWTProcessor<SecurityContext> jwtProcessor;
  
  private final Oauth2TokenConverterConfig oauth2TokenConverterConfig;
  
  @Autowired
  public JwsVerificationServiceImpl(ConfigurableJWTProcessor<SecurityContext> jwtProcessor, Oauth2TokenConverterConfig oauth2TokenConverterConfig, Cache<String, JWKSource<SecurityContext>> keyCache) {
    this.keyCache = keyCache;
    this.jwtProcessor = jwtProcessor;
    this.oauth2TokenConverterConfig = oauth2TokenConverterConfig;
  }
  
  public Map<String, Object> verifyToken(String token, URL url, String algorithm, String keyId) {
    JWTClaimsSet claimsSet;
    JWKSource<SecurityContext> keySource = retrieveOrCreateKeySource(url, keyId);
    JWSAlgorithm expectedJwsAlg = verifyAndGetAlgorithm(algorithm);
    JWSVerificationKeySelector jWSVerificationKeySelector = new JWSVerificationKeySelector(expectedJwsAlg, keySource);
    this.jwtProcessor.setJWSKeySelector(jWSVerificationKeySelector);
    try {
      claimsSet = this.jwtProcessor.process(token, null);
    } catch (ParseException|com.nimbusds.jose.proc.BadJOSEException|com.nimbusds.jose.JOSEException e) {
      log.debug("Token failed verification: {}", token, e);
      throw new JwtException(e.getMessage());
    } 
    return claimsSet.getClaims();
  }
  
  private JWSAlgorithm verifyAndGetAlgorithm(String algorithm) {
    if (this.oauth2TokenConverterConfig.getWhitelistedAlgorithms().stream().noneMatch(algorithm::equalsIgnoreCase))
      throw new InvalidAlgorithmException("The token algorithm is not whitelisted :: " + algorithm);
    JWSAlgorithm configuredAlgorithm = JWSAlgorithm.parse(algorithm.toUpperCase());
    if (this.supportedAlgorithms.stream().noneMatch(configuredAlgorithm::equals))
      throw new InvalidAlgorithmException("The token algorithm is not supported :: " + algorithm); 
    return configuredAlgorithm;
  }
  
  private JWKSource<SecurityContext> retrieveOrCreateKeySource(URL url, String keyId) {
    return this.keyCache.get(keyId, k -> new RemoteJWKSet(url));
  }
}
