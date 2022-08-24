package com.value.authentication.tokenconverter;

import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jwt.proc.ConfigurableJWTProcessor;
import com.nimbusds.jwt.proc.DefaultJWTProcessor;
import com.value.authentication.tokenconverter.config.Oauth2TokenConverterConfig;
import com.value.authentication.tokenconverter.filter.ErrorHandlingFilter;
import com.value.authentication.tokenconverter.filter.TokenVerificationFilter;
import com.value.authentication.tokenconverter.handler.RestTemplateResponseErrorHandler;
import java.time.Duration;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@EnableConfigurationProperties({Oauth2TokenConverterConfig.class})
public class Oauth2TokenConverterServiceApplication extends SpringBootServletInitializer implements
  WebApplicationInitializer {

  public static void main(String[] args) {
    SpringApplication.run(
      Oauth2TokenConverterServiceApplication.class, args);
  }

  @Bean(name = {"filterRegistrationBeanVerifier"})
  protected FilterRegistrationBean authenticationFilter(
    TokenVerificationFilter tokenVerificationFilter) {
    FilterRegistrationBean registrationBean = new FilterRegistrationBean();
    registrationBean.setFilter(tokenVerificationFilter);
    registrationBean.addUrlPatterns("/convert");
    registrationBean.setOrder(2);
    return registrationBean;
  }

  @Bean(name = {"filterRegistrationBeanErrorHandler"})
  protected FilterRegistrationBean errorHandlingFilter(ErrorHandlingFilter errorHandlingFilter) {
    FilterRegistrationBean registrationBean = new FilterRegistrationBean();
    registrationBean.setFilter(errorHandlingFilter);
    registrationBean.addUrlPatterns(new String[]{"/convert"});
    registrationBean.setOrder(1);
    return registrationBean;
  }

  @Qualifier("userInfoRestTemplate")
  @Bean
  protected RestTemplate userInfoRestTemplate(RestTemplateBuilder restTemplateBuilder,
    RestTemplateResponseErrorHandler restTemplateResponseErrorHandler,
    Oauth2TokenConverterConfig oauth2TokenConverterConfig) {
    return restTemplateBuilder
      .setConnectTimeout(
        Duration.ofSeconds(oauth2TokenConverterConfig.getUserInfoConnectionTimeoutSeconds()))
      .setReadTimeout(Duration.ofSeconds(oauth2TokenConverterConfig.getUserInfoReadTimeoutSeconds()))
      .errorHandler((ResponseErrorHandler) restTemplateResponseErrorHandler)
      .build();
  }

  @Bean
  protected ConfigurableJWTProcessor<SecurityContext> configurableJwtProcessor() {
    return (ConfigurableJWTProcessor<SecurityContext>) new DefaultJWTProcessor();
  }
}

