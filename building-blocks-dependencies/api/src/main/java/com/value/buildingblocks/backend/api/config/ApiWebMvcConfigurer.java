package com.value.buildingblocks.backend.api.config;

import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Deprecated
public class ApiWebMvcConfigurer implements WebMvcConfigurer {

  private final ApiProperties apiProperties;

  public ApiWebMvcConfigurer(ApiProperties apiProperties) {
    this.apiProperties = apiProperties;
  }

  public void configurePathMatch(PathMatchConfigurer configurer) {
    configurer
      .setUseSuffixPatternMatch(this.apiProperties.isUseSuffixPatternMatch());
  }

  public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
    configurer.favorPathExtension(this.apiProperties.isUsePathExtensionsForContentNegotiation());
  }
}
