package com.value.buildingblocks.backend.communication.http;

import java.util.Optional;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class TestOAuthTokenCustomizerConfiguration {
  @Bean
  public ServiceTokenInterServiceCustomizer interServiceTokenCustomizer(Optional<String> testJwtServiceToken) {
    return new ServiceTokenInterServiceCustomizer(testJwtServiceToken.orElse("eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJteS1zZXJ2aWNlIiwic2NvcGUiOlsiYXBpOnNlcnZpY2UiXSwiZXhwIjoyMTQ3NDgzNjQ3LCJpYXQiOjE0ODQ4MjAxOTZ9.G13i2kk5zKSJws2TXfmxBxefBywArcqWUj6jOgYaUcU"));
  }
}
