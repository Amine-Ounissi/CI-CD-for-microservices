package com.value.authentication.tokenconverter;

import com.value.authentication.tokenconverter.cache.TokenConverterCacheManager;
import com.value.authentication.tokenconverter.cache.TokenConverterTenantProvider;
import com.value.authentication.tokenconverter.client.DbsUserClient;
import com.value.authentication.tokenconverter.client.TokenConverterRequestInterceptor;
import com.value.authentication.tokenconverter.client.impl.DbsUserClientImpl;
import com.value.authentication.tokenconverter.configuration.TokenConverterProperties;
import com.value.authentication.tokenconverter.crypto.JsonWebAlgorithm;
import com.value.authentication.tokenconverter.crypto.SignatureService;
import com.value.authentication.tokenconverter.enhancer.InternalTokenEnhancer;
import com.value.authentication.tokenconverter.enhancer.ValidationEnhancer;
import com.value.authentication.tokenconverter.exception.TokenConverterConfigurationException;
import com.value.authentication.tokenconverter.internaltoken.InternalTokenHandler;
import com.value.authentication.tokenconverter.mapper.ClaimsStore;
import com.value.authentication.tokenconverter.mapper.MapBasedClaimsStore;
import com.value.authentication.tokenconverter.mapper.TokenClaimMapper;
import com.value.authentication.tokenconverter.service.api.listener.client.v2.users.ServiceApiUsersClient;
import com.value.authentication.tokenconverter.server.AuthServer;
import com.value.authentication.tokenconverter.server.AuthServerTokenEnhancer;
import com.value.authentication.tokenconverter.usercontext.UserContextHandler;
import com.value.authentication.tokenconverter.utils.TokenHelper;
import com.value.authentication.tokenconverter.validationenhancers.ClaimValidationValidator;
import com.value.authentication.tokenconverter.verifyonly.VerifyOnlyService;
import com.value.authentication.tokenconverter.verifyonly.impl.VerifyOnlyServiceImpl;
import com.value.buildingblocks.multitenancy.TenantProvider;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.jwt.JwtAlgorithms;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.annotation.RequestScope;

@Configuration
@EnableCaching
@EnableConfigurationProperties({TokenConverterProperties.class})
@Import({AuthServer.class})
public class TokenConverterAutoConfiguration {

  private static final Logger log = LoggerFactory.getLogger(TokenConverterAutoConfiguration.class);

  @Value("${value.multi-tenancy.tenant-id-http-header:X-TID}")
  public String tenantIdHeaderName;

  static {
    try {
      Field field = ReflectionUtils.findField(JwtAlgorithms.class, "javaToSigAlgs");
      if (field == null) {
        String message = "Couldn't extract supported signing algorithms via reflection. Irrecoverable error.";
        log.error(message);
        throw new TokenConverterConfigurationException(message);
      }
      field.setAccessible(true);
      for (JsonWebAlgorithm jwa : JsonWebAlgorithm.values()) {
        ((Map<String, String>) field.get(Map.class)).put(jwa.getJcaAlg(), jwa.name());
      }
    } catch (IllegalAccessException e) {
      throw new TokenConverterConfigurationException(e);
    }
  }

  @Bean
  public InternalTokenHandler internalTokenHandler(TokenHelper helper,
    List<InternalTokenEnhancer> enhancerList, List<ValidationEnhancer> validationEnhancerList,
    ClaimsStore claimsStore) {
    return new InternalTokenHandler(helper, enhancerList, validationEnhancerList, claimsStore);
  }

  @Bean
  public TokenClaimMapper tokenClaimMapper(TokenConverterProperties tokenConverterProperties,
    ClaimsStore claimsSupplier) {
    return new TokenClaimMapper(tokenConverterProperties, claimsSupplier);
  }

  @Bean
  @RequestScope
  @ConditionalOnMissingBean({ClaimsStore.class})
  public ClaimsStore externalClaimsSupplier() {
    return new MapBasedClaimsStore();
  }

  @Bean
  public UserContextHandler userContextHandler() {
    return new UserContextHandler();
  }

  @Bean
  public AuthServerTokenEnhancer authServerTokenEnhancer(
    TokenConverterProperties tokenConverterProperties) {
    return new AuthServerTokenEnhancer(tokenConverterProperties);
  }

  @Bean
  public SignatureService signatureService(TokenConverterProperties tokenConverterProperties) {
    return new SignatureService(tokenConverterProperties);
  }

  @Bean
  public TokenHelper tokenHelper(TokenConverterProperties tokenConverterProperties) {
    return new TokenHelper(tokenConverterProperties);
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return PasswordEncoderFactories.createDelegatingPasswordEncoder();
  }

  @Bean
  public DbsUserClient dbsUserClient(ServiceApiUsersClient client) {
    return new DbsUserClientImpl(client);
  }

  @Bean
  @Primary
  public TenantProvider tenantProvider() {
    return new TokenConverterTenantProvider();
  }

  @Bean
  @Primary
  public CacheManager tokenConverterCacheManager(
    TokenConverterProperties tokenConverterProperties) {
    return new TokenConverterCacheManager(tokenConverterProperties);
  }

  @Bean
  public ClientHttpRequestInterceptor tenantIdInterceptor(
    @Qualifier("interServiceRestTemplate") RestTemplate restTemplate) {
    TokenConverterRequestInterceptor tenantHeaderRequestInterceptor = new TokenConverterRequestInterceptor(
      this.tenantIdHeaderName);
    restTemplate.getInterceptors().add(tenantHeaderRequestInterceptor);
    return tenantHeaderRequestInterceptor;
  }

  @Bean
  @ConditionalOnProperty(value = {
    "value.token-converter.claim-value-validation.enabled"}, havingValue = "true")
  public ClaimValidationValidator claimValidationValidator(TokenConverterProperties properties) {
    return new ClaimValidationValidator(properties);
  }

  @Bean
  public VerifyOnlyService verifyOnlyService(TokenConverterProperties properties) {
    return new VerifyOnlyServiceImpl(properties);
  }
}
