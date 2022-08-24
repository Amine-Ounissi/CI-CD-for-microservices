package com.value.buildingblocks.security.csrf;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.SecurityBuilder;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRepository;

public class CsrfWebSecurityConfigurer extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {
  private static final Logger logger = LoggerFactory.getLogger(CsrfWebSecurityConfigurer.class);
  
  private static final String CSRF_ENABLED_PROPERTY = "buildingblocks.security.csrf.enabled";
  
  public static CsrfWebSecurityConfigurer configuration() {
    return new CsrfWebSecurityConfigurer();
  }
  
  public void init(HttpSecurity builder) throws Exception {
    logger.debug("Applying CSRF configuration");
    boolean csrfEnabled = isCsrfEnabled(builder);
    builder.csrf()
      .requireCsrfProtectionMatcher(new CsrfRequestMatcher(csrfEnabled))
      .csrfTokenRepository((CsrfTokenRepository)getCookieRepository());
    super.init(builder);
  }
  
  private static boolean isCsrfEnabled(HttpSecurity builder) {
    ApplicationContext applicationContext = (ApplicationContext)builder.getSharedObject(ApplicationContext.class);
    return isCsrfEnabledByProperty(applicationContext);
  }
  
  private static CookieCsrfTokenRepository getCookieRepository() {
    CookieCsrfTokenRepository cookieCsrfTokenRepository = new CookieCsrfTokenRepository();
    cookieCsrfTokenRepository.setCookieHttpOnly(false);
    cookieCsrfTokenRepository.setCookiePath("/");
    return cookieCsrfTokenRepository;
  }
  
  private static boolean isCsrfEnabledByProperty(ApplicationContext applicationContext) {
    if (applicationContext == null)
      return true; 
    String csrfEnabledPropertyValue = applicationContext.getEnvironment().getProperty("buildingblocks.security.csrf.enabled");
    if (csrfEnabledPropertyValue == null || csrfEnabledPropertyValue.trim().length() == 0)
      return true; 
    return Boolean.parseBoolean(csrfEnabledPropertyValue);
  }
  
  public static class Beans {
    private boolean isCsrfEnabledByParam = true;
    
    @Autowired
    private ApplicationContext applicationContext;
    
    public Beans() {}
    
    public Beans(boolean isCsrdEnabled) {
      this.isCsrfEnabledByParam = isCsrdEnabled;
    }
    
    @Bean
    public CsrfRequestMatcher csrfRequestMatcher() {
      boolean isCsrfEnabled = (CsrfWebSecurityConfigurer.isCsrfEnabledByProperty(this.applicationContext) && this.isCsrfEnabledByParam);
      return new CsrfRequestMatcher(isCsrfEnabled);
    }
    
    @Bean
    public CookieCsrfTokenRepository cookieCsrfTokenRepository() {
      return CsrfWebSecurityConfigurer.getCookieRepository();
    }
  }
}
