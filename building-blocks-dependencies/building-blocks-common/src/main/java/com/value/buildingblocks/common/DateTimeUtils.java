package com.value.buildingblocks.common;

import java.time.ZoneOffset;
import java.util.TimeZone;

public final class DateTimeUtils {
  public static final String RFC2616_ZONED_DATE_TIME_FORMAT = "EEE, d MMM yyyy HH:mm:ss z";
  
  public static final String RFC3339_LOCAL_DATE_FORMAT = "yyyy-MM-dd";
  
  public static final String RFC3339_LOCAL_DATE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";
  
  public static final String RFC3339_LOCAL_TIME_FORMAT = "HH:mm:ss";
  
  public static final String RFC3339_ZONED_DATE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ssX";
  
  public static final String RFC3339_ZONED_DATE_TIME_FORMAT_X = "yyyy-MM-dd'T'HH:mm[:ss][.SSS][.SS][.S][XXX][X]";
  
  public static final TimeZone TIME_ZONE_UTC = TimeZone.getTimeZone(ZoneOffset.UTC);
  
  public static final String RFC3339_ZONED_DATE_TIME_FORMAT_Z = "yyyy-MM-dd'T'HH:mm:ssZ";
  
  private DateTimeUtils() {
    throw new IllegalStateException("Nope!");
  }
}
