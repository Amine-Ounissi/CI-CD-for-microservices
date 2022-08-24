package com.value.buildingblocks.backend.security.auth.config;

import com.value.buildingblocks.jwt.internal.config.EnableInternalJwtConsumer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableInternalJwtConsumer
@ConditionalOnProperty(value = {"value.security.internal-jwt-consumer.enabled"}, havingValue = "true", matchIfMissing = true)
class InternalJwtConsumerAutoConfiguration {}
