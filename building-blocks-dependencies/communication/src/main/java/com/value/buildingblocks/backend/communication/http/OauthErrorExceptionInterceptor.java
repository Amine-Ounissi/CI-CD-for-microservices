package com.value.buildingblocks.backend.communication.http;

import java.io.IOException;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.security.oauth2.client.http.OAuth2ErrorHandler;
import org.springframework.security.oauth2.client.resource.OAuth2ProtectedResourceDetails;
import org.springframework.security.oauth2.client.token.grant.client.ClientCredentialsResourceDetails;

public class OauthErrorExceptionInterceptor implements ClientHttpRequestInterceptor {
  private final ClientCredentialsResourceDetails resource;
  
  public OauthErrorExceptionInterceptor(ClientCredentialsResourceDetails resource) {
    this.resource = resource;
  }
  
  public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
    ClientHttpResponse response = execution.execute(request, body);
    boolean enabled = Boolean.parseBoolean(request
        .getHeaders().getFirst("X-Intercept-Errors"));
    if (enabled && HttpStatus.Series.CLIENT_ERROR.equals(response.getStatusCode().series())) {
      NoopResponseErrorHandler errorHandler = new NoopResponseErrorHandler(response);
      OAuth2ErrorHandler oAuth2ErrorHandler = new OAuth2ErrorHandler(errorHandler, (OAuth2ProtectedResourceDetails)this.resource);
      oAuth2ErrorHandler.handleError(response);
      return errorHandler.getResponse();
    } 
    return response;
  }
}
