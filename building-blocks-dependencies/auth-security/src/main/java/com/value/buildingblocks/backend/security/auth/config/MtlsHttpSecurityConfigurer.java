package com.value.buildingblocks.backend.security.auth.config;

import com.value.buildingblocks.security.HttpSecurityConfigurationException;
import com.value.buildingblocks.security.HttpSecurityConfigurer;
import java.util.Arrays;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.util.CollectionUtils;

public class MtlsHttpSecurityConfigurer implements HttpSecurityConfigurer {

  private static final Logger log = LoggerFactory.getLogger(MtlsHttpSecurityConfigurer.class);

  @Autowired
  protected MutualTlsConfigurationProperties mutualTlsConfigurationProperties;

  @Autowired(required = false)
  @Qualifier("mutualTlsUserDetailsService")
  protected UserDetailsService mutualTlsUserDetailsService;

  public void configure(HttpSecurity http) throws HttpSecurityConfigurationException {
    if (this.mutualTlsConfigurationProperties.isValidateClient() &&
      CollectionUtils.isEmpty(this.mutualTlsConfigurationProperties.getTrustedClients())) {
      log.warn(
        "No trusted clients are configured via value.security.mtls.trusted-clients property but the validate client flag is enabled.");
    }
    if (!this.mutualTlsConfigurationProperties.isValidateClient() &&
      !CollectionUtils.isEmpty(this.mutualTlsConfigurationProperties.getTrustedClients())) {
      log.warn(
        "Trusted clients are configured via value.security.mtls.trusted-clients property but the validate client flag is disabled.");
    }
    String[] mtlsPaths = this.mutualTlsConfigurationProperties.getPaths().<String>toArray(
      new String[0]);
    try {
      ((HttpSecurity) ((ExpressionUrlAuthorizationConfigurer.AuthorizedUrl) http.authorizeRequests()
        .antMatchers(mtlsPaths))
        .hasAuthority("MTLS").and()).x509()
        .subjectPrincipalRegex(this.mutualTlsConfigurationProperties.getSubjectPrincipalRegex())
        .userDetailsService(this.mutualTlsUserDetailsService);
    } catch (Exception e) {
      throw new HttpSecurityConfigurationException("Unable to configure mTLS paths " +
        Arrays.toString(mtlsPaths), e);
    }
  }

  public String toString() {
    return getClass().getSimpleName() + ": with principal " + this.mutualTlsConfigurationProperties
      .getSubjectPrincipalRegex() + " for paths " + this.mutualTlsConfigurationProperties
      .getPaths() + " UserDetailsService " + this.mutualTlsUserDetailsService;
  }
}
