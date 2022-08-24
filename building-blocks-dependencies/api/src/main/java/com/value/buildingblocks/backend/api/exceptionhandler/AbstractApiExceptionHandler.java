package com.value.buildingblocks.backend.api.exceptionhandler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.value.buildingblocks.backend.api.config.ApiProperties;
import com.value.buildingblocks.presentation.errors.InternalServerErrorException;
import javax.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

public abstract class AbstractApiExceptionHandler {

  private static final Logger log = LoggerFactory.getLogger(AbstractApiExceptionHandler.class);

  private ObjectMapper mapper = new ObjectMapper();

  @Autowired
  private ApiProperties properties;

  @PostConstruct
  public void init() {
    this.mapper.disable(
      MapperFeature.AUTO_DETECT_CREATORS, MapperFeature.AUTO_DETECT_FIELDS,
      MapperFeature.AUTO_DETECT_GETTERS, MapperFeature.AUTO_DETECT_IS_GETTERS);
  }

  protected ResponseEntity<String> createApiExceptionResponse(Exception apiException,
    HttpStatus statusValue) {
    InternalServerErrorException internalServerErrorException = new InternalServerErrorException(
      apiException.getMessage());
    try {
      if (this.properties.isOverride5xxErrorMessages() && statusValue.is5xxServerError()) {
        log.info("Replacing throwable message [{}] with generic message [{}]",
          apiException.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR
            .getReasonPhrase());
        internalServerErrorException = new InternalServerErrorException(
          HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
      }
      String body = this.mapper.writeValueAsString(internalServerErrorException);
      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_JSON);
      return new ResponseEntity<>(body, headers, statusValue);
    } catch (JsonProcessingException e) {
      logException(internalServerErrorException, HttpStatus.INTERNAL_SERVER_ERROR);
      return internalServerErrorResponse(e);
    }
  }

  protected void logException(Exception apiException, HttpStatus status) {
    if (status.is5xxServerError()) {
      log.warn("Unexpected exception from API", apiException);
    } else {
      log.debug("Error caught", apiException);
    }
  }

  protected ResponseEntity<String> internalServerErrorResponse(Exception e) {
    return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
  }

  public ObjectMapper getMapper() {
    return this.mapper;
  }

  public void setMapper(ObjectMapper mapper) {
    this.mapper = mapper;
  }

  public ApiProperties getProperties() {
    return this.properties;
  }

  public void setProperties(ApiProperties properties) {
    this.properties = properties;
  }
}
