package com.value.buildingblocks.webclient;

import org.springframework.web.reactive.function.client.WebClient;

@FunctionalInterface
public interface InterServiceWebClientCustomizer {
  void customize(WebClient.Builder paramBuilder);
}
