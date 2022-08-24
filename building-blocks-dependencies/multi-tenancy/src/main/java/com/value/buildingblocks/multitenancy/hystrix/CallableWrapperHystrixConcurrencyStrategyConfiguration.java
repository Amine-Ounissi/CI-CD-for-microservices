package com.value.buildingblocks.multitenancy.hystrix;

import com.netflix.hystrix.Hystrix;
import com.value.buildingblocks.multitenancy.Tenant;
import com.value.buildingblocks.multitenancy.TenantContext;
import com.value.buildingblocks.resilience.CallableWrapperSupplier;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(name = {
  "value.multi-tenancy.enabled"}, havingValue = "true", matchIfMissing = false)
@AutoConfigureBefore(name = {"com.value.buildingblocks.resilience.ResilienceAutoConfiguration"})
@ConditionalOnClass({Hystrix.class, CallableWrapperSupplier.class})
public class CallableWrapperHystrixConcurrencyStrategyConfiguration {

  @Bean
  @ConditionalOnMissingBean(name = {"tenantContextCallableWrapperSupplier"})
  public CallableWrapperSupplier<Tenant> tenantContextCallableWrapperSupplier() {
    return new CallableWrapperSupplier(() -> (Tenant) TenantContext.getTenant().orElse(null),
      tenant -> TenantContext.setTenant((Tenant) tenant), TenantContext::clear);
  }
}
