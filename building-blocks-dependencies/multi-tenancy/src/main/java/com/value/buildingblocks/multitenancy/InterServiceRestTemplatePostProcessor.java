package com.value.buildingblocks.multitenancy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.web.client.RestTemplate;

@Deprecated
public class InterServiceRestTemplatePostProcessor implements BeanPostProcessor {
  private static final Logger log = LoggerFactory.getLogger(InterServiceRestTemplatePostProcessor.class);
  
  protected final String expectedBeanName;
  
  protected final String tenantIdHttpHeader;
  
  public InterServiceRestTemplatePostProcessor(String tenantIdHttpHeader, String expectedBeanName) {
    this.tenantIdHttpHeader = tenantIdHttpHeader;
    this.expectedBeanName = expectedBeanName;
  }
  
  protected boolean beanMatches(Object bean, String beanName) {
    return (bean instanceof RestTemplate && this.expectedBeanName.equals(beanName));
  }
  
  public Object postProcessAfterInitialization(Object bean, String beanName) {
    if (beanMatches(bean, beanName)) {
      log.debug("Adding TenantHeaderRequestInterceptor to {} {}", bean, beanName);
      RestTemplate restTemplate = (RestTemplate)bean;
      restTemplate.getInterceptors().add(new TenantHeaderRequestInterceptor(this.tenantIdHttpHeader));
    } 
    return bean;
  }
  
  public Object postProcessBeforeInitialization(Object bean, String beanName) {
    return bean;
  }
}
