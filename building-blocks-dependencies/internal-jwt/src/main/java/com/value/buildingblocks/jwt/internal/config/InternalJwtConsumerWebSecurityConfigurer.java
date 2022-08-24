package com.value.buildingblocks.jwt.internal.config;

import com.value.buildingblocks.jwt.internal.InternalJwtConsumerConfiguration;
import com.value.buildingblocks.jwt.internal.filter.InternalJwtConsumerFilter;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.Filter;
import org.springframework.beans.factory.annotation.AnnotatedGenericBeanDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;

public final class InternalJwtConsumerWebSecurityConfigurer extends
  SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {

  @Autowired
  private InternalJwtConsumerFilter internalJwtConsumerFilter;

  public static InternalJwtConsumerWebSecurityConfigurer internalJwtConsumer() {
    return new InternalJwtConsumerWebSecurityConfigurer();
  }

  public void init(HttpSecurity http) throws Exception {
    autowireThisClass(http);
    http.addFilterBefore((Filter) this.internalJwtConsumerFilter,
      AbstractPreAuthenticatedProcessingFilter.class);
  }

  private void autowireThisClass(HttpSecurity http) {
    ApplicationContext applicationContext = (ApplicationContext) http
      .getSharedObject(ApplicationContext.class);
    registerRequiredBeans(applicationContext, InternalJwtConsumerConfiguration.class,
      InternalJwtConsumerFilter.class);
    applicationContext.getAutowireCapableBeanFactory().autowireBean(this);
  }

  private void registerRequiredBeans(ApplicationContext parentApplicationContext,
    Class<?>... requiredBeanClasses) {
    AnnotationConfigApplicationContext childApplicationContext = new AnnotationConfigApplicationContext();
    childApplicationContext.setParent(parentApplicationContext);
    childApplicationContext.register(requiredBeanClasses);
    childApplicationContext.refresh();
    Map<String, Object> beansMissingInParent = getBeansMissingInParent(parentApplicationContext,
      childApplicationContext);
    ConfigurableListableBeanFactory beanFactory = ((ConfigurableApplicationContext) parentApplicationContext)
      .getBeanFactory();
    beansMissingInParent.entrySet()
      .forEach(entry -> beanFactory.registerSingleton((String) entry.getKey(), entry.getValue()));
    for (String beanName : childApplicationContext.getBeanDefinitionNames()) {
      if (beansMissingInParent.containsKey(beanName)) {
        childApplicationContext.removeBeanDefinition(beanName);
      }
    }
  }

  private Map<String, Object> getBeansMissingInParent(ApplicationContext parentApplicationContext,
    AnnotationConfigApplicationContext childApplicationContext) {
    Map<String, Object> missingBeans = new HashMap<>();
    String[] beanDefinitionNames = childApplicationContext.getBeanDefinitionNames();
    for (String beanName : beanDefinitionNames) {
      Object requiredBean = childApplicationContext.getBean(beanName);
      Class<?> requiredBeanClass = requiredBean.getClass();
      if (!isBeanAnnotated(childApplicationContext.getBeanDefinition(beanName),
        Configuration.class)) {
        if (!beanOfTypeExists(parentApplicationContext, requiredBeanClass)) {
          missingBeans.put(beanName, requiredBean);
        }
      }
    }
    return missingBeans;
  }

  private boolean isBeanAnnotated(BeanDefinition definition, Class annotation) {
    if (definition instanceof AnnotatedGenericBeanDefinition) {
      return ((AnnotatedGenericBeanDefinition) definition).getMetadata()
        .isAnnotated(annotation.getName());
    }
    return false;
  }

  private boolean beanOfTypeExists(ApplicationContext applicationContext, Class<?> beanType) {
    try {
      Map beansOfType = applicationContext.getBeansOfType(beanType, true, false);
      if (beansOfType.isEmpty()) {
        return false;
      }
      return true;
    } catch (Exception e) {
      return false;
    }
  }
}
