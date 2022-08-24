package com.value.platform.edge.filters;

import java.util.Map;
import java.util.Objects;
import java.util.StringJoiner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpCookie;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

@Component
public class RemoveRequestCookie extends AbstractGatewayFilterFactory {
  private static final String HEADER_COOKIE = "Cookie";
  
  private static final Logger log = LoggerFactory.getLogger(RemoveRequestCookie.class);
  
  private RemoveRequestCookieProperties properties;
  
  public RemoveRequestCookie(RemoveRequestCookieProperties properties) {
    this.properties = properties;
  }
  
  public GatewayFilter apply(Object config) {
    return (exchange, chain) -> {
        Map<String, HttpCookie> cookiesMap = exchange.getRequest().getCookies().toSingleValueMap();
        if (cookiesMap.isEmpty())
          return chain.filter(exchange); 
        StringJoiner joiner = new StringJoiner("; ");
        for (Map.Entry<String, HttpCookie> entry : cookiesMap.entrySet()) {
          if (this.properties.getCookies().contains(entry.getKey()))
            continue; 
          joiner.add(cookieAsString(entry.getKey(), entry.getValue()));
        } 
        log.debug("Final cookie passed => {}", joiner);
        ServerHttpRequest.Builder modifiedRequestBuilder = exchange.getRequest().mutate();
        modifiedRequestBuilder.header(HEADER_COOKIE, joiner.toString());
        return chain.filter(exchange.mutate().request(modifiedRequestBuilder.build()).build());
      };
  }
  
  private String cookieAsString(String cookieName, HttpCookie cookie) {
    return cookieName + "=" + cookie.getValue();
  }
}
