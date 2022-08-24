package com.value.buildingblocks.kubernetes;

import com.value.buildingblocks.eureka.EurekaConfig;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.loadbalancer.config.LoadBalancerCacheAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@AutoConfigureBefore({LoadBalancerCacheAutoConfiguration.class})
@AutoConfigureAfter({EurekaConfig.class})
@PropertySource({"classpath:service-sdk-starter-kubernetes-client.properties"})
@ConditionalOnProperty(value = {"spring.cloud.kubernetes.enabled"}, havingValue = "true", matchIfMissing = false)
public class KubernetesConfig {}
