package com.value.buildingblocks.backend.security.auth.config;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.util.CollectionUtils;

public class MutualTlsUserDetailsService implements UserDetailsService {

  public static final String MTLS_AUTHORITY = "MTLS";

  private static final Logger log = LoggerFactory.getLogger(MutualTlsUserDetailsService.class);

  @Autowired
  private MutualTlsConfigurationProperties mutualTlsConfigurationProperties;

  public UserDetails loadUserByUsername(String id) {
    Collection<GrantedAuthority> grantedAuthorities =
      isUserAuthorised(id) ? Collections.singleton(new SimpleGrantedAuthority("MTLS"))
        : Collections.<GrantedAuthority>emptySet();
    return (UserDetails) new User(id, "", grantedAuthorities);
  }

  protected boolean isUserAuthorised(String username) {
    if (!this.mutualTlsConfigurationProperties.isValidateClient()) {
      return true;
    }
    List<MutualTlsConfigurationProperties.TrustedClient> trustedClients = this.mutualTlsConfigurationProperties
      .getTrustedClients();
    if (CollectionUtils.isEmpty(trustedClients)) {
      return false;
    }
    if (trustedClients.stream().anyMatch(c -> c.getSubject().equals(username))) {
      return true;
    }
    log.warn(
      "User presented in MTLS client certificate ({}) is not in the trusted client list configured via the value.security.mtls.trusted-clients property.",
      username);
    return false;
  }

  public String toString() {
    return getClass().getSimpleName() + "(validateClient:" + this.mutualTlsConfigurationProperties
      .isValidateClient() + ",trustedClients:" + this.mutualTlsConfigurationProperties
      .getTrustedClients() + ")";
  }
}
