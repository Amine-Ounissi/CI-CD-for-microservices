package com.value.buildingblocks.eureka;

import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cloud.netflix.eureka.EurekaClientAutoConfiguration;
import org.springframework.cloud.netflix.eureka.metadata.ManagementMetadataProvider;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@AutoConfigureBefore({EurekaClientAutoConfiguration.class})
public class ManagementMetadataProviderAutoConfiguration {
  @Bean
  @ConditionalOnMissingBean({ManagementMetadataProvider.class})
  public ManagementMetadataProvider contextPathAwareManagementMetadataProvider(ApplicationContext applicationContext) {
    return (ManagementMetadataProvider)new ContextPathAwareManagementMetadataProvider(applicationContext);
  }
}
