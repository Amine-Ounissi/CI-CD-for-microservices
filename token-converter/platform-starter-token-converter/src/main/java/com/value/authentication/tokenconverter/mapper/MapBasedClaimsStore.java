package com.value.authentication.tokenconverter.mapper;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.stereotype.Component;

@Component
@Scope(value = "request", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class MapBasedClaimsStore implements ClaimsStore {
  private static final List<String> CLAIMS = Arrays.asList(new String[] { "exp", "iat" });
  
  private Map<String, Object> storedClaims = new HashMap<>();
  
  public void setClaims(Map<String, Object> claims) {
    storeClaimsWhileConvertingDateClaimsToLong(claims);
  }
  
  public Map<String, Object> getClaims(OAuth2AccessToken internalToken, OAuth2Authentication auth) {
    return this.storedClaims;
  }
  
  private void storeClaimsWhileConvertingDateClaimsToLong(Map<String, Object> claims) {
    for (Map.Entry<String, Object> entry : claims.entrySet()) {
      if (entry.getValue() instanceof Date && dateRequiresConversion(entry.getKey())) {
        this.storedClaims.put(entry.getKey(), Long.valueOf(convertDateToEpochSecond((Date)entry.getValue())));
        continue;
      } 
      this.storedClaims.put(entry.getKey(), entry.getValue());
    } 
  }
  
  private long convertDateToEpochSecond(Date date) {
    return date.getTime() / 1000L;
  }
  
  private boolean dateRequiresConversion(String claim) {
    return CLAIMS.contains(claim);
  }
}
