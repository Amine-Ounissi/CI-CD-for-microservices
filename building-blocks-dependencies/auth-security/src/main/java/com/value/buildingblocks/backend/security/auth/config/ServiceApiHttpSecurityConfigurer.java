package com.value.buildingblocks.backend.security.auth.config;

import com.value.buildingblocks.security.HttpSecurityConfigurationException;
import com.value.buildingblocks.security.HttpSecurityConfigurer;
import java.util.Arrays;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;

public class ServiceApiHttpSecurityConfigurer implements HttpSecurityConfigurer {

  @Autowired
  private PeerServiceExpressionHandler peerServiceExpressionHandler;

  @Autowired
  private ServiceApiConfigurationProperties serviceApiConfigurationProperties;

  public void configure(HttpSecurity http) throws HttpSecurityConfigurationException {
    String[] servicePaths = this.serviceApiConfigurationProperties.getPaths().toArray(
      new String[this.serviceApiConfigurationProperties.getPaths().size()]);
    try {
      ((ExpressionUrlAuthorizationConfigurer.AuthorizedUrl) http.authorizeRequests()
        .antMatchers(servicePaths)).access("#peerService")
        .expressionHandler(this.peerServiceExpressionHandler);
    } catch (Exception e) {
      throw new HttpSecurityConfigurationException(
        "Unable to configure " + Arrays.toString(servicePaths), e);
    }
  }

  public String toString() {
    return getClass().getSimpleName() + ": authorizeRequests() with "
      + this.peerServiceExpressionHandler + " for paths " + this.serviceApiConfigurationProperties
      .getPaths();
  }
}
