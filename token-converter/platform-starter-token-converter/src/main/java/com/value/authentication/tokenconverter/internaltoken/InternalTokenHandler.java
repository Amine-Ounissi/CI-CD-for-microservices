package com.value.authentication.tokenconverter.internaltoken;

import com.value.authentication.tokenconverter.mapper.ClaimsStore;
import com.value.authentication.tokenconverter.enhancer.InternalTokenEnhancer;
import com.value.authentication.tokenconverter.enhancer.ValidationEnhancer;
import com.value.authentication.tokenconverter.exception.TokenConverterValidationException;
import com.value.authentication.tokenconverter.internaltoken.model.InternalTokenResponse;
import com.value.authentication.tokenconverter.utils.TokenHelper;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import lombok.Generated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

public class InternalTokenHandler {
  @Generated
  private static final Logger log = LoggerFactory.getLogger(InternalTokenHandler.class);
  
  public static final String SIGNING_MODE_SYMMETRIC_KEY = "symmetric-key";
  
  public static final String SIGNING_MODE_ASYMMETRIC_KEY_PAIR = "asymmetric-key-pair";
  
  private JwtAccessTokenConverter converter;
  
  private JwtTokenStore tokenStore;
  
  private DefaultTokenServices tokenServices;
  
  private TokenHelper tokenHelper;
  
  private List<InternalTokenEnhancer> tokenEnhancerList;
  
  private List<ValidationEnhancer> validationEnhancerList;
  
  private ClaimsStore claimsStore;
  
  public InternalTokenHandler(TokenHelper tokenHelper, List<InternalTokenEnhancer> tokenEnhancerList, List<ValidationEnhancer> validationEnhancerList, ClaimsStore claimsStore) {
    this.tokenHelper = tokenHelper;
    this.tokenEnhancerList = tokenEnhancerList;
    this.validationEnhancerList = validationEnhancerList;
    this.claimsStore = claimsStore;
  }
  
  @PostConstruct
  public void init() {
    this.converter = this.tokenHelper.createAccessTokenConverter();
    this.tokenStore = this.tokenHelper.tokenStore(this.converter);
    createTokenServices();
  }
  
  private void createTokenServices() {
    this.tokenServices = new DefaultTokenServices();
    this.tokenServices.setTokenStore((TokenStore)this.tokenStore);
    this.tokenServices.setTokenEnhancer((TokenEnhancer)this.converter);
  }
  
  @Cacheable(value = {"accessTokenCache"}, key = "{#externalToken, #userCtxJwe}", condition = "#externalToken != null", unless = "#result == null")
  public InternalTokenResponse create(String externalToken, String userCtxJwe, Authentication authentication) throws TokenConverterValidationException {
    OAuth2Request request = new OAuth2Request(null, "Token-converter", authentication.getAuthorities(), true, null, null, "", null, null);
    OAuth2Authentication auth = new OAuth2Authentication(request, authentication);
    OAuth2AccessToken accessToken = this.tokenServices.createAccessToken(auth);
    if (!this.validationEnhancerList.isEmpty())
      validateToken(auth, accessToken); 
    Map<String, Object> claims = this.claimsStore.getClaims(accessToken, auth);
    String enhancedToken = enhanceToken(auth, accessToken);
    return new InternalTokenResponse(enhancedToken, claims);
  }
  
  private void validateToken(OAuth2Authentication auth, OAuth2AccessToken accessToken) throws TokenConverterValidationException {
    Map<String, Object> externalClaims = this.claimsStore.getClaims(accessToken, auth);
    for (ValidationEnhancer item : this.validationEnhancerList) {
      log.debug("Validating token with {}", item.getClass());
      item.validate(externalClaims);
    } 
  }
  
  private String enhanceToken(OAuth2Authentication auth, OAuth2AccessToken accessToken) {
    for (InternalTokenEnhancer enhancer : this.tokenEnhancerList) {
      log.debug("Enhancing token with {}", enhancer.getClass());
      accessToken = enhancer.enhance(accessToken, auth);
    } 
    return this.converter.enhance(accessToken, auth).getValue();
  }
}
