package com.value.buildingblocks.logging;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.CommonsRequestLoggingFilter;

@Configuration
@ConditionalOnClass({CommonsRequestLoggingFilter.class})
public class RequestLoggingAutoConfiguration {
  @Bean
  @ConditionalOnMissingBean({CommonsRequestLoggingFilter.class})
  @ConfigurationProperties(prefix = "value.http.request-logging-filter")
  public CommonsRequestLoggingFilter commonsRequestLoggingFilter() {
    CommonsRequestLoggingFilter loggingFilter = new CommonsRequestLoggingFilter();
    loggingFilter.setIncludeClientInfo(true);
    loggingFilter.setIncludeQueryString(true);
    loggingFilter.setIncludePayload(true);
    loggingFilter.setIncludeHeaders(true);
    return loggingFilter;
  }
}
