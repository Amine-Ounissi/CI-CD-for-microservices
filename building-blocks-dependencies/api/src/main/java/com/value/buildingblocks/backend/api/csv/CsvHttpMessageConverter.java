package com.value.buildingblocks.backend.api.csv;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

public class CsvHttpMessageConverter extends AbstractHttpMessageConverter<Object> {

  private static final Logger log = LoggerFactory
    .getLogger(CsvHttpMessageConverter.class.getName());

  private Map<Class<?>, CsvClassProperties> csvClassPropertiesMap = new HashMap<>();

  public CsvHttpMessageConverter() {
    super(CsvConstants.MEDIA_TYPE_TEXT_CSV);
  }

  protected boolean supports(Class<?> clazz) {
    if (clazz == null) {
      return false;
    }
    if (Collection.class.isAssignableFrom(clazz)) {
      return true;
    }
    try {
      CsvClassProperties properties = this.csvClassPropertiesMap.get(clazz);
      if (properties == null || StringUtils.isEmpty(properties.getCollectionPropertyName())) {
        log.trace(
          "{} is not supported because there is no CsvClassProperties.CollectionPropertyName", clazz
            .getName());
        return false;
      }
      Field field = clazz.getDeclaredField(properties.getCollectionPropertyName());
      return Collection.class.isAssignableFrom(field.getType());
    } catch (Exception e) {
      log.warn("Invalid collection property name configured for {}", clazz, e);
      return false;
    }
  }

  protected void writeInternal(Object object, HttpOutputMessage httpOutputMessage) {
    if (object == null) {
      throw new HttpMessageNotWritableException("Null not supported.");
    }
    if (Collection.class.isAssignableFrom(object.getClass())) {
      Collection<? extends Object> collection = (Collection<? extends Object>) object;
      writeCollectionInternal(collection, httpOutputMessage);
    } else {
      CsvClassProperties properties = this.csvClassPropertiesMap.get(object.getClass());
      if (properties == null || StringUtils.isEmpty(properties.getCollectionPropertyName())) {
        throw new HttpMessageNotWritableException(
          "Collection property name not set for non-collection class.");
      }
      try {
        Field field = object.getClass().getDeclaredField(properties.getCollectionPropertyName());
        field.setAccessible(true);
        Collection<? extends Object> collection = (Collection<? extends Object>) field.get(object);
        writeCollectionInternal(collection, httpOutputMessage);
      } catch (NoSuchFieldException nsfe) {
        throw new HttpMessageNotWritableException(
          "Could not find collection property with name " + properties
            .getCollectionPropertyName(), nsfe);
      } catch (Exception e) {
        throw new HttpMessageNotWritableException("Error serializing to CSV", e);
      }
    }
  }

  private <T> void writeCollectionInternal(Collection<T> collection,
    HttpOutputMessage httpOutputMessage) {
    try {
      Class<T> type = (Class) getClassOfElements(collection);
      CsvClassProperties elementClassProperties = this.csvClassPropertiesMap.get(type);
      CsvStreamingSerializer<T> csvStreamingSerializer = new CsvStreamingSerializer<>(type,
        (elementClassProperties == null) ? null : elementClassProperties.getMixIns(),
        (elementClassProperties == null) ? null : elementClassProperties.getIgnoredProperties());
      csvStreamingSerializer.writeCsv(collection, httpOutputMessage

        .getBody());
    } catch (Exception e) {
      throw new HttpMessageNotWritableException("Error serializing CSV", e);
    }
  }

  protected Object readInternal(Class<? extends Object> clazz, HttpInputMessage httpInputMessage)
    throws IOException {
    throw new UnsupportedOperationException();
  }

  public void setCsvClassPropertiesMap(Map<Class<?>, CsvClassProperties> csvClassPropertiesMap) {
    Assert.notNull(csvClassPropertiesMap, "Class properties map can not be null");
    this.csvClassPropertiesMap = csvClassPropertiesMap;
  }

  public void addCsvClassProperties(Class<?> clazz, CsvClassProperties csvClassProperties) {
    Assert.notNull(csvClassProperties, "Class properties can not be null");
    this.csvClassPropertiesMap.put(clazz, csvClassProperties);
  }

  private Class<? extends Object> getClassOfElements(Collection<?> collection) {
    if (CollectionUtils.isEmpty(collection)) {
      return Object.class;
    }
    return (Class) collection.iterator().next().getClass();
  }
}
