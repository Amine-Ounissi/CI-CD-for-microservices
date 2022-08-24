package com.value.authentication.tokenconverter.internaltoken;

import com.value.authentication.tokenconverter.configuration.TokenConverterProperties;
import com.value.authentication.tokenconverter.mapper.ClaimsStore;
import com.value.authentication.tokenconverter.enhancer.InternalTokenEnhancer;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;

public abstract class InternalTokenDbsEnhancer implements InternalTokenEnhancer {
  protected static final String SUB_CLAIM = "sub";
  
  protected static final String ISS_CLAIM = "iss";
  
  protected static final String IDP_CLAIM = "idp";
  
  protected static final String INUID_CLAIM = "inuid";
  
  protected static final String LEID_CLAIM = "leid";
  
  private final ClaimsStore claimsStore;
  
  private final TokenConverterProperties configuration;
  
  public InternalTokenDbsEnhancer(ClaimsStore claimsStore, TokenConverterProperties tokenConverterProperties) {
    this.claimsStore = claimsStore;
    this.configuration = tokenConverterProperties;
  }
  
  public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
    Map<String, Object> claims = this.claimsStore.getClaims(accessToken, authentication);
    Map<String, Object> idp = new HashMap<>();
    Optional.ofNullable(claims.get("sub")).ifPresent(sub -> idp.put("sub", sub));
    Optional.ofNullable(claims.get("iss")).ifPresent(iss -> idp.put("iss", iss));
    accessToken.getAdditionalInformation().put("idp", idp);
    return accessToken;
  }
  
  protected ClaimsStore getClaimsStore() {
    return this.claimsStore;
  }
  
  protected TokenConverterProperties getConfiguration() {
    return this.configuration;
  }
}
