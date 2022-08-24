package com.value.buildingblocks.backend.communication.http;

import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.web.client.RestTemplate;

public class ServiceTokenInterServiceCustomizer implements InterServiceRestTemplateCustomizer {
  private String jwtServiceToken;
  
  public ServiceTokenInterServiceCustomizer() {
    this("eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJteS1zZXJ2aWNlIiwic2NvcGUiOlsiYXBpOnNlcnZpY2UiXSwiZXhwIjoyMTQ3NDgzNjQ3LCJpYXQiOjE0ODQ4MjAxOTZ9.G13i2kk5zKSJws2TXfmxBxefBywArcqWUj6jOgYaUcU");
  }
  
  public ServiceTokenInterServiceCustomizer(String jwtServiceToken) {
    this.jwtServiceToken = jwtServiceToken;
  }
  
  public void customize(RestTemplate restTemplate) {
    if (restTemplate instanceof OAuth2RestTemplate)
      ((OAuth2RestTemplate)restTemplate).getOAuth2ClientContext().setAccessToken((OAuth2AccessToken)new DefaultOAuth2AccessToken(this.jwtServiceToken)); 
  }
}
