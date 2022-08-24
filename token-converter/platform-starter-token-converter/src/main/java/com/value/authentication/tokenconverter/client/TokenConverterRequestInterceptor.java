package com.value.authentication.tokenconverter.client;

import com.value.buildingblocks.multitenancy.Tenant;
import com.value.buildingblocks.multitenancy.TenantContext;
import java.io.IOException;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

public class TokenConverterRequestInterceptor implements ClientHttpRequestInterceptor {
  private static final Logger log = LoggerFactory.getLogger(TokenConverterRequestInterceptor.class);
  
  private final String tenantIdHttpHeader;
  
  public TokenConverterRequestInterceptor(String tenantIdHttpHeader) {
    this.tenantIdHttpHeader = tenantIdHttpHeader;
  }

  @Override
  public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
    HttpHeaders headers = request.getHeaders();
    Optional<Tenant> tenant = TenantContext.getTenant();
    headers.remove(this.tenantIdHttpHeader);
    headers.remove("X-CXT-User-Token");
    if (tenant.isPresent()) {
      log.debug("Setting header {} to {}", this.tenantIdHttpHeader, tenant);
      headers.add(this.tenantIdHttpHeader, ((Tenant)tenant.get()).getId());
    } else {
      log.warn("Cannot set header {} because TenantContext is empty.", this.tenantIdHttpHeader);
    } 
    return execution.execute(request, body);
  }
}
