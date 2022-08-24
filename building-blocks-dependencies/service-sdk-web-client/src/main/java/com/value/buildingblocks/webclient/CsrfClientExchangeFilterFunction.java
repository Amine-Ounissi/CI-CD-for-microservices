package com.value.buildingblocks.webclient;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import reactor.core.publisher.Mono;

public class CsrfClientExchangeFilterFunction implements ExchangeFilterFunction {

  public Mono<ClientResponse> filter(ClientRequest request, ExchangeFunction next) {
    return next.exchange(request).flatMap(response -> {
      if (response.statusCode() == HttpStatus.FORBIDDEN) {
        ResponseCookie csrfCookie = (ResponseCookie) response.cookies().getFirst("XSRF-TOKEN");
        if (csrfCookie != null) {
          ClientRequest retryRequest = ClientRequest.from(request).headers(httpHeaders -> {
          }).cookies(stringStringMultiValueMap -> {}).build();
          return next.exchange(retryRequest);
        }
      }
      return Mono.just(response);
    });
  }
}
