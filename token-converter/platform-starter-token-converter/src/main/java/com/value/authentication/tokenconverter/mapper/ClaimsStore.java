package com.value.authentication.tokenconverter.mapper;

import java.util.Map;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;

public interface ClaimsStore {
  void setClaims(Map<String, Object> paramMap);
  
  Map<String, Object> getClaims(OAuth2AccessToken paramOAuth2AccessToken,
    OAuth2Authentication paramOAuth2Authentication);
}
