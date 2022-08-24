package com.value.buildingblocks.multitenancy.datasource.hibernate;

import javax.sql.DataSource;
import org.hibernate.engine.jdbc.connections.spi.MultiTenantConnectionProvider;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnClass(name = {"org.hibernate.jpa.HibernateEntityManager"})
@ConditionalOnProperty(name = {"value.multi-tenancy.enabled"}, havingValue = "true", matchIfMissing = false)
@ConditionalOnBean({DataSource.class})
@AutoConfigureAfter(name = {"org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration"})
@AutoConfigureBefore(name = {"org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration"})
public class MultiTenancyHibernateAutoConfiguration {
  @Bean
  @ConditionalOnMissingBean
  public MultiTenantConnectionProvider multiTenancyConnectionProvider(DataSource dataSource) {
    return (MultiTenantConnectionProvider)new MultiTenancyConnectionProvider(dataSource);
  }
  
  @Deprecated
  public EntityManagerBeanPostProcessor entityManagerBeanPostProcessor(MultiTenantConnectionProvider multiTenancyConnectionProvider) {
    return new EntityManagerBeanPostProcessor(multiTenancyConnectionProvider);
  }
  
  @Bean
  public HibernateMultiTenanacyConfiguration hibernateMultiTenanacyConfiguration(MultiTenantConnectionProvider multiTenancyConnectionProvider) {
    return new HibernateMultiTenanacyConfiguration(multiTenancyConnectionProvider);
  }
}
