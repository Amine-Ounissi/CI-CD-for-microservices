package com.value.authentication.tokenconverter.service.api.listener.client.v2.users;

import com.value.buildingblocks.backend.internalrequest.InternalRequestContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import javax.validation.constraints.Pattern;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@AutoConfigureAfter(name = {"com.value.buildingblocks.backend.communication.http.HttpCommunicationConfiguration", "com.value.buildingblocks.backend.security.auth.config.SecurityContextUtilConfiguration"})
@Configuration("com.value.authentication.tokenconverter.service.api.listener.client.v2.users.ServiceApiUsersClientHttpImplAutoConfiguration")
@ConfigurationProperties("value.communication.services.service.api")
public class ServiceApiUsersClientHttpImplAutoConfiguration {
  private String serviceId = "user-manager";
  
  private String baseUri = "/internal/v2";
  
  @Pattern(regexp = "https?")
  private String scheme;
  
  public String getScheme() {
    return this.scheme;
  }
  
  public void setScheme(String scheme) {
    this.scheme = scheme;
  }
  
  public String getServiceId() {
    return this.serviceId;
  }
  
  public void setServiceId(String serviceId) {
    this.serviceId = serviceId;
  }
  
  public String getBaseUri() {
    return this.baseUri;
  }
  
  public void setBaseUri(String baseUri) {
    this.baseUri = baseUri;
  }
  
  @ConditionalOnMissingBean
  @Bean({"com.value.authentication.tokenconverter.service.api.listener.client.v2.users.ServiceApiUsersClient"})
  @ConditionalOnBean(name = {"interServiceRestTemplate"})
  public ServiceApiUsersClient createClient(@Value("${value.communication.http.default-scheme:http}") String defaultScheme, @Qualifier("interServiceRestTemplate") RestTemplate restTemplate, ObjectMapper objectMapper, InternalRequestContext internalRequestContext) {
    ServiceApiUsersClientHttpImpl clientInstance = new ServiceApiUsersClientHttpImpl(restTemplate, this.serviceId, (this.scheme == null) ? defaultScheme : this.scheme, objectMapper, internalRequestContext);
    clientInstance.setBaseUri(this.baseUri);
    return clientInstance;
  }
}
