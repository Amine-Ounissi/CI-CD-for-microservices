package com.value.buildingblocks.jwt.internal.config;

import java.util.Collections;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;

public abstract class AbstractWebSecurityConfigurerAdapter extends WebSecurityConfigurerAdapter {
  private static final Logger logger = LoggerFactory.getLogger(AbstractWebSecurityConfigurerAdapter.class);
  
  protected void configure(HttpSecurity http) throws Exception {
    logger.debug("Applying default jwt stateless security configuration");
    ((HttpSecurity)http.sessionManagement()
      
      .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
      .and())
      
      .httpBasic().disable();
    configurePublicUrls(http);
    authorizeRequests(http.authorizeRequests());
    ((ExpressionUrlAuthorizationConfigurer.AuthorizedUrl)http.authorizeRequests().anyRequest()).authenticated();
  }
  
  protected void configurePublicUrls(HttpSecurity http) throws Exception {
    logger.debug("permitting all requests to /health, /actuator/health and /production-support/health");
    ((ExpressionUrlAuthorizationConfigurer.AuthorizedUrl)http
      .authorizeRequests()
      .antMatchers(new String[] { "/health", "/actuator/health", "/production-support/health" })).permitAll();
    List<String> customPublicUrls = getCustomPublicUrls();
    if (customPublicUrls != null && !customPublicUrls.isEmpty())
      customPublicUrls.forEach(url -> {
            try {
              logger.debug("permitting all requests to url[{}]", url);
              ((ExpressionUrlAuthorizationConfigurer.AuthorizedUrl)http.authorizeRequests().antMatchers(new String[] { url })).permitAll();
            } catch (Exception e) {
              logger.warn("Cannot grant access to everyone, to url : {}. Error: {}", url, e);
            } 
          }); 
  }
  
  protected List<String> getCustomPublicUrls() {
    return Collections.emptyList();
  }
  
  protected void authorizeRequests(ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry authorizeRequests) {}
}
