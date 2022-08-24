package com.value.buildingblocks.security.csrf;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
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
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.util.ReflectionUtils;

@Configuration
@ConditionalOnProperty(prefix = "buildingblocks.security.csrf", name = {"autoconfig"}, havingValue = "true", matchIfMissing = true)
public class AutoCsrfWebSecurityConfiguration implements BeanPostProcessor, BeanFactoryAware {
  private static final Logger logger = LoggerFactory.getLogger(AutoCsrfWebSecurityConfiguration.class);
  
  private List<Object> annotatedBeans;
  
  private BeanFactory beanFactory;
  
  public void setBeanFactory(BeanFactory beanFactory) {
    this.beanFactory = beanFactory;
  }
  
  public Object postProcessBeforeInitialization(Object bean, String beanName) {
    if (bean instanceof WebSecurityConfigurerAdapter) {
      collectAnnotatedBeans();
      if (isAutoConfigDisabled())
        return bean; 
      return createProxyBean(bean);
    } 
    return bean;
  }
  
  public Object postProcessAfterInitialization(Object bean, String beanName) {
    return bean;
  }
  
  private boolean isAutoConfigDisabled() {
    return !this.annotatedBeans.isEmpty();
  }
  
  private void collectAnnotatedBeans() {
    if (this.annotatedBeans != null)
      return; 
    if (!(this.beanFactory instanceof ListableBeanFactory))
      return; 
    this.annotatedBeans = new ArrayList();
    ListableBeanFactory listableBeanFactory = (ListableBeanFactory)this.beanFactory;
    String[] annotatedBeansTmp = listableBeanFactory.getBeanNamesForAnnotation(DisableAutoCsrfWebSecurityConfiguration.class);
    for (String beanName : annotatedBeansTmp)
      this.annotatedBeans.add(this.beanFactory.getBean(beanName)); 
    if (!this.annotatedBeans.isEmpty())
      logger.debug("Auto configuration for CSRF is disabled by annotation {} in {}", DisableAutoCsrfWebSecurityConfiguration.class
          .getName(), this.annotatedBeans); 
  }
  
  private Object createProxyBean(Object bean) {
    ProxyFactory factory = new ProxyFactory();
    factory.setTarget(bean);
    factory.addAdvice((Advice)new CsrfWebSecurityConfigurerAdapter());
    return factory.getProxy();
  }
  
  private static class CsrfWebSecurityConfigurerAdapter implements MethodInterceptor {
    private CsrfWebSecurityConfigurerAdapter() {}
    
    public Object invoke(MethodInvocation invocation) throws Throwable {
      if ("init".equals(invocation.getMethod().getName())) {
        Method method = ReflectionUtils.findMethod(WebSecurityConfigurerAdapter.class, "getHttp");
        assert method != null;
        ReflectionUtils.makeAccessible(method);
        try {
          HttpSecurity http = (HttpSecurity)ReflectionUtils.invokeMethod(method, getTarget(invocation.getThis()));
          assert http != null;
          http.apply(CsrfWebSecurityConfigurer.configuration());
          AutoCsrfWebSecurityConfiguration.logger.debug("Automatically loaded CSRF configuration");
        } catch (Exception e) {
          String msg = "Can't auto-enable CSRF configuration! CSRF cannot be automatically enabled due to an internal error, Please enable it manually.";
          AutoCsrfWebSecurityConfiguration.logger.warn(msg, e);
        } 
      } 
      return invocation.proceed();
    }
    
    private Object getTarget(Object object) throws Exception {
      if (AopUtils.isAopProxy(object))
        return getTarget(((Advised)object).getTargetSource().getTarget()); 
      return object;
    }
  }
}
