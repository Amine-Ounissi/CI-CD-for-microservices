package com.value.authentication.tokenconverter.server;

import com.value.authentication.tokenconverter.configuration.TokenConverterProperties;
import com.value.authentication.tokenconverter.utils.TokenHelper;
import java.util.Arrays;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.oauth2.provider.token.TokenEnhancerChain;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

@Configuration
@EnableAuthorizationServer
public class AuthServer extends AuthorizationServerConfigurerAdapter {

  @Autowired
  private AuthServerTokenEnhancer authServerTokenEnhancer;

  private TokenConverterProperties properties;

  private JwtAccessTokenConverter converter;

  private JwtTokenStore tokenStore;

  private DefaultTokenServices tokenServices;

  private TokenHelper tokenHelper;

  public AuthServer(TokenConverterProperties tokenConverterProperties, TokenHelper tokenHelper) {
    this.properties = tokenConverterProperties;
    this.tokenHelper = tokenHelper;
  }

  @PostConstruct
  public void init() {
    this.converter = this.tokenHelper.createAccessTokenConverter();
    this.tokenStore = this.tokenHelper.tokenStore(this.converter);
    createTokenServices();
  }

  private void createTokenServices() {
    TokenEnhancerChain enhancerChain = new TokenEnhancerChain();
    enhancerChain.setTokenEnhancers(Arrays.asList(this.authServerTokenEnhancer,
      (TokenEnhancer) this.converter));
    this.tokenServices = new DefaultTokenServices();
    this.tokenServices.setTokenStore((TokenStore) this.tokenStore);
    this.tokenServices.setTokenEnhancer((TokenEnhancer) enhancerChain);
    this.tokenServices.setAccessTokenValiditySeconds(this.properties.getTokenTimeout());
  }

  public void configure(AuthorizationServerEndpointsConfigurer endpoints) {
    endpoints
      .approvalStoreDisabled()
      .tokenServices((AuthorizationServerTokenServices) this.tokenServices)
      .tokenStore((TokenStore) this.tokenStore)
      .tokenEnhancer((TokenEnhancer) this.converter);
  }

  public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
    clients
      .inMemory()
      .withClient(this.properties.getClientId())
      .secret("{noop}" + this.properties.getSecret())
      .authorizedGrantTypes(new String[]{"client_credentials"}).scopes(new String[]{"api:service"})
      .resourceIds("vds-oauth")
      .and()
      .withClient(this.properties.getActuatorClientId())
      .secret("{noop}" + this.properties.getActuatorSecret())
      .authorizedGrantTypes(new String[]{"client_credentials"}).scopes(new String[]{"api:service"})
      .authorities("ROLE_ACTUATOR")
      .resourceIds("vds-oauth");
  }

  public void configure(AuthorizationServerSecurityConfigurer security) {
    security
      .checkTokenAccess("isAuthenticated()")
      .allowFormAuthenticationForClients();
  }
}
