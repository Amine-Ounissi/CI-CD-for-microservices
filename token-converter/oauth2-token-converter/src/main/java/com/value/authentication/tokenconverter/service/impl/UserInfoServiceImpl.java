package com.value.authentication.tokenconverter.service.impl;

import com.value.authentication.tokenconverter.exception.common.TokenConverterBaseException;
import com.value.authentication.tokenconverter.service.UserInfoService;
import java.net.URI;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
public class UserInfoServiceImpl implements UserInfoService {

  private static final Logger log = LoggerFactory.getLogger(UserInfoServiceImpl.class);
  private static final UserInfoServiceImpl.UserInfoServiceTypeReference TYPE_REFERENCE = new UserInfoServiceImpl.UserInfoServiceTypeReference();
  private final RestTemplate restTemplate;

  public UserInfoServiceImpl(@Qualifier("userInfoRestTemplate") RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  public Map verifyToken(String token, URI uri) {
    HttpEntity entity = new HttpEntity(this.getHeaders(token));
    try {
      return this.restTemplate.exchange(uri, HttpMethod.GET, entity, TYPE_REFERENCE)
        .getBody();
    } catch (TokenConverterBaseException var5) {
      log.debug("Token failed verification: {}", token);
      throw var5;
    }
  }

  private MultiValueMap<String, String> getHeaders(String token) {
    MultiValueMap<String, String> headers = new HttpHeaders();
    headers.add("Authorization", "Bearer ".concat(token));
    return headers;
  }

  private static class UserInfoServiceTypeReference extends
    ParameterizedTypeReference<Map<String, Object>> {

    private UserInfoServiceTypeReference() {
    }
  }
}
