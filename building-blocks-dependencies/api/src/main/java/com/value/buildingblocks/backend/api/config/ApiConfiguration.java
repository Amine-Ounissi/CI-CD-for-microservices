package com.value.buildingblocks.backend.api.config;

import com.value.buildingblocks.backend.api.exceptionhandler.ApiExceptionHandler;
import com.value.buildingblocks.backend.api.exceptionhandler.BindingResultConverter;
import com.value.buildingblocks.backend.api.exceptionhandler.BindingResultErrorsExceptionHandler;
import com.value.buildingblocks.backend.api.exceptionhandler.ConstraintViolationConverter;
import com.value.buildingblocks.backend.api.exceptionhandler.ConstraintViolationExceptionHandler;
import com.value.buildingblocks.backend.api.exceptionhandler.ConversionFailedExceptionHandler;
import com.value.buildingblocks.backend.api.exceptionhandler.HttpMediaTypeNotAcceptableExceptionHandler;
import com.value.buildingblocks.backend.api.exceptionhandler.HttpMessageConversionExceptionHandler;
import com.value.buildingblocks.backend.api.exceptionhandler.MethodArgumentNotValidExceptionHandler;
import com.value.buildingblocks.backend.api.exceptionhandler.NonApiExceptionHandler;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableConfigurationProperties({ApiProperties.class})
public class ApiConfiguration {

  @ConditionalOnMissingBean(name = {"apiWebMvcConfigurer"})
  @Bean({"apiWebMvcConfigurer"})
  public WebMvcConfigurer apiWebMvcConfigurer(ApiProperties apiProperties) {
    return new ApiWebMvcConfigurer(apiProperties);
  }

  @ConditionalOnProperty(prefix = "value.api.errors", name = {
    "handle-api-exceptions"}, havingValue = "true", matchIfMissing = true)
  @Bean
  @ConditionalOnMissingBean
  public ApiExceptionHandler apiExceptionHandler() {
    return new ApiExceptionHandler();
  }

  @ConditionalOnProperty(prefix = "value.api.errors", name = {
    "handle-binding-result-errors-exception"}, havingValue = "true", matchIfMissing = true)
  @Bean
  @ConditionalOnMissingBean
  public BindingResultErrorsExceptionHandler bindingResultErrorsExceptionHandler() {
    return new BindingResultErrorsExceptionHandler();
  }

  @ConditionalOnProperty(prefix = "value.api.errors", name = {
    "handle-conversion-failed-exception"}, havingValue = "true", matchIfMissing = true)
  @Bean
  @ConditionalOnMissingBean
  public ConversionFailedExceptionHandler conversionFailedExceptionHandler() {
    return new ConversionFailedExceptionHandler();
  }

  @ConditionalOnProperty(prefix = "value.api.errors", name = {
    "handle-http-media-type-not-acceptable-exception"}, havingValue = "true", matchIfMissing = true)
  @Bean
  @ConditionalOnMissingBean
  public HttpMediaTypeNotAcceptableExceptionHandler httpMediaTypeNotAcceptableExceptionHandler() {
    return new HttpMediaTypeNotAcceptableExceptionHandler();
  }

  @ConditionalOnProperty(prefix = "value.api.errors", name = {
    "handle-http-message-conversion-exception"}, havingValue = "true", matchIfMissing = true)
  @Bean
  @ConditionalOnMissingBean
  public HttpMessageConversionExceptionHandler httpMessageConversionExceptionHandler(
    ApiProperties apiProperties) {
    return new HttpMessageConversionExceptionHandler(apiProperties);
  }

  @ConditionalOnProperty(prefix = "value.api.errors", name = {
    "handle-method-argument-not-valid-exception"}, havingValue = "true", matchIfMissing = true)
  @Bean
  @ConditionalOnMissingBean
  public MethodArgumentNotValidExceptionHandler methodArgumentNotValidExceptionHandler() {
    return new MethodArgumentNotValidExceptionHandler();
  }

  @ConditionalOnProperty(prefix = "value.api.errors", name = {
    "handle-constraint-violation-exception"}, havingValue = "true", matchIfMissing = true)
  @Bean
  @ConditionalOnMissingBean
  public ConstraintViolationExceptionHandler constraintViolationExceptionHandler(
    ConstraintViolationConverter constraintViolationConverter) {
    return new ConstraintViolationExceptionHandler(constraintViolationConverter);
  }

  @ConditionalOnProperty(prefix = "value.api.errors", name = {
    "handle-non-api-exceptions"}, havingValue = "true", matchIfMissing = true)
  @Bean
  @ConditionalOnMissingBean
  public NonApiExceptionHandler nonApiExceptionHandler() {
    return new NonApiExceptionHandler();
  }

  @Bean
  @ConditionalOnMissingBean(name = {"bindingResultConverter"})
  public BindingResultConverter bindingResultConverter() {
    return new BindingResultConverter();
  }

  @Bean
  @ConditionalOnMissingBean(name = {"constraintViolationConverter"})
  public ConstraintViolationConverter constraintViolationConverter(ApiProperties apiProperties) {
    return new ConstraintViolationConverter(apiProperties);
  }
}
