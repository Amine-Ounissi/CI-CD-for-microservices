package com.value.buildingblocks.backend.communication.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

public class ObjectMapperInterServiceCustomizer implements InterServiceRestTemplateCustomizer {
  private static final Logger log = LoggerFactory.getLogger(ObjectMapperInterServiceCustomizer.class);
  
  private Optional<ObjectMapper> objectMapper;
  
  public ObjectMapperInterServiceCustomizer(Optional<ObjectMapper> objectMapper) {
    this.objectMapper = objectMapper;
  }
  
  public void customize(RestTemplate restTemplate) {
    if (this.objectMapper.isPresent()) {
      for (HttpMessageConverter converter : restTemplate.getMessageConverters()) {
        if (converter instanceof MappingJackson2HttpMessageConverter)
          ((MappingJackson2HttpMessageConverter)converter).setObjectMapper(this.objectMapper.get()); 
      } 
    } else {
      log.warn("Couldn't find an \"ObjectMapper\" bean to use in \"interServiceRestTemplate\", a default instance will be used instead");
    } 
  }
}
