package com.value.buildingblocks.backend.communication.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.value.buildingblocks.backend.internalrequest.InternalRequestContext;
import com.value.buildingblocks.presentation.errors.BadRequestException;
import com.value.buildingblocks.presentation.errors.ForbiddenException;
import com.value.buildingblocks.presentation.errors.InternalServerErrorException;
import com.value.buildingblocks.presentation.errors.NotAcceptableException;
import com.value.buildingblocks.presentation.errors.NotFoundException;
import com.value.buildingblocks.presentation.errors.UnauthorizedException;
import com.value.buildingblocks.presentation.errors.UnsupportedMediaTypeException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import javax.net.ssl.HostnameVerifier;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.client.loadbalancer.LoadBalancerAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.security.oauth2.client.DefaultOAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.token.AccessTokenProvider;
import org.springframework.security.oauth2.client.token.AccessTokenProviderChain;
import org.springframework.security.oauth2.client.token.DefaultAccessTokenRequest;
import org.springframework.security.oauth2.client.token.grant.client.ClientCredentialsAccessTokenProvider;
import org.springframework.security.oauth2.client.token.grant.client.ClientCredentialsResourceDetails;
import org.springframework.security.oauth2.common.AuthenticationScheme;
import org.springframework.web.client.RestTemplate;

@Configuration
@AutoConfigureBefore({LoadBalancerAutoConfiguration.class})
@AutoConfigureAfter(name = {
  "com.value.buildingblocks.backend.internalrequest.InternalRequestContextConfiguration"})
@EnableConfigurationProperties({HttpCommunicationProperties.class})
public class HttpCommunicationConfiguration {

  public static final String INTER_SERVICE_CLIENT_HTTP_REQUEST_INTERCEPTOR_QUALIFIER = "interServiceClientHttpRequestInterceptor";

  private static final Logger log = LoggerFactory.getLogger(HttpCommunicationConfiguration.class);

  public static final String INTER_SERVICE_REST_TEMPLATE_BEAN_NAME = "interServiceRestTemplate";

  public static final String INTERCEPTORS_ENABLED_HEADER = "X-Intercept-Errors";

  @Autowired(required = false)
  private List<AccessTokenProvider> accessTokenProviders;

  @Autowired
  private HttpCommunicationProperties properties;

  public HttpCommunicationProperties getProperties() {
    return this.properties;
  }

  public void setProperties(HttpCommunicationProperties properties) {
    this.properties = properties;
  }

  @Bean
  public ClientCredentialsResourceDetails clientCredentialsResourceDetails(
    @Value("${value.communication.http.client-secret:vds-secret}") String clientSecret) {
    ClientCredentialsResourceDetails resource = new ClientCredentialsResourceDetails();
    resource.setClientAuthenticationScheme(this.properties.getClientAuthenticationScheme());
    resource.setAccessTokenUri(this.properties.getAccessTokenUri());
    resource.setClientId(this.properties.getClientId());
    resource.setClientSecret(clientSecret);
    resource.setAuthenticationScheme(AuthenticationScheme.header);
    resource.setScope(this.properties.getClientScope());
    return resource;
  }

  @LoadBalanced
  @Bean({"interServiceRestTemplate"})
  @ConditionalOnMissingBean(name = {"interServiceRestTemplate"})
  public RestTemplate interServiceRestTemplate(
    ClientCredentialsResourceDetails clientCredentialsResourceDetails,
    @Qualifier("interServiceRequestFactory") ClientHttpRequestFactory interServiceRequestFactory,
    @Qualifier("interServiceClientHttpRequestInterceptor") List<ClientHttpRequestInterceptor> interServiceClientHttpRequestInterceptors,
    List<InterServiceRestTemplateCustomizer> interServiceRestTemplateCustomizers) {
    OAuth2RestTemplate template = new OAuth2RestTemplate(clientCredentialsResourceDetails,
      new DefaultOAuth2ClientContext(new DefaultAccessTokenRequest()));
    if (this.accessTokenProviders != null) {
      template
        .setAccessTokenProvider((this.accessTokenProviders.size() == 1) ? this.accessTokenProviders
          .get(0) : new AccessTokenProviderChain(this.accessTokenProviders));
    }
    template.setRequestFactory(interServiceRequestFactory);
    template.getInterceptors().addAll(interServiceClientHttpRequestInterceptors);
    for (InterServiceRestTemplateCustomizer customizer : interServiceRestTemplateCustomizers) {
      customizer.customize(template);
    }
    return template;
  }

  @ConditionalOnProperty(value = {
    "value.communication.http.discoverable-access-token-service"}, havingValue = "true", matchIfMissing = true)
  @Bean
  public DisoverableClientCredentialsAccessTokenProvider interServiceRestTemplateAccessTokenProvider() {
    return new DisoverableClientCredentialsAccessTokenProvider();
  }

  @ConditionalOnBean({DisoverableClientCredentialsAccessTokenProvider.class})
  @LoadBalanced
  @Bean({"accessProviderRestTemplate"})
  public RestTemplate accessProviderRestTemplate(
    DisoverableClientCredentialsAccessTokenProvider disoverableClientCredentialsAccessTokenProvider) {
    return disoverableClientCredentialsAccessTokenProvider.getRestTemplate();
  }

  @Bean
  @ConditionalOnMissingBean(name = {"interServiceRequestFactory"})
  public ClientHttpRequestFactory interServiceRequestFactory(HttpCommunicationProperties config) {
    HttpCommunicationProperties.InterServiceHttpClientProperties clientConfig = config.getClient();
    HttpClientBuilder clientBuilder = HttpClients.custom().disableAuthCaching()
      .disableConnectionState().disableCookieManagement();
    if (clientConfig.isUseSystemProperties()) {
      clientBuilder.useSystemProperties();
    }
    if (!config.isVerifyCertificateHostnames()) {
      log
        .info("SSL certificate hostname verification for inter-service communication is disabled.");
      clientBuilder.setSSLHostnameVerifier((HostnameVerifier) new NoopHostnameVerifier());
    }
    clientBuilder.setDefaultRequestConfig(config.getRequestConfig().build());
    if (!StringUtils.isEmpty(clientConfig.getUserAgent())) {
      clientBuilder.setUserAgent(clientConfig.getUserAgent());
    }
    if (clientConfig.isDefaultUserAgentDisabled()) {
      clientBuilder.disableDefaultUserAgent();
    }
    clientBuilder.setMaxConnPerRoute(clientConfig.getMaxConnPerRoute());
    clientBuilder.setMaxConnTotal(clientConfig.getMaxConnTotal());
    clientBuilder.setConnectionTimeToLive(clientConfig.getConnTimeToLive(), TimeUnit.MILLISECONDS);
    if (clientConfig.isEvictIdleConnections()) {
      clientBuilder.evictIdleConnections(clientConfig.getMaxIdleTime(), TimeUnit.MILLISECONDS);
    }
    if (clientConfig.isEvictExpiredConnections()) {
      clientBuilder.evictExpiredConnections();
    }
    if (clientConfig.isRedirectHandlingDisabled()) {
      clientBuilder.disableRedirectHandling();
    }
    if (clientConfig.isContentCompressionDisabled()) {
      clientBuilder.disableContentCompression();
    }
    if (clientConfig.isAutomaticRetriesDisabled()) {
      clientBuilder.disableAutomaticRetries();
    }
    return (ClientHttpRequestFactory) new HttpComponentsClientHttpRequestFactory(
      (HttpClient) clientBuilder.build());
  }

  @Bean
  @Order(10)
  @Qualifier("interServiceClientHttpRequestInterceptor")
  ApiErrorExceptionInterceptor badRequestErrorInterceptor(
    List<HttpMessageConverter<?>> messageConverters) {
    return new ApiErrorExceptionInterceptor(HttpStatus.BAD_REQUEST,
      (Class) BadRequestException.class, messageConverters);
  }

  @Bean
  @Order(20)
  @Qualifier("interServiceClientHttpRequestInterceptor")
  ApiErrorExceptionInterceptor notFoundErrorInterceptor(
    List<HttpMessageConverter<?>> messageConverters) {
    return new ApiErrorExceptionInterceptor(HttpStatus.NOT_FOUND, (Class) NotFoundException.class,
      messageConverters);
  }

  @Bean
  @Order(30)
  @Qualifier("interServiceClientHttpRequestInterceptor")
  ApiErrorExceptionInterceptor notAcceptableErrorInterceptor(
    List<HttpMessageConverter<?>> messageConverters) {
    return new ApiErrorExceptionInterceptor(HttpStatus.NOT_ACCEPTABLE,
      (Class) NotAcceptableException.class, messageConverters);
  }

  @Bean
  @Order(40)
  @Qualifier("interServiceClientHttpRequestInterceptor")
  ApiErrorExceptionInterceptor unsupportedMediaTypeErrorInterceptor(
    List<HttpMessageConverter<?>> messageConverters) {
    return new ApiErrorExceptionInterceptor(HttpStatus.UNSUPPORTED_MEDIA_TYPE,
      (Class) UnsupportedMediaTypeException.class, messageConverters);
  }

  @Bean
  @Order(50)
  @Qualifier("interServiceClientHttpRequestInterceptor")
  ApiErrorExceptionInterceptor internalServerErrorInterceptor(
    List<HttpMessageConverter<?>> messageConverters) {
    return new ApiErrorExceptionInterceptor(HttpStatus.INTERNAL_SERVER_ERROR,
      (Class) InternalServerErrorException.class, messageConverters);
  }

  @Bean
  @Order(60)
  @Qualifier("interServiceClientHttpRequestInterceptor")
  ApiErrorExceptionInterceptor forbiddenErrorInterceptor(
    List<HttpMessageConverter<?>> messageConverters) {
    return new ApiErrorExceptionInterceptor(HttpStatus.FORBIDDEN, (Class) ForbiddenException.class,
      messageConverters);
  }

  @Bean
  @Order(70)
  @Qualifier("interServiceClientHttpRequestInterceptor")
  ApiErrorExceptionInterceptor unauthorizedErrorInterceptor(
    List<HttpMessageConverter<?>> messageConverters) {
    return new ApiErrorExceptionInterceptor(HttpStatus.UNAUTHORIZED,
      (Class) UnauthorizedException.class, messageConverters);
  }

  @Bean
  @Order(90)
  @Qualifier("interServiceClientHttpRequestInterceptor")
  OauthErrorExceptionInterceptor oauthRequestErrorInterceptor(
    ClientCredentialsResourceDetails resource) {
    return new OauthErrorExceptionInterceptor(resource);
  }

  @Bean
  @Order(100)
  @Qualifier("interServiceClientHttpRequestInterceptor")
  ClientHttpRequestInterceptor interceptorsEnabledHeaderInterceptor() {
    return new InterceptorsEnabledHeaderInterceptor();
  }

  @Bean
  @Qualifier("interServiceClientHttpRequestInterceptor")
  @ConditionalOnBean({InternalRequestContext.class})
  @Order(100)
  public InternalRequestContextInterceptor internalRequestContextInterceptor(
    InternalRequestContext internalRequestContext) {
    return new InternalRequestContextInterceptor(internalRequestContext);
  }

  @Bean
  @Qualifier("interServiceClientHttpRequestInterceptor")
  @ConditionalOnProperty(value = {
    "value.communication.http.client.propagate-headers"}, matchIfMissing = true)
  @Order(200)
  @ConditionalOnMissingBean({CopyHeaderInterceptor.class})
  public CopyHeaderInterceptor copyHeaderInterceptor() {
    return new CopyHeaderInterceptor(this.properties.getClient().getBlacklistedHeaders());
  }

  @Bean
  @Qualifier("interServiceClientHttpRequestInterceptor")
  @Order(500)
  @ConfigurationProperties(prefix = "value.communication.http.inter-service-logging")
  public InterServiceLoggingInterceptor interServiceLoggingInterceptor() {
    return new InterServiceLoggingInterceptor();
  }

  @Bean
  @Order(10)
  @ConditionalOnProperty({"value.communication.http.autowire-object-mapper"})
  public ObjectMapperInterServiceCustomizer interServiceObjectMapperCustomizer(
    Optional<ObjectMapper> objectMapper) {
    return new ObjectMapperInterServiceCustomizer(objectMapper);
  }

  @Bean
  @Order(20)
  public ConverterInterServiceCustomizer converterInterServiceCustomizer(
    Optional<ObjectMapper> objectMapper) {
    return new ConverterInterServiceCustomizer();
  }

  @Bean
  @Order(100)
  public LoggingInterServiceCustomizer interServiceLoggingCustomizer(
    @Qualifier("interServiceRequestFactory") ClientHttpRequestFactory interServiceRequestFactory) {
    return new LoggingInterServiceCustomizer(interServiceRequestFactory);
  }

  class DisoverableClientCredentialsAccessTokenProvider extends
    ClientCredentialsAccessTokenProvider {

    public RestTemplate getRestTemplate() {
      return (RestTemplate) super.getRestTemplate();
    }
  }
}
