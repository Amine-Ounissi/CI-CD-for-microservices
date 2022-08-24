package com.value.buildingblocks.backend.api;

import com.fasterxml.jackson.datatype.jsr310.deser.InstantDeserializer;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;

public class ISO8601OffsetDateTimeDeserializer extends InstantDeserializer<OffsetDateTime> {
  private static final long serialVersionUID = -237644245579626895L;
  
  public ISO8601OffsetDateTimeDeserializer() {
    super(InstantDeserializer.OFFSET_DATE_TIME, (new DateTimeFormatterBuilder())
        
        .parseCaseInsensitive()
        .append(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        .optionalStart().appendOffset("+HH:MM", "+00:00").optionalEnd()
        .optionalStart().appendOffset("+HHMM", "+0000").optionalEnd()
        .optionalStart().appendOffset("+HH", "Z").optionalEnd()
        .toFormatter());
  }
}
