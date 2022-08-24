package com.value.buildingblocks.webclient;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.util.StdDateFormat;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.text.DateFormat;
import java.util.TimeZone;
import java.util.function.Consumer;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.security.oauth2.client.OAuth2ClientProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.reactive.ReactiveLoadBalancer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ClientCodecConfigurer;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@EnableConfigurationProperties({OAuth2ClientProperties.class})
@AutoConfigureAfter(name = {
  "org.springframework.cloud.loadbalancer.config.LoadBalancerAutoConfiguration",
  "org.springframework.boot.autoconfigure.web.reactive.function.client.WebClientAutoConfiguration",
  "org.springframework.cloud.client.loadbalancer.reactive.LoadBalancerBeanPostProcessorAutoConfiguration"})
@ConditionalOnBean({WebClient.Builder.class})
public class InterServiceWebClientConfiguration {

  @Bean
  @ConditionalOnMissingBean
  public ObjectMapper objectMapper(DateFormat dateFormat) {
    ObjectMapper mapper = new ObjectMapper();
    mapper.setDateFormat(dateFormat);
    mapper.registerModule((Module) new JavaTimeModule());
    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    return mapper;
  }

  @Bean
  @ConditionalOnMissingBean
  public DateFormat dateFormat() {
    StdDateFormat stdDateFormat = new StdDateFormat();
    stdDateFormat.setTimeZone(TimeZone.getDefault());
    return (DateFormat) stdDateFormat;
  }

  @Bean({"interServiceWebClient"})
  @ConditionalOnMissingBean(name = {"interServiceWebClient"})
  public WebClient interServiceWebClient(
    ObjectProvider<InterServiceWebClientCustomizer> interServiceWebClientCustomizers,
    WebClient.Builder builder) {
    builder.defaultHeader("Content-Type", new String[]{MediaType.APPLICATION_JSON.toString()});
    builder.defaultHeader("Accept", new String[]{MediaType.APPLICATION_JSON.toString()});
    interServiceWebClientCustomizers.orderedStream()
      .forEach(customizer -> customizer.customize(builder));
    return builder.build();
  }

  @Bean
  @Order(50)
  @ConditionalOnBean({ReactiveLoadBalancer.Factory.class})
  InterServiceWebClientCustomizer loadBalancingCustomizer(
    ReactiveLoadBalancer.Factory<ServiceInstance> loadBalancerFactory) {
    return builder -> builder.filter(
      (ExchangeFilterFunction) new ContextPathAwareReactorLoadBalancerExchangeFilterFunction(
        loadBalancerFactory));
  }

  @Bean
  @Order(40)
  InterServiceWebClientCustomizer csrfClientExchangeFilterFunctionWebClientCustomizer() {
    return builder -> builder
      .filter((ExchangeFilterFunction) new CsrfClientExchangeFilterFunction());
  }

  @Bean
  @Order(20)
  InterServiceWebClientCustomizer exchangeStrategiesInterServiceWebClientCustomizer(
    ObjectMapper objectMapper) {
    return webClientBuilder -> webClientBuilder
      .codecs(clientCodecConfigurer -> {});
  }
}
