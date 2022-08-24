package com.value.buildingblocks.backend.communication.http;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.support.HttpRequestWrapper;
import org.springframework.test.web.client.MockMvcClientHttpRequestFactory;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

@TestConfiguration
@Order(-2147483648)
public class TestRestTemplateConfiguration {
  private static final Logger log = LoggerFactory.getLogger(TestRestTemplateConfiguration.class);
  
  public static final String TEST_JWT_SIG_KEY = "JWTSecretKeyDontUseInProduction!";
  
  public static final String TEST_SERVICE_TOKEN_VALUE = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJteS1zZXJ2aWNlIiwic2NvcGUiOlsiYXBpOnNlcnZpY2UiXSwiZXhwIjoyMTQ3NDgzNjQ3LCJpYXQiOjE0ODQ4MjAxOTZ9.G13i2kk5zKSJws2TXfmxBxefBywArcqWUj6jOgYaUcU";
  
  public static final String TEST_SERVICE_TOKEN = "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJteS1zZXJ2aWNlIiwic2NvcGUiOlsiYXBpOnNlcnZpY2UiXSwiZXhwIjoyMTQ3NDgzNjQ3LCJpYXQiOjE0ODQ4MjAxOTZ9.G13i2kk5zKSJws2TXfmxBxefBywArcqWUj6jOgYaUcU";
  
  @Bean({"interServiceRestTemplate"})
  public RestTemplate interServiceRestTemplate(@Qualifier("interServiceClientHttpRequestInterceptor") List<ClientHttpRequestInterceptor> interServiceClientHttpRequestInterceptors, List<InterServiceRestTemplateCustomizer> interServiceRestTemplateCustomizers) {
    RestTemplate restTemplate = new RestTemplate();
    restTemplate.getInterceptors().addAll(interServiceClientHttpRequestInterceptors);
    for (InterServiceRestTemplateCustomizer customizer : interServiceRestTemplateCustomizers)
      customizer.customize(restTemplate); 
    return restTemplate;
  }
  
  @Bean
  public MockTestRestTemplateCustomizer mockTestRestTemplateCustomizer(MockMvc mvc) {
    return new MockTestRestTemplateCustomizer(mvc);
  }
  
  public static class MockTestRestTemplateCustomizer implements InterServiceRestTemplateCustomizer {
    private MockMvc mvc;
    
    public MockTestRestTemplateCustomizer(MockMvc mvc) {
      this.mvc = mvc;
    }
    
    public void customize(RestTemplate restTemplate) {
      restTemplate.getInterceptors().add(0, new TestServiceTokenProvider());
      restTemplate.getInterceptors().add(0, new QueryParameterPlusToPercentEncodingCoercer());
      restTemplate.setRequestFactory((ClientHttpRequestFactory)new MockMvcClientHttpRequestFactory(this.mvc));
    }
  }
  
  public static class TestServiceTokenProvider implements ClientHttpRequestInterceptor {
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
      request.getHeaders()
        .putIfAbsent("Authorization", Collections.singletonList("Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJteS1zZXJ2aWNlIiwic2NvcGUiOlsiYXBpOnNlcnZpY2UiXSwiZXhwIjoyMTQ3NDgzNjQ3LCJpYXQiOjE0ODQ4MjAxOTZ9.G13i2kk5zKSJws2TXfmxBxefBywArcqWUj6jOgYaUcU"));
      return execution.execute(request, body);
    }
  }
  
  public static class QueryParameterPlusToPercentEncodingCoercer implements ClientHttpRequestInterceptor {
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
      HttpRequestWrapper httpRequestWrapper = new HttpRequestWrapper(request);
      URI originalUri = request.getURI();
      String originalRawQuery = originalUri.getRawQuery();
      if (!StringUtils.isEmpty(originalRawQuery)) {
        String query = '?' + originalRawQuery.replace("+", "%20");
        String fragment = originalUri.getFragment();
        if (fragment == null) {
          fragment = "";
        } else {
          fragment = '#' + fragment;
        } 
        String uriStr = originalUri.toString();
        uriStr = uriStr.substring(0, uriStr.length() - 1 + originalRawQuery.length() + fragment.length()) + query + fragment;
        try {
          final URI uri = new URI(uriStr);
          httpRequestWrapper = new HttpRequestWrapper(request) {
              public URI getURI() {
                return uri;
              }
            };
        } catch (URISyntaxException ex) {
          TestRestTemplateConfiguration.log.warn("Failed to replace + with %20 in query string of URI {}!  Original URI value will continue to be used.", originalUri, ex);
        } 
      } 
      return execution.execute((HttpRequest)httpRequestWrapper, body);
    }
  }
}
