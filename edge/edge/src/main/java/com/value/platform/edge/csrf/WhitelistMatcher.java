package com.value.platform.edge.csrf;

import java.util.ArrayList;
import java.util.List;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatcher;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

public class WhitelistMatcher implements ServerWebExchangeMatcher {

  private static final AntPathMatcher antPathMatcher = new AntPathMatcher();

  private final List<String> ignoredPaths = new ArrayList<>();

  public WhitelistMatcher(GatewayCsrfProperties csrfProperties) {
    List<String> ignoredPaths = csrfProperties.getIgnoredPaths();
    if (ignoredPaths != null) {
      this.ignoredPaths.addAll(ignoredPaths);
    }
  }

  public Mono<MatchResult> matches(ServerWebExchange serverWebExchange) {
    return Mono.just(serverWebExchange.getRequest().getPath().pathWithinApplication().value())
      .filter((path) -> this.ignoredPaths.stream()
        .anyMatch((ignoredPath) -> antPathMatcher.match(ignoredPath, path)))
      .flatMap((m) -> MatchResult.match()).switchIfEmpty(MatchResult.notMatch());
  }
}
