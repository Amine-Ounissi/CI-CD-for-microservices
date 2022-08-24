package com.value.platform.edge.filters;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ClientHttpConnector;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

@Configuration
@EnableConfigurationProperties
public class WebClientConfiguration {

  private static final Logger log = LoggerFactory.getLogger(WebClientConfiguration.class);

  @Bean
  @LoadBalanced
  @ConditionalOnProperty(value = {
    "gateway.token.converter.webclient.loadBalanced"}, matchIfMissing = true)
  WebClient.Builder loadBalancedWebClient(HttpClient httpClient) {
    log.debug("Load balancing enabled for webclient");
    return webClientBuilder(httpClient);
  }

  @Bean
  @ConditionalOnProperty(value = {
    "gateway.token.converter.webclient.loadBalanced"}, havingValue = "false")
  WebClient.Builder webClient(HttpClient httpClient) {
    log.debug("Load balancing disabled for webclient");
    return webClientBuilder(httpClient);
  }

  private WebClient.Builder webClientBuilder(HttpClient httpClient) {
    return WebClient.builder()
      .clientConnector(new ReactorClientHttpConnector(httpClient));
  }
}
