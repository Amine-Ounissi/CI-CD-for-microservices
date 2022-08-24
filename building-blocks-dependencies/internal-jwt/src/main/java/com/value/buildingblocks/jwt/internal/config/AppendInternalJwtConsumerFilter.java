package com.value.buildingblocks.jwt.internal.config;

import com.value.buildingblocks.jwt.internal.InternalJwtConsumer;
import com.value.buildingblocks.jwt.internal.InternalJwtConsumerProperties;
import com.value.buildingblocks.jwt.internal.filter.InternalJwtConsumerFilter;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;
import javax.servlet.Filter;
import org.aopalliance.aop.Advice;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.Advised;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;
import org.springframework.util.ReflectionUtils;

@Configuration
public class AppendInternalJwtConsumerFilter implements BeanPostProcessor, BeanFactoryAware {
  private static final Logger logger = LoggerFactory.getLogger(AppendInternalJwtConsumerFilter.class);
  
  private Set<String> annotatedBeanNames;
  
  private BeanFactory beanFactory;
  
  public void setBeanFactory(BeanFactory beanFactory) {
    this.beanFactory = beanFactory;
  }
  
  public Object postProcessBeforeInitialization(Object bean, String beanName) {
    if (bean instanceof WebSecurityConfigurerAdapter) {
      collectAnnotatedBeanNames();
      if (this.annotatedBeanNames.isEmpty() || this.annotatedBeanNames.contains(beanName)) {
        logger.debug("creating proxy bean for bean[{}]", beanName);
        return createProxyBean(bean);
      } 
    } 
    return bean;
  }
  
  public Object postProcessAfterInitialization(Object bean, String beanName) {
    return bean;
  }
  
  private void collectAnnotatedBeanNames() {
    if (this.annotatedBeanNames != null)
      return; 
    if (!(this.beanFactory instanceof ListableBeanFactory))
      return; 
    this.annotatedBeanNames = new HashSet<>();
    ListableBeanFactory factory = (ListableBeanFactory)this.beanFactory;
    String[] annotatedBeans = factory.getBeanNamesForAnnotation(EnableInternalJwtConsumer.class);
    for (String beanName : annotatedBeans) {
      Class<?> beanType = factory.getType(beanName);
      if (beanType != null && WebSecurityConfigurerAdapter.class.isAssignableFrom(beanType))
        this.annotatedBeanNames.add(beanName); 
    } 
  }
  
  private Object createProxyBean(Object bean) {
    ProxyFactory factory = new ProxyFactory();
    factory.addAdvice((Advice)new InternalJwtConsumerSecurityAdapter(this.beanFactory));
    factory.setTarget(bean);
    return factory.getProxy();
  }
  
  private static class InternalJwtConsumerSecurityAdapter implements MethodInterceptor {
    private final BeanFactory beanFactory;
    
    InternalJwtConsumerSecurityAdapter(BeanFactory beanFactory) {
      this.beanFactory = beanFactory;
    }
    
    public Object invoke(MethodInvocation methodInvocation) throws Throwable {
      if ("init".equals(methodInvocation.getMethod().getName())) {
        Method method = ReflectionUtils.findMethod(WebSecurityConfigurerAdapter.class, "getHttp");
        if (method != null) {
          ReflectionUtils.makeAccessible(method);
          HttpSecurity http = (HttpSecurity)ReflectionUtils.invokeMethod(method, 
              getTarget(methodInvocation.getThis()));
          if (http != null)
            configure(http, methodInvocation.getThis().getClass()); 
        } 
      } 
      return methodInvocation.proceed();
    }
    
    private Object getTarget(Object object) throws Exception {
      if (AopUtils.isAopProxy(object))
        return getTarget(((Advised)object).getTargetSource().getTarget()); 
      return object;
    }
    
    private void configure(HttpSecurity http, Class<?> onClass) {
      InternalJwtConsumer internalJwtConsumer = (InternalJwtConsumer)this.beanFactory.getBean(InternalJwtConsumer.class);
      InternalJwtConsumerProperties internalJwtConsumerProperties = (InternalJwtConsumerProperties)this.beanFactory.getBean(InternalJwtConsumerProperties.class);
      InternalJwtConsumerFilter internalJwtConsumerFilter = new InternalJwtConsumerFilter(internalJwtConsumer, internalJwtConsumerProperties);
      AppendInternalJwtConsumerFilter.logger.debug("Adding {} before {} to HttpSecurity on {}", new Object[] { internalJwtConsumerFilter
            .getClass().getSimpleName(), AbstractPreAuthenticatedProcessingFilter.class
            .getSimpleName(), onClass.getSimpleName() });
      http.addFilterBefore((Filter)internalJwtConsumerFilter, AbstractPreAuthenticatedProcessingFilter.class);
    }
  }
}
