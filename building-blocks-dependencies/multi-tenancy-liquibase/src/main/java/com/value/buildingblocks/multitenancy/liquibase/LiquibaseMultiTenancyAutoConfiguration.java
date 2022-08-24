package com.value.buildingblocks.multitenancy.liquibase;

import com.value.buildingblocks.multitenancy.datasource.MultiTenancyDataSourceAutoConfiguration;
import javax.sql.DataSource;
import liquibase.change.DatabaseChange;
import liquibase.integration.spring.SpringLiquibase;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.AllNestedConditions;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseAutoConfiguration;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ConfigurationCondition;
import org.springframework.context.annotation.Import;

@Configuration
@Conditional({LiquibaseMultiTenancyAutoConfiguration.OnLiquibaseMultiTenancyEnabledCondition.class})
@AutoConfigureAfter({MultiTenancyDataSourceAutoConfiguration.class})
@AutoConfigureBefore({LiquibaseAutoConfiguration.class})
@Import({LiquibaseMultiTenancyBeanDefinitionRegistrar.class})
public class LiquibaseMultiTenancyAutoConfiguration {
  static class OnLiquibaseMultiTenancyEnabledCondition extends AllNestedConditions {
    OnLiquibaseMultiTenancyEnabledCondition() {
      super(ConfigurationCondition.ConfigurationPhase.REGISTER_BEAN);
    }
    
    @ConditionalOnClass({SpringLiquibase.class, DatabaseChange.class})
    static class HasLiquibaseDependency {}
    
    @ConditionalOnBean({DataSource.class})
    static class HasDataSourceBean {}
    
    @ConditionalOnProperty(name = {"value.multi-tenancy.enabled"}, havingValue = "true", matchIfMissing = false)
    static class OnMultiTenancyEnabled {}
    
    @ConditionalOnProperty(prefix = "spring.liquibase", name = {"enabled"}, matchIfMissing = true)
    static class OnLiquibaseEnabled {}
  }
}
