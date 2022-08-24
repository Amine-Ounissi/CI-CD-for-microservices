package com.value.buildingblocks.backend.api.csv;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;

public class ObjectJsonSerializer extends JsonSerializer<Object> {

  public void serialize(Object value, JsonGenerator gen, SerializerProvider serializerProvider)
    throws IOException {
    ObjectMapper objectMapper = new ObjectMapper();
    gen.writeString(objectMapper.writeValueAsString(value));
  }
}
