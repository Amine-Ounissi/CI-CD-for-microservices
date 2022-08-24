package com.value.buildingblocks.backend.communication.context;

import java.util.concurrent.Executor;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.task.TaskExecutionAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;

@Configuration
@AutoConfigureBefore({TaskExecutionAutoConfiguration.class})
class CommunicationContextConfiguration {
  @Bean
  public OriginatorContextUtil originatorContextUtil() {
    return new OriginatorContextUtil();
  }
  
  @Bean(name = {"applicationTaskExecutor", "taskExecutor"})
  @ConditionalOnMissingBean({Executor.class})
  public SimpleAsyncTaskExecutor applicationTaskExecutor() {
    return new SimpleAsyncTaskExecutor();
  }
}
