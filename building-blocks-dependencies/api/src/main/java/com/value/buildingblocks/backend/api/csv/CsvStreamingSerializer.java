package com.value.buildingblocks.backend.api.csv;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.core.FormatSchema;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SequenceWriter;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

public class CsvStreamingSerializer<T> {

  private Class<T> type;

  private List<Class<?>> mixIns = new ArrayList<>();

  private List<String> ignoredProperties = new ArrayList<>();

  private SequenceWriter sequenceWriter;

  public CsvStreamingSerializer(Class<T> type, List<Class<?>> mixIns,
    List<String> ignoredProperties) {
    Assert.notNull(type, "Type can not be null.");
    this.type = type;
    this.mixIns = mixIns;
    this.ignoredProperties = ignoredProperties;
  }

  private CsvSchema buildSchema() {
    CsvSchema.Builder builder = CsvSchema.builder();
    List<String> values = getOrderedJsonPropertyAnnotatedMembers(this.type);
    if (!CollectionUtils.isEmpty(this.ignoredProperties)) {
      values.removeAll(this.ignoredProperties);
    }
    values.forEach(builder::addColumn);
    return builder.build().withLineSeparator("\r\n").withHeader();
  }

  private Set<String> getJsonPropertyAnnotatedMembers(Class<?> clazz) {
    Set<String> values = new HashSet<>();
    for (Field field : clazz.getDeclaredFields()) {
      JsonProperty property = field.<JsonProperty>getAnnotation(JsonProperty.class);
      if (property != null) {
        values.add(property.value());
      }
    }
    for (Method method : clazz.getDeclaredMethods()) {
      JsonProperty property = method.<JsonProperty>getAnnotation(JsonProperty.class);
      if (property != null) {
        values.add(property.value());
      }
    }
    return values;
  }

  private List<String> getOrderedJsonPropertyAnnotatedMembers(Class<?> clazz) {
    Set<String> values = getJsonPropertyAnnotatedMembers(clazz);
    List<String> orderedValues = new ArrayList<>();
    JsonPropertyOrder order = clazz.<JsonPropertyOrder>getAnnotation(JsonPropertyOrder.class);
    if (order != null) {
      orderedValues.addAll(Arrays.asList(order.value()));
      values.removeAll(orderedValues);
    }
    orderedValues.addAll(values);
    return orderedValues;
  }

  private CsvMapper createCsvMapper() {
    CsvMapper csvMapper = new CsvMapper();
    csvMapper.configure(JsonGenerator.Feature.IGNORE_UNKNOWN, true);
    if (this.mixIns != null) {
      this.mixIns.forEach(mixIn -> csvMapper.addMixIn(this.type, mixIn));
    }
    return csvMapper;
  }

  public void writeCsv(Collection<T> collection, OutputStream outputStream) throws IOException {
    if (CollectionUtils.isEmpty(collection)) {
      return;
    }
    if (this.sequenceWriter == null) {
      this

        .sequenceWriter = createCsvMapper().writer((FormatSchema) buildSchema())
        .writeValues(outputStream);
    }
    this.sequenceWriter.writeAll(collection);
  }
}
