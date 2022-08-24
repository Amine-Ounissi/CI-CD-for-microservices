package com.value.buildingblocks.cloudconfig;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource({"classpath:cloud-config-client.properties"})
public class CloudConfigClientConfig {}
