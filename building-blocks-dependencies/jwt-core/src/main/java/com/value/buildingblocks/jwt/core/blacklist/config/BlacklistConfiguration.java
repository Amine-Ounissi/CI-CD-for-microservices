package com.value.buildingblocks.jwt.core.blacklist.config;

import com.value.buildingblocks.jwt.core.blacklist.TokenBlacklistService;
import com.value.buildingblocks.jwt.core.blacklist.impl.DefaultTokenBlacklistService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BlacklistConfiguration {
  @Bean
  @ConditionalOnProperty(name = {"sso.jwt.blacklist.enabled"}, havingValue = "true")
  @ConditionalOnMissingBean({TokenBlacklistService.class})
  public TokenBlacklistService tokenBlacklistService() {
    return new DefaultTokenBlacklistService();
  }
}
