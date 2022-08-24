package com.value.buildingblocks.eureka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.netflix.eureka.EurekaInstanceConfigBean;
import org.springframework.cloud.netflix.eureka.metadata.DefaultManagementMetadataProvider;
import org.springframework.cloud.netflix.eureka.metadata.ManagementMetadata;
import org.springframework.context.ApplicationContext;
import org.springframework.util.StringUtils;
import org.springframework.web.context.WebApplicationContext;

public class ContextPathAwareManagementMetadataProvider extends DefaultManagementMetadataProvider {
  private static final Logger log = LoggerFactory.getLogger(ContextPathAwareManagementMetadataProvider.class);
  
  private static final String METADATA_CONTEXT_PATH_PROPERTY_KEY = "eureka.instance.metadata-map.contextPath";
  
  private final ApplicationContext applicationContext;
  
  public ContextPathAwareManagementMetadataProvider(ApplicationContext applicationContext) {
    this.applicationContext = applicationContext;
  }
  
  public ManagementMetadata get(EurekaInstanceConfigBean instance, int serverPort, String serverContextPath, String managementContextPath, Integer managementPort) {
    String actualContextPath;
    if (this.applicationContext instanceof WebApplicationContext) {
      actualContextPath = ((WebApplicationContext)this.applicationContext).getServletContext().getContextPath();
    } else {
      String basePath = this.applicationContext.getEnvironment().getProperty("spring.webflux.base-path");
      if (StringUtils.hasText(basePath)) {
        actualContextPath = basePath;
      } else {
        actualContextPath = serverContextPath;
      } 
    } 
    if ("/".equals(actualContextPath))
      actualContextPath = ""; 
    if (!actualContextPath.equals(serverContextPath)) {
      log.info("Replacing configured context path of {} with actual context path of {} for management endpoints registered with eureka", serverContextPath, actualContextPath);
      serverContextPath = actualContextPath;
    } 
    String configuredMetadataContextPath = this.applicationContext.getEnvironment().getProperty("eureka.instance.metadata-map.contextPath");
    if (configuredMetadataContextPath != null && !actualContextPath.equals(configuredMetadataContextPath))
      log.warn("Value of property {} ({}) does not match the actual context path in use ({}).  Other services may not be able to contact this service properly.", new Object[] { "eureka.instance.metadata-map.contextPath", configuredMetadataContextPath, actualContextPath }); 
    instance.getMetadataMap().put("contextPath", actualContextPath);
    return super.get(instance, serverPort, serverContextPath, managementContextPath, managementPort);
  }
}
