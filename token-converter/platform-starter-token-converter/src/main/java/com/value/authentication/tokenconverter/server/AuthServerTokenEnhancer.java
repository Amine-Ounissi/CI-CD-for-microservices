package com.value.authentication.tokenconverter.server;

import com.value.authentication.tokenconverter.configuration.TokenConverterProperties;
import java.util.HashMap;
import java.util.Map;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;

public class AuthServerTokenEnhancer implements TokenEnhancer {
  private TokenConverterProperties properties;
  
  public AuthServerTokenEnhancer(TokenConverterProperties tokenConverterProperties) {
    this.properties = tokenConverterProperties;
  }
  
  public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
    Map<String, Object> additionalInfo = new HashMap<>();
    additionalInfo.put("sub", this.properties.getClientId());
    additionalInfo.put("iss", "token-converter");
    ((DefaultOAuth2AccessToken)accessToken).setAdditionalInformation(additionalInfo);
    return accessToken;
  }
}
