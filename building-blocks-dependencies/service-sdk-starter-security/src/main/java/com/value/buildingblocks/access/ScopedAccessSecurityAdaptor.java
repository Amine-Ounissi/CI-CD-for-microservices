package com.value.buildingblocks.access;

import com.value.buildingblocks.security.HttpSecurityConfigurationException;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.access.expression.SecurityExpressionHandler;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;
import org.springframework.util.AntPathMatcher;

public class ScopedAccessSecurityAdaptor extends WebSecurityConfigurerAdapter {

  private static final Logger log = LoggerFactory.getLogger(ScopedAccessSecurityAdaptor.class);

  private final ScopedAccessProperties properties;

  public ScopedAccessSecurityAdaptor(ScopedAccessProperties properties) {
    this.properties = properties;
  }

  public void configure(HttpSecurity http) throws HttpSecurityConfigurationException {
    try {
      ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry expressionInterceptUrlRegistry = http
        .authorizeRequests();
      expressionInterceptUrlRegistry.expressionHandler(new UserScopeExpressionHandler());
      for (Map.Entry<String, ScopedAccessProperties.AccessRule> entry : this.properties
        .getSortedRules()) {
        applyRule(expressionInterceptUrlRegistry, entry.getKey(), entry.getValue());
      }
      if (!this.properties.getRules().containsKey("deny-last-rule")) {
        ScopedAccessProperties.AccessRule denyLastRule = new ScopedAccessProperties.AccessRule();
        denyLastRule.setPaths("/service-api/**", "/client-api/**", "/public-api/**",
          "/integration-api/**");
        denyLastRule.setExpression("denyAll()");
        applyRule(expressionInterceptUrlRegistry, "deny-last-rule", denyLastRule);
      }
      log.info("Configured {} rules.", this.properties.getRules().size());
    } catch (Exception e) {
      throw new HttpSecurityConfigurationException("Unexpected problem...", e);
    }
  }

  protected void applyRule(
    ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry expressionInterceptUrlRegistry,
    String name, ScopedAccessProperties.AccessRule rule) throws HttpSecurityConfigurationException {
    String accessExpression = (new ScopedAccessExpressionBuilder()).withAccessRule(rule).build();
    try {
      if (log.isDebugEnabled()) {
        log.debug("value.security.rules.{}={} -> '{}'", name, rule, accessExpression);
      }
      if (rule.getMethods() == null || (rule.getMethods()).length == 0) {
        expressionInterceptUrlRegistry.mvcMatchers(rule.getPaths()).access(accessExpression);
      } else {
        for (HttpMethod method : rule.getMethods()) {
          expressionInterceptUrlRegistry.mvcMatchers(method, rule.getPaths())
            .access(accessExpression);
        }
      }
    } catch (Exception e) {
      log.error("Invalid rule: {}={}", name, accessExpression);
      throw new HttpSecurityConfigurationException("Invalid rule: " + name + "=" + accessExpression,
        e);
    }
  }

  public String toString() {
    return String.valueOf(this.properties);
  }

  public HttpSecurity getHttpSecurity() throws Exception {
    return getHttp();
  }
}
