package com.value.authentication.tokenconverter.mapper;

import com.value.authentication.tokenconverter.configuration.TokenConverterProperties;
import com.value.authentication.tokenconverter.enhancer.InternalTokenEnhancer;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;

public class TokenClaimMapper implements InternalTokenEnhancer {
  private static final Logger log = LoggerFactory.getLogger(TokenClaimMapper.class);
  
  private TokenConverterProperties tokenConverterProperties;
  
  private ClaimsStore claimStore;
  
  public TokenClaimMapper(TokenConverterProperties tokenConverterProperties, ClaimsStore claimStore) {
    this.tokenConverterProperties = tokenConverterProperties;
    this.claimStore = claimStore;
  }
  
  public OAuth2AccessToken enhance(OAuth2AccessToken internalAccessToken, OAuth2Authentication authentication) {
    Map<String, Object> externalTokenClaims = this.claimStore.getClaims(internalAccessToken, authentication);
    if (!this.tokenConverterProperties.getClaimsMapping().isEmpty() && !externalTokenClaims.isEmpty())
      mapClaimsFromExternalToken(internalAccessToken, externalTokenClaims); 
    return internalAccessToken;
  }
  
  private void mapClaimsFromExternalToken(OAuth2AccessToken internalAccessToken, Map<String, Object> claims) {
    for (TokenConverterProperties.ClaimMapping claimMapping : this.tokenConverterProperties.getClaimsMapping())
      mapClaim(internalAccessToken, claims, claimMapping); 
  }
  
  private void mapClaim(OAuth2AccessToken internalAccessToken, Map<String, Object> claims, TokenConverterProperties.ClaimMapping claimMapping) {
    String externalTokenKey = claimMapping.getExternal();
    if (claims.containsKey(externalTokenKey)) {
      internalAccessToken.getAdditionalInformation().putIfAbsent(claimMapping.getInternal(), claims
          .get(externalTokenKey));
    } else {
      if (claimMapping.isRequired())
        throw new IllegalArgumentException("Token is missing a required claim: " + externalTokenKey); 
      log.debug("Optional claim {} not present in the external JWT, skipping mapping", externalTokenKey);
    } 
  }
}
