package com.value.authentication.tokenconverter.handler;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.value.authentication.tokenconverter.exception.NotFoundException;
import com.value.authentication.tokenconverter.exception.ServerErrorException;
import com.value.authentication.tokenconverter.exception.UserinfoException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResponseErrorHandler;

@Component
public class RestTemplateResponseErrorHandler implements ResponseErrorHandler {

  private static final Logger log = LoggerFactory.getLogger(RestTemplateResponseErrorHandler.class);

  private ObjectMapper mapper = new ObjectMapper();

  public boolean hasError(ClientHttpResponse httpResponse) throws IOException {
    return (httpResponse.getStatusCode().series() == HttpStatus.Series.CLIENT_ERROR || httpResponse
      .getStatusCode().series() == HttpStatus.Series.SERVER_ERROR);
  }

  public void handleError(ClientHttpResponse response) throws IOException {
    if (response.getStatusCode().series() == HttpStatus.Series.SERVER_ERROR) {
      throw new ServerErrorException();
    }
    if (response.getStatusCode() == HttpStatus.NOT_FOUND) {
      throw new NotFoundException();
    }
    if (response.getStatusCode().series() == HttpStatus.Series.CLIENT_ERROR) {
      Map<String, String> errorMap = getErrorMap(response);
      throw new UserinfoException((String) errorMap.get("error"),
        (String) errorMap.get("error_description"));
    }
    log.debug("Unexpected error occurred :: {}", response.getStatusText());
  }

  private Map<String, String> getErrorMap(ClientHttpResponse response) throws IOException {
    Object object = this;
    try (BufferedReader br = new BufferedReader(new InputStreamReader(response
      .getBody(), Charset.defaultCharset()))) {
      return (Map) this.mapper
        .readValue(br.lines().collect(Collectors.joining("\n")), (TypeReference) object);
    }
  }
}
