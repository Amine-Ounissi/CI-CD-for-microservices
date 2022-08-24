package com.value.buildingblocks.jwt.internal.config;

import com.value.buildingblocks.jwt.internal.InternalJwtConsumerConfiguration;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Configuration
@Import({InternalJwtConsumerConfiguration.class, AppendInternalJwtConsumerFilter.class})
public @interface EnableInternalJwtConsumer {}
