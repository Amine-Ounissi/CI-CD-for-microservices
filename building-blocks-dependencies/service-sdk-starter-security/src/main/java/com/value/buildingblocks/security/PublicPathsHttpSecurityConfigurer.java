package com.value.buildingblocks.security;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;
import org.springframework.util.CollectionUtils;

public class PublicPathsHttpSecurityConfigurer implements HttpSecurityConfigurer {

  private static final Logger log = LoggerFactory
    .getLogger(PublicPathsHttpSecurityConfigurer.class);

  List<String> publicPaths;

  public PublicPathsHttpSecurityConfigurer(List<String> publicPaths) {
    this.publicPaths = publicPaths;
  }

  public void setPublicPaths(List<String> publicPaths) {
    this.publicPaths = publicPaths;
  }

  public void configure(HttpSecurity http) throws HttpSecurityConfigurationException {
    if (!CollectionUtils.isEmpty(this.publicPaths)) {
      String[] publicPathsArray = this.publicPaths.toArray(new String[this.publicPaths.size()]);
      try {
        log.debug("permitting all requests to paths [{}]", publicPathsArray);
        ((ExpressionUrlAuthorizationConfigurer.AuthorizedUrl) http.authorizeRequests()
          .antMatchers(publicPathsArray)).permitAll();
      } catch (Exception e) {
        throw new HttpSecurityConfigurationException(
          "Cannot grant permitAll access to configured public paths", e);
      }
    }
  }

  public String toString() {
    return getClass().getSimpleName() + ": all requests permitAll() for paths " + this.publicPaths;
  }
}
