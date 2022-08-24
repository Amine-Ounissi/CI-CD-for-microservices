package com.value.platform.edge.csrf;

import java.util.Optional;
import java.util.regex.Pattern;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

public class MobileRequestMatcher implements ServerWebExchangeMatcher {
  private static final String USER_AGENT = "user-agent";
  
  private Pattern mobilePattern = Pattern.compile("(.*CxpMobile.*)");
  
  public MobileRequestMatcher(GatewayCsrfProperties csrfProperties) {
    String mobileUserAgentPattern = csrfProperties.getMobileUserAgent();
    if (mobileUserAgentPattern != null)
      this.mobilePattern = Pattern.compile(mobileUserAgentPattern); 
  }
  
  public Mono<MatchResult> matches(ServerWebExchange serverWebExchange) {
    Optional<String> userAgent = Optional.ofNullable(serverWebExchange.getRequest().getHeaders().getFirst(USER_AGENT));
    if (userAgent.isPresent() && this.mobilePattern.matcher(userAgent.get()).matches())
      return MatchResult.match();
    return MatchResult.notMatch();
  }
}
