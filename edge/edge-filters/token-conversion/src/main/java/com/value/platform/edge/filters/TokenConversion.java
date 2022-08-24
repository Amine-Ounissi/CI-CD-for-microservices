package com.value.platform.edge.filters;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient.Builder;
import org.springframework.web.reactive.function.client.WebClient.RequestBodySpec;
import org.springframework.web.reactive.function.client.WebClient.ResponseSpec;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class TokenConversion extends AbstractGatewayFilterFactory {

  private static final Logger log = LoggerFactory.getLogger(TokenConversion.class);
  private final AntPathMatcher antPathMatcher = new AntPathMatcher();
  private final Builder client;
  private final TokenConversionProperties properties;

  public TokenConversion(Builder client, TokenConversionProperties properties) {
    this.properties = properties;
    this.client = client;
  }

  public GatewayFilter apply(Object config) {
    return (exchange, chain) -> {
      ServerHttpRequest request = exchange.getRequest();
      if (this.skipTokenConversion(request, this.properties)) {
        log.debug("Token conversion skipped");
        return chain.filter(exchange);
      } else {
        MultiValueMap<String, String> headersForTokenConversion = this
          .getHeadersTokenConverterRequest(request);
        ResponseSpec tokenConverterResponse = this.callTokenConverter(headersForTokenConversion);
        return tokenConverterResponse.onStatus(HttpStatus::isError,
          (clientResponse) -> this.processTokenConverterError(exchange, request, clientResponse))
          .toEntity(String.class)
          .flatMap((r) -> this.processTokenConverterResponse(exchange, chain, request, r));
      }
    };
  }

  private Mono<Void> processTokenConverterResponse(ServerWebExchange exchange,
    GatewayFilterChain chain, ServerHttpRequest request,
    ResponseEntity<String> tokenConverterResponse) {
    log.error("Token conversion response success");
    this.copyDownstreamHeaders(tokenConverterResponse.getHeaders(), request, this.properties);
    return chain.filter(exchange).then(Mono.fromRunnable(() -> {
      if (!tokenConverterResponse.getStatusCode().isError()) {
        this.copyUpstreamHeaders(tokenConverterResponse.getHeaders(), exchange.getResponse(),
          this.properties);
      }
    }));
  }

  private Mono<? extends Throwable> processTokenConverterError(ServerWebExchange exchange,
    ServerHttpRequest request, ClientResponse tokenConverterResponse) {
    if (tokenConverterResponse.statusCode().is5xxServerError() && this.properties.getStrict()
      .isEnabled()) {
      int errorCode = this.properties.getStrict().getInaccessibleStatus();
      String errorMessage = "Connecting with external token converter service was not possible";
      log.error("{} with errorCode {}", errorMessage, errorCode);
      Mono<ResponseEntity<Void>> errorMono = tokenConverterResponse.toBodilessEntity()
        .doOnNext((it) -> log.error("Token Converter service error code {} {}", errorCode, it));
      return errorMono.then(Mono.error(new ResponseStatusException(HttpStatus.valueOf(errorCode),
        "Connecting with external token converter service was not possible")));
    } else {
      this.cleanDownstreamHeaders(request, this.properties);
      return this.handleTokenConversionError(exchange, tokenConverterResponse);
    }
  }

  private MultiValueMap<String, String> getHeadersTokenConverterRequest(ServerHttpRequest request) {
    MultiValueMap<String, String> headersForTokenConversion = this
      .omitHeaders(request, this.properties.getOmitHeaders());
    this.properties.getHeaders().forEach(headersForTokenConversion::add);
    if (this.properties.getStrict().isEnabled() && !headersForTokenConversion
      .containsKey(this.properties.getAuthorizationHeader())) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,
        HttpStatus.UNAUTHORIZED.toString());
    }
    if (this.properties.getDownstreamRequestHeader().isEnabled()) {
      headersForTokenConversion
        .add(this.properties.getDownstreamRequestHeader().getName(), request.getURI().toString());
    }

    return headersForTokenConversion;
  }

  private ResponseSpec callTokenConverter(MultiValueMap<String, String> headersForTokenConversion) {
    RequestBodySpec request = this.client.build()
      .method(this.properties.getMethod()).uri(this.properties.getUrl(), new Object[0])
      .headers((hs) -> hs.addAll(headersForTokenConversion));
    log.debug(headersForTokenConversion.toString());
    return request.retrieve();
  }

  private Mono<? extends Throwable> handleTokenConversionError(ServerWebExchange exchange,
    final ClientResponse tokenConverterResponse) {
    this.logResponseError(tokenConverterResponse.statusCode());
    if (this.properties.isCopyUpstreamOnFailure()) {
      this.copyUpstreamHeaders(tokenConverterResponse.headers().asHttpHeaders(),
        exchange.getResponse(), this.properties);
    }

    return this.handleErrorForStrictMode(tokenConverterResponse);
  }

  private Mono<? extends Throwable> handleErrorForStrictMode(
    ClientResponse tokenConverterResponse) {
    if (this.properties.getStrict().isEnabled()) {
      int errorCode = tokenConverterResponse.rawStatusCode();
      String errorMessage = "Received invalid response from token converter";
      log.debug(errorMessage);
      Mono<ResponseEntity<Void>> errorMono = tokenConverterResponse.toBodilessEntity()
        .doOnNext(
          (it) -> log.error("Received invalid response from token converter {} {}", errorCode, it));
      return errorMono.then(Mono.error(new ResponseStatusException(HttpStatus.valueOf(errorCode),
        errorMessage)));
    } else {
      return Mono.empty();
    }
  }

  private void logResponseError(HttpStatus tokenConverterResponse) {
    if (tokenConverterResponse.is4xxClientError()) {
      log.info("Client error {}, from external token converter service",
        tokenConverterResponse.value());
    } else if (tokenConverterResponse.is5xxServerError()) {
      log.warn("Server error from external token converter service. Check it's logs");
    }

  }

  boolean skipTokenConversion(ServerHttpRequest request, TokenConversionProperties properties) {
    String path = request.getPath().pathWithinApplication().value();
    return Optional.ofNullable(properties.getIgnore()).orElse(new ArrayList<>()).stream()
      .anyMatch((pattern) -> this.antPathMatcher.match(pattern, path));
  }

  private MultiValueMap<String, String> omitHeaders(ServerHttpRequest request,
    Set<String> omitHeaders) {
    MultiValueMap<String, String> headers = new HttpHeaders();
    HttpHeaders requestHeaders = request.getHeaders();
    requestHeaders.forEach((key, values) -> {
      if (omitHeaders.stream().noneMatch((h) -> h.equalsIgnoreCase(key))) {
        values.forEach((value) -> headers.add(key, value));
      }
    });
    return headers;
  }

  private void copyDownstreamHeaders(final HttpHeaders responseHeaders,
    final ServerHttpRequest inputRequest, final TokenConversionProperties properties) {
    org.springframework.http.server.reactive.ServerHttpRequest.Builder modifiedRequestBuilder = inputRequest
      .mutate();
    for (String responseHeader : properties.getDownstreamHeaders()) {
      this.copyHeaderToDownstreamRequest(responseHeaders, modifiedRequestBuilder, responseHeader);
    }
  }

  private void cleanDownstreamHeaders(final ServerHttpRequest inputRequest,
    final TokenConversionProperties properties) {
    org.springframework.http.server.reactive.ServerHttpRequest.Builder modifiedRequestBuilder = inputRequest
      .mutate();
    for (String responseHeader : properties.getDownstreamHeaders()) {
      modifiedRequestBuilder.headers((httpHeaders) -> httpHeaders.remove(responseHeader));
    }

  }

  private void copyHeaderToDownstreamRequest(HttpHeaders responseHeaders,
    org.springframework.http.server.reactive.ServerHttpRequest.Builder modifiedRequestBuilder,
    String responseHeader) {
    if (!responseHeaders.containsKey(responseHeader)) {
      modifiedRequestBuilder.headers((httpHeaders) -> httpHeaders.remove(responseHeader));
    } else {
      List<String> result = responseHeaders.get(responseHeader);
      assert result != null;
      modifiedRequestBuilder
        .header(responseHeader, result.toArray(new String[0]));
    }
  }

  private void copyUpstreamHeaders(HttpHeaders tokenConverterResponseHeaders,
    ServerHttpResponse gatewayResponse, TokenConversionProperties properties) {
    String prefix = properties.getUpstreamHeaderPrefix();
    for (String expectedHeader : properties.getUpstreamHeaders()) {
      this
        .copyExpectedHeaderToGatewayResponse(tokenConverterResponseHeaders, gatewayResponse, prefix,
          expectedHeader);
    }

  }

  private void copyExpectedHeaderToGatewayResponse(HttpHeaders tokenConverterResponseHeaders,
    ServerHttpResponse gatewayResponse, String prefix, String expectedHeader) {
    if (tokenConverterResponseHeaders.containsKey(expectedHeader)) {
      String headerFinalName = this.getHeaderFinalName(prefix, expectedHeader);
      HttpHeaders gatewayResponseHeaders = gatewayResponse.getHeaders();
      Objects.requireNonNull(tokenConverterResponseHeaders.get(expectedHeader))
        .forEach((expectedHeaderValue) -> gatewayResponseHeaders
          .add(headerFinalName, expectedHeaderValue));
    }
  }

  private String getHeaderFinalName(String prefix, String expectedHeader) {
    return expectedHeader.startsWith(prefix) ? expectedHeader.substring(prefix.length())
      : expectedHeader;
  }
}
