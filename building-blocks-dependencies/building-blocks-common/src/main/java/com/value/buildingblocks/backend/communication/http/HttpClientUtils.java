package com.value.buildingblocks.backend.communication.http;

import com.value.buildingblocks.presentation.errors.InternalServerErrorException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.lang.Nullable;
import org.springframework.web.client.HttpMessageConverterExtractor;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;

public final class HttpClientUtils {
  protected static final MediaType APPLICATION_JSON_WILDCARD = new MediaType("application", "*+json");
  
  private static final Logger log = LoggerFactory.getLogger(HttpClientUtils.class);
  
  private HttpClientUtils() {
    throw new IllegalStateException("Nope!");
  }
  
  public static <T> T extractException(@Nullable ObjectMapper objectMapper, List<HttpMessageConverter<?>> messageConverters, RestClientResponseException exception, Class<T> exceptionClass) {
    log.debug("extracting {} to ", exception, exceptionClass);
    try {
      if (objectMapper != null && isJson(exception.getResponseHeaders().getContentType()))
        return (T)objectMapper.readValue(exception.getResponseBodyAsString(), exceptionClass); 
      HttpMessageConverterExtractor extractor = new HttpMessageConverterExtractor(exceptionClass, messageConverters);
      T result = (T)extractor.extractData(new RestClientResponseExceptionClientHttpResponseAdaptor(exception));
      if (result == null)
        return exceptionClass.getDeclaredConstructor(new Class[0]).newInstance(new Object[0]); 
      return result;
    } catch (JsonMappingException jsonMappingException) {
      throw new InternalServerErrorException(jsonMappingException);
    } catch (JsonParseException jsonParseException) {
      throw new InternalServerErrorException(jsonParseException);
    } catch (IOException ioException) {
      throw new InternalServerErrorException(ioException);
    } catch (RestClientException unexpectedException) {
      throw new InternalServerErrorException(unexpectedException);
    } catch (ReflectiveOperationException reflectionException) {
      throw new InternalServerErrorException(reflectionException);
    } 
  }
  
  private static boolean isJson(MediaType contentType) {
    return (MediaType.APPLICATION_JSON.includes(contentType) || APPLICATION_JSON_WILDCARD
      .includes(contentType));
  }
}
