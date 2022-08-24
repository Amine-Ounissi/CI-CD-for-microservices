package com.value.buildingblocks.security;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Order(2147483640)
@Configuration("coreHttpSecurityConfigurerAdapter")
@ConditionalOnProperty(name = {"value.security.http.enabled", "value.security.http.adapter.enabled"}, havingValue = "true", matchIfMissing = true)
@ConditionalOnMissingBean({CoreHttpSecurityConfigurerAdapter.class})
public class CoreHttpSecurityConfigurerAdapter extends WebSecurityConfigurerAdapter {
  private static final Logger log = LoggerFactory.getLogger(CoreHttpSecurityConfigurerAdapter.class);
  
  public static final int CORE_SECURITY_ORDER = 2147483640;
  
  @Autowired
  private List<HttpSecurityConfigurer> configurers;
  
  public void setConfigurers(List<HttpSecurityConfigurer> configurers) {
    this.configurers = configurers;
  }
  
  protected void configure(HttpSecurity http) throws Exception {
    if (log.isInfoEnabled())
      log.info(toString()); 
    for (HttpSecurityConfigurer configurer : this.configurers)
      configurer.configure(http); 
  }
  
  public String toString() {
    int i = 0;
    StringBuilder sb = new StringBuilder("**CoreHttpSecurityConfigurerAdapter**\n");
    for (HttpSecurityConfigurer configurer : this.configurers) {
      try {
        sb.append(++i).append(". Configuring ").append(configurer).append("\n");
      } catch (Exception e) {
        log.debug("Error during toString", e);
        sb.append(configurer.getClass().getSimpleName()).append(": ").append(e).append("\n");
      } 
    } 
    return sb.toString();
  }
}
