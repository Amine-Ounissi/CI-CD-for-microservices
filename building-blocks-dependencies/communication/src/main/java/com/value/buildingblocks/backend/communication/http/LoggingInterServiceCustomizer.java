package com.value.buildingblocks.backend.communication.http;

import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

public class LoggingInterServiceCustomizer implements InterServiceRestTemplateCustomizer {
  private ClientHttpRequestFactory interServiceRequestFactory;
  
  public LoggingInterServiceCustomizer(ClientHttpRequestFactory interServiceRequestFactory) {
    this.interServiceRequestFactory = interServiceRequestFactory;
  }
  
  public void customize(RestTemplate restTemplate) {
    restTemplate.setRequestFactory((ClientHttpRequestFactory)new BufferingClientHttpRequestFactory(this.interServiceRequestFactory));
  }
}
