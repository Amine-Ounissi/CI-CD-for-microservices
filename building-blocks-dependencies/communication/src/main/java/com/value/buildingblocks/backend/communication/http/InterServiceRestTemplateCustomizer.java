package com.value.buildingblocks.backend.communication.http;

import org.springframework.web.client.RestTemplate;

@FunctionalInterface
public interface InterServiceRestTemplateCustomizer {
  void customize(RestTemplate paramRestTemplate);
}
