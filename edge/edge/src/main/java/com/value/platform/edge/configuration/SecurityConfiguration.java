package com.value.platform.edge.configuration;

import com.value.platform.edge.csrf.GatewayCsrfProperties;
import com.value.platform.edge.csrf.MobileRequestMatcher;
import com.value.platform.edge.csrf.RequestMatcher;
import com.value.platform.edge.csrf.WhitelistMatcher;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.autoconfigure.security.reactive.EndpointRequest;
import org.springframework.boot.actuate.health.HealthEndpoint;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.csrf.CookieServerCsrfTokenRepository;
import org.springframework.security.web.server.util.matcher.AndServerWebExchangeMatcher;
import org.springframework.security.web.server.util.matcher.NegatedServerWebExchangeMatcher;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers;
import reactor.core.publisher.Mono;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfiguration {

  private final GatewayActuatorProperties actuatorProperties;

  private final GatewayCsrfProperties csrfProperties;

  private final String roles;

  public SecurityConfiguration(@Value("${management.security.roles:ADMIN}") String roles,
    GatewayActuatorProperties actuatorProperties, GatewayCsrfProperties csrfProperties) {
    this.roles = roles;
    this.csrfProperties = csrfProperties;
    this.actuatorProperties = actuatorProperties;
  }

  @Bean
  public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
    ServerHttpSecurity.AuthorizeExchangeSpec authorizeExchangeSpec = http.authorizeExchange();
    applyActuatorPolicy(authorizeExchangeSpec, this.actuatorProperties);
    ServerHttpSecurity serverHttpSecurity = getServerHttpSecurity(authorizeExchangeSpec);
    applyCsrfPolicy(serverHttpSecurity, this.csrfProperties);
    return getSecurityWebFilterChain(serverHttpSecurity);
  }

  @Bean
  public ReactiveUserDetailsService userDetailsService() {
    Optional<String> user = this.actuatorProperties.getUser();
    Optional<String> password = this.actuatorProperties.getPassword();
    if (user.isPresent() && password.isPresent()) {
      UserDetails userDetails = User.withDefaultPasswordEncoder().username(user.get())
        .password(password.get()).roles(new String[]{this.roles}).build();
      return new MapReactiveUserDetailsService(userDetails);
    }
    return username -> Mono.empty();
  }

  private void applyActuatorPolicy(ServerHttpSecurity.AuthorizeExchangeSpec authorizeExchangeSpec,
    GatewayActuatorProperties actuatorProperties) {
    if (actuatorProperties.getSecurity().isEnabled()) {
      authorizeExchangeSpec
        .matchers(
          EndpointRequest.to(HealthEndpoint.class))
        .permitAll()
        .matchers(EndpointRequest.toAnyEndpoint())
        .hasRole(this.roles);
    }
  }

  private ServerHttpSecurity getServerHttpSecurity(
    ServerHttpSecurity.AuthorizeExchangeSpec authorizeExchangeSpec) {
    return authorizeExchangeSpec
      .anyExchange().permitAll()
      .and()
      .httpBasic()
      .and();
  }

  private void applyCsrfPolicy(ServerHttpSecurity serverHttpSecurity,
    GatewayCsrfProperties csrfProperties) {
    if (csrfProperties.isEnabled()) {
      serverHttpSecurity.csrf()
        .csrfTokenRepository(getCookieRepository())
        .requireCsrfProtectionMatcher(new AndServerWebExchangeMatcher(
          new RequestMatcher(csrfProperties),
          new NegatedServerWebExchangeMatcher(ServerWebExchangeMatchers
            .matchers(new WhitelistMatcher(csrfProperties),
              new MobileRequestMatcher(csrfProperties)))));
    } else {
      serverHttpSecurity.csrf().disable();
    }
  }

  private SecurityWebFilterChain getSecurityWebFilterChain(ServerHttpSecurity serverHttpSecurity) {
    return serverHttpSecurity
      .headers().disable()
      .formLogin().disable()
      .logout().disable()
      .build();
  }

  private static CookieServerCsrfTokenRepository getCookieRepository() {
    CookieServerCsrfTokenRepository cookieCsrfTokenRepository = new CookieServerCsrfTokenRepository();
    cookieCsrfTokenRepository.setCookieHttpOnly(false);
    cookieCsrfTokenRepository.setCookiePath("/");
    return cookieCsrfTokenRepository;
  }
}
