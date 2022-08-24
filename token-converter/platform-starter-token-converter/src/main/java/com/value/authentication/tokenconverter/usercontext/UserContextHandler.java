package com.value.authentication.tokenconverter.usercontext;

import com.value.authentication.tokenconverter.enhancer.InternalTokenEnhancer;
import com.nimbusds.jose.JWEDecrypter;
import com.nimbusds.jose.JWEObject;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.DirectDecrypter;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import lombok.Generated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.util.WebUtils;

public class UserContextHandler implements InternalTokenEnhancer {
  @Generated
  private static final Logger log = LoggerFactory.getLogger(UserContextHandler.class);
  
  public static final String USER_CONTEXT_COOKIE_NAME = "USER_CONTEXT";
  
  private static final String USER_CONTEXT_SERVICE_AGREEMENT_TOKEN_KEY = "said";
  
  private static final String USER_CONTEXT_LEGAL_ENTITY_TOKEN_KEY = "leid";
  
  private static final String USER_CONTEXT_SIGN_ENC_ENV_KEY = "USERCTX_KEY";
  
  private static final String SUBJECT = "sub";
  
  private static final String KEY_MISSING_ERROR = "No user context key (%s) configured. Not attempting decryption of user context cookie";
  
  public static final String USER_CTX_COOKIE_NOT_PRESENT = "No user context cookie (%s) detected";
  
  private Optional<Map<String, Object>> getUserContext(HttpServletRequest request) {
    Optional<String> userContextKey = getUserContextKey();
    Optional<String> encryptedCookie = extractUserContextDataFromCookie(request);
    if (!encryptedCookie.isPresent() || !userContextKey.isPresent()) {
      log.debug(!userContextKey.isPresent() ? String.format("No user context key (%s) configured. Not attempting decryption of user context cookie", new Object[] { "USERCTX_KEY" }) : String.format("No user context cookie (%s) detected", new Object[] { "USER_CONTEXT" }));
      return Optional.empty();
    } 
    return Optional.of(decryptAndExtractClaims(encryptedCookie.get(), userContextKey.get()));
  }
  
  private Map<String, Object> decryptAndExtractClaims(String encryptedCookie, String key) {
    Map<String, Object> userContextClaims = new HashMap<>();
    log.debug("Attempting to decrypt user context");
    try {
      byte[] secretKey = key.getBytes(Charset.defaultCharset());
      JWEObject jweObject = JWEObject.parse(encryptedCookie);
      jweObject.decrypt((JWEDecrypter)new DirectDecrypter(secretKey));
      SignedJWT signedJwt = jweObject.getPayload().toSignedJWT();
      if (signedJwt.verify((JWSVerifier)new MACVerifier(secretKey))) {
        log.debug("User context decrypted successfully");
        addUserContextClaims(userContextClaims, signedJwt);
      } 
    } catch (Exception e) {
      log.warn("Unable to decrypt user context");
    } 
    return userContextClaims;
  }
  
  private void addUserContextClaims(Map<String, Object> userContextClaims, SignedJWT signedJwt) throws ParseException {
    JWTClaimsSet claims = signedJwt.getJWTClaimsSet();
    log.debug("User context claims :: {}", claims);
    if (shouldMapUserContext(claims)) {
      log.debug("Mapping user context claims");
      if (claims.getStringClaim("said") != null)
        userContextClaims.put("said", claims
            .getStringClaim("said")); 
      if (claims.getStringClaim("leid") != null)
        userContextClaims.put("leid", claims
            .getStringClaim("leid")); 
    } else {
      log.debug("Authenticated user does not match user context - not mapping claims");
    } 
  }
  
  private Optional<String> extractUserContextDataFromCookie(HttpServletRequest request) {
    Cookie cookie = WebUtils.getCookie(request, "USER_CONTEXT");
    if (cookie != null)
      return Optional.of(cookie.getValue()); 
    return Optional.empty();
  }
  
  private Optional<String> getUserContextKey() {
    if (StringUtils.hasText(System.getenv("USERCTX_KEY")))
      return Optional.of(System.getenv("USERCTX_KEY")); 
    if (StringUtils.hasText(System.getProperty("USERCTX_KEY")))
      return Optional.of(System.getProperty("USERCTX_KEY")); 
    return Optional.empty();
  }
  
  public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
    HttpServletRequest request = ((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getRequest();
    Optional<Map<String, Object>> userContext = getUserContext(request);
    userContext.ifPresent(userContextClaims -> accessToken.getAdditionalInformation().putAll(userContextClaims));
    return accessToken;
  }
  
  private boolean shouldMapUserContext(JWTClaimsSet userContextClaims) throws ParseException {
    return (userContextClaims.getStringClaim("sub") == null || userContextClaims
      .getStringClaim("sub").equals(getUsername()));
  }
  
  private String getUsername() {
    return SecurityContextHolder.getContext().getAuthentication().getName();
  }
}
