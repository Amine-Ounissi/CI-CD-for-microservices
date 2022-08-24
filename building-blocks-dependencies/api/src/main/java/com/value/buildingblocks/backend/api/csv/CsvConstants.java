package com.value.buildingblocks.backend.api.csv;

import org.springframework.http.MediaType;

public final class CsvConstants {

  public static final String RFC_4180_LINE_SEPARATOR = "\r\n";

  public static final MediaType MEDIA_TYPE_TEXT_CSV = MediaType.valueOf("text/csv");
}
