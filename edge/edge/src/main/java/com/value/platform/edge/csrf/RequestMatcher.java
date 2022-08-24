package com.value.platform.edge.csrf;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.apache.commons.lang.StringUtils;
import org.springframework.http.HttpMethod;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

public class RequestMatcher implements ServerWebExchangeMatcher {
  private static final Set<HttpMethod> METHODS_TO_CHECK = new HashSet<>(
      Arrays.asList(HttpMethod.POST, HttpMethod.PUT, HttpMethod.PATCH, HttpMethod.DELETE));
  
  private String cookieName = "Authorization";
  
  public RequestMatcher(GatewayCsrfProperties csrfProperties) {
    String cookieName = csrfProperties.getCookieName();
    if (StringUtils.isNotEmpty(cookieName))
      this.cookieName = cookieName; 
  }
  
  public Mono<MatchResult> matches(ServerWebExchange exchange) {
    ServerHttpRequest request = exchange.getRequest();
    if (METHODS_TO_CHECK.contains(request.getMethod()) && request
      .getCookies().containsKey(this.cookieName))
      return MatchResult.match();
    return MatchResult.notMatch();
  }
}
