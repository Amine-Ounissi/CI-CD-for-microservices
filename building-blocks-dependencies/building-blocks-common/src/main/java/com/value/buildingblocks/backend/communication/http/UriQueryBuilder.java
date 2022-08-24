package com.value.buildingblocks.backend.communication.http;

import com.value.buildingblocks.presentation.errors.InternalServerErrorException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.time.temporal.Temporal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TimeZone;
import java.util.stream.Collectors;

public class UriQueryBuilder {
  private static final DateTimeFormatter RFC3339_DATE_TIME_FORMATTER = (new DateTimeFormatterBuilder())
    .parseCaseInsensitive().parseLenient()
    
    .appendPattern("yyyy-MM-dd'T'HH:mm:ss")
    
    .optionalStart().appendFraction(ChronoField.MILLI_OF_SECOND, 0, 3, true).optionalEnd()

    
    .appendPattern("xx").toFormatter();
  
  public static final String FORMAT_TIME_ONLY = "time-only";
  
  public static final String FORMAT_DATETIME_ONLY = "datetime-only";
  
  public static final String FORMAT_DATETIME = "datetime";
  
  public static final String FORMAT_DATE_ONLY = "date-only";
  
  private final Map<String, DateFormat> dateFormats = new HashMap<>();
  
  private final Map<String, DateTimeFormatter> dateTimeFormatters = new HashMap<>();
  
  private List<QueryParameter> parameters = new ArrayList<>();
  
  private String defaultDateFormatString;
  
  private String separator;
  
  private String queryStart;
  
  private TimeZone timeZone;
  
  private UriQueryBuilder() {
    this.dateTimeFormatters.put("date-only", DateTimeFormatter.ISO_LOCAL_DATE);
    this.dateTimeFormatters.put("datetime", RFC3339_DATE_TIME_FORMATTER);
    this.dateTimeFormatters.put("datetime-only", DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    this.dateTimeFormatters.put("time-only", DateTimeFormatter.ISO_LOCAL_TIME);
  }
  
  public static UriQueryBuilder instance() {
    return (new UriQueryBuilder()).withDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
      .withTimeZone(TimeZone.getTimeZone(ZoneOffset.UTC)).withQueryStart("?").withSeparator("&");
  }
  
  public UriQueryBuilder withSeparator(String separator) {
    this.separator = separator;
    return this;
  }
  
  public UriQueryBuilder withTimeZone(TimeZone timeZone) {
    this.timeZone = timeZone;
    return this;
  }
  
  public UriQueryBuilder withQueryStart(String queryStart) {
    this.queryStart = queryStart;
    return this;
  }
  
  public UriQueryBuilder withDateFormat(String dateFormatString) {
    this.defaultDateFormatString = dateFormatString;
    return this;
  }
  
  public UriQueryBuilder addParameter(String key, Object value) {
    Objects.requireNonNull(key, "Null keys are not allowed");
    if (value != null && value.getClass().isArray()) {
      Object[] array = (Object[])value;
      for (Object object : array)
        this.parameters.add(new QueryParameter(key, object)); 
    } else {
      this.parameters.add(new QueryParameter(key, value));
    } 
    return this;
  }
  
  public UriQueryBuilder addParameterIfNotNull(String key, Object value, String format) {
    if (value instanceof Date)
      return addParameterIfNotNull(key, (Date)value, format); 
    if (value instanceof Date[])
      return addParameterIfNotNull(key, (Date[])value, format); 
    if (value instanceof Temporal)
      return addParameterIfNotNull(key, (Temporal)value, format); 
    if (value instanceof Temporal[])
      return addParameterIfNotNull(key, (Temporal[])value, format); 
    return addParameterIfNotNull(key, value);
  }
  
  public UriQueryBuilder addParameterIfNotNull(String key, Temporal value, String format) {
    if (value == null)
      return this; 
    return addParameter(key, getDateTimeFormatter(format).format(value));
  }
  
  public UriQueryBuilder addParameterIfNotNull(String key, Object value) {
    if (value != null)
      addParameter(key, value); 
    return this;
  }
  
  public UriQueryBuilder addParameterIfNotNull(String key, Date value, String format) {
    if (value != null)
      addParameter(key, getDateFormat(format).format(value)); 
    return this;
  }
  
  public UriQueryBuilder addParameterIfNotNull(String key, Date[] value, String format) {
    if (value != null)
      for (Date date : value)
        addParameterIfNotNull(key, date, format);  
    return this;
  }
  
  public UriQueryBuilder addParameterIfNotNull(String key, Temporal[] value, String format) {
    if (value != null)
      for (Temporal date : value)
        addParameterIfNotNull(key, date, format);  
    return this;
  }
  
  public String build() {
    if (this.parameters.isEmpty())
      return ""; 
    return this.queryStart + (String)this.parameters.stream().map(this::formatQueryParameter)
      .collect(Collectors.joining(this.separator));
  }
  
  private String encodeQueryString(String value) {
    try {
      return URLEncoder.encode(value, StandardCharsets.UTF_8.name());
    } catch (UnsupportedEncodingException shouldNeverHappen) {
      throw new InternalServerErrorException(shouldNeverHappen);
    } 
  }
  
  private DateFormat getDateFormat(String formatString) {
    DateFormat format = this.dateFormats.computeIfAbsent(formatString, this::computeDateFormat);
    format.setTimeZone(this.timeZone);
    return format;
  }
  
  private DateFormat computeDateFormat(String formatString) {
    return new SimpleDateFormat(formatString);
  }
  
  private DateTimeFormatter getDateTimeFormatter(String formatString) {
    return ((DateTimeFormatter)this.dateTimeFormatters.computeIfAbsent(formatString, this::computeDateTimeFormatter))
      .withZone(this.timeZone.toZoneId());
  }
  
  private DateTimeFormatter computeDateTimeFormatter(String formatString) {
    return (new DateTimeFormatterBuilder()).appendPattern(formatString).toFormatter();
  }
  
  private String formatQueryParameter(QueryParameter p) {
    String valueString;
    if (p.getValue() == null) {
      valueString = "";
    } else if (p.getValue() instanceof Date) {
      valueString = getDateFormat(this.defaultDateFormatString).format(p.getValue());
    } else if (p.getValue() instanceof ZonedDateTime) {
      valueString = getDateTimeFormatter("datetime").format((ZonedDateTime)p.getValue());
    } else if (p.getValue() instanceof LocalDate) {
      valueString = getDateTimeFormatter("date-only").format((LocalDate)p.getValue());
    } else if (p.getValue() instanceof LocalTime) {
      valueString = getDateTimeFormatter("time-only").format((LocalTime)p.getValue());
    } else if (p.getValue() instanceof LocalDateTime) {
      valueString = getDateTimeFormatter("datetime-only").format((LocalDateTime)p.getValue());
    } else {
      valueString = String.valueOf(p.getValue());
    } 
    return encodeQueryString(p.getKey()) + "=" + encodeQueryString(valueString);
  }
  
  public String toString() {
    return "UriQueryBuilder [build()=" + build() + "]";
  }
}
