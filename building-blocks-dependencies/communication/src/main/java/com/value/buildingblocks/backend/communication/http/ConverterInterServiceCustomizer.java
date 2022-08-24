package com.value.buildingblocks.backend.communication.http;

import java.util.Collections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestTemplate;

public class ConverterInterServiceCustomizer implements InterServiceRestTemplateCustomizer {
  private static final Logger log = LoggerFactory.getLogger(ConverterInterServiceCustomizer.class);
  
  public void customize(RestTemplate restTemplate) {
    int jsonIndex = -1, xmlIndex = -1;
    for (int i = 0; i < restTemplate.getMessageConverters().size(); i++) {
      if (restTemplate.getMessageConverters().get(i) instanceof org.springframework.http.converter.xml.MappingJackson2XmlHttpMessageConverter) {
        xmlIndex = i;
      } else if (restTemplate.getMessageConverters().get(i) instanceof org.springframework.http.converter.json.MappingJackson2HttpMessageConverter) {
        jsonIndex = i;
      } 
    } 
    if (jsonIndex >= 0 && xmlIndex >= 0 && xmlIndex < jsonIndex) {
      Collections.swap(restTemplate.getMessageConverters(), xmlIndex, jsonIndex);
    } else {
      log.warn("Couldn't set MappingJackson2HttpMessageConverter as the first HttpMessageConverter");
    } 
  }
}
