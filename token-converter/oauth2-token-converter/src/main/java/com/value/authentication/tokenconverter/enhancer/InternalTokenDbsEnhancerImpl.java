package com.value.authentication.tokenconverter.enhancer;

import com.value.authentication.tokenconverter.client.DbsUserClient;
import com.value.authentication.tokenconverter.config.Oauth2TokenConverterConfig;
import com.value.authentication.tokenconverter.configuration.TokenConverterProperties;
import com.value.authentication.tokenconverter.internaltoken.InternalTokenDbsEnhancer;
import com.value.authentication.tokenconverter.mapper.ClaimsStore;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.stereotype.Service;

@Service
public class InternalTokenDbsEnhancerImpl extends InternalTokenDbsEnhancer {
  private static final Logger log = LoggerFactory.getLogger(InternalTokenDbsEnhancerImpl.class);
  
  private final DbsUserClient client;
  
  private final Oauth2TokenConverterConfig oauth2TokenConverterConfig;
  
  @Autowired
  public InternalTokenDbsEnhancerImpl(DbsUserClient client, ClaimsStore claimsStore, TokenConverterProperties tokenConverterProperties, Oauth2TokenConverterConfig oauth2TokenConverterConfig) {
    super(claimsStore, tokenConverterProperties);
    this.client = client;
    this.oauth2TokenConverterConfig = oauth2TokenConverterConfig;
  }
  
  public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
    OAuth2AccessToken token = super.enhance(accessToken, authentication);
    Map<String, Object> claims = getClaimsStore().getClaims(accessToken, authentication);
    token.getAdditionalInformation().put("sub", claims.get(getClaimName()));
    String userId = (String)claims.get("sub");
    token.getAdditionalInformation().put("inuid", userId);
    if (getConfiguration().getDbs().isPropagationEnabled()) {
      log.debug("DBS ID propagation is enabled. Retrieving `leid` from DBS.");
      this.client.getUserInfoFromUserInternalId(userId)
        .ifPresent(ui -> token.getAdditionalInformation().put("leid", ui.getLeid()));
    } 
    return token;
  }
  
  private String getClaimName() {
    return this.oauth2TokenConverterConfig
      .isUserinfoVerification() ? this.oauth2TokenConverterConfig
      .getUserinfoUsernameClaimName() : this.oauth2TokenConverterConfig
      .getJwksUsernameClaimName();
  }
}
