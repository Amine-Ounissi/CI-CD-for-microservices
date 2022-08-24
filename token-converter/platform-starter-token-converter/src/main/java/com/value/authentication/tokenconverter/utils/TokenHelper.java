package com.value.authentication.tokenconverter.utils;

import com.value.authentication.tokenconverter.configuration.TokenConverterProperties;
import com.value.authentication.tokenconverter.crypto.SignatureService;
import com.value.authentication.tokenconverter.crypto.SignerAndVerifier;
import com.value.authentication.tokenconverter.internaltoken.InternalUserAuthenticationConverter;
import org.springframework.security.oauth2.provider.token.DefaultAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.UserAuthenticationConverter;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

public class TokenHelper {
  private final SignatureService signatureService;
  
  public TokenHelper(TokenConverterProperties tokenConverterProperties) {
    this.signatureService = new SignatureService(tokenConverterProperties);
  }
  
  public JwtAccessTokenConverter createAccessTokenConverter() {
    JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
    SignerAndVerifier signerAndVerifier = this.signatureService.getSignerAndVerifier();
    converter.setSigner(signerAndVerifier);
    converter.setVerifier(signerAndVerifier);
    ((DefaultAccessTokenConverter)converter.getAccessTokenConverter())
      .setUserTokenConverter((UserAuthenticationConverter)new InternalUserAuthenticationConverter());
    return converter;
  }
  
  public JwtTokenStore tokenStore(JwtAccessTokenConverter converter) {
    return new JwtTokenStore(converter);
  }
}
