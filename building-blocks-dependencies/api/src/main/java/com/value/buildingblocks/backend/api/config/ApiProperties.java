package com.value.buildingblocks.backend.api.config;

import com.value.buildingblocks.backend.api.csv.CsvClassProperties;
import java.util.HashMap;
import java.util.Map;
import javax.validation.constraints.Pattern;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.DeprecatedConfigurationProperty;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties("value.api")
@Validated
public class ApiProperties {

  private boolean override5xxErrorMessages = true;

  private boolean disableSecureExceptions;

  @Pattern(regexp = "([^^$]|^\\S+$)", message = "No whitespace allowed")
  private String name = "api";

  private final Mvc mvc = new Mvc();

  private final Errors errors = new Errors();

  private boolean useSuffixPatternMatch;

  private boolean usePathExtensionsForContentNegotiation;

  @Deprecated
  @DeprecatedConfigurationProperty(reason = "In Spring Framework 5.3 the use of suffixes for content negotiation will be removed.")
  public boolean isUseSuffixPatternMatch() {
    return this.useSuffixPatternMatch;
  }

  @Deprecated
  public void setUseSuffixPatternMatch(boolean useSuffixPatternMatch) {
    this.useSuffixPatternMatch = useSuffixPatternMatch;
  }

  @Deprecated
  @DeprecatedConfigurationProperty(reason = "In Spring Framework 5.3 the use of suffixes for content negotiation will be removed.")
  public boolean isUsePathExtensionsForContentNegotiation() {
    return this.usePathExtensionsForContentNegotiation;
  }

  @Deprecated
  public void setUsePathExtensionsForContentNegotiation(
    boolean usePathExtensionsForContentNegotiation) {
    this.usePathExtensionsForContentNegotiation = usePathExtensionsForContentNegotiation;
  }

  public Mvc getMvc() {
    return this.mvc;
  }

  public boolean isOverride5xxErrorMessages() {
    return this.override5xxErrorMessages;
  }

  public void setOverride5xxErrorMessages(boolean override5xxErrorMessages) {
    this.override5xxErrorMessages = override5xxErrorMessages;
  }

  public boolean isDisableSecureExceptions() {
    return this.disableSecureExceptions;
  }

  public void setDisableSecureExceptions(boolean disableSecureExceptions) {
    this.disableSecureExceptions = disableSecureExceptions;
  }

  public Errors getErrors() {
    return this.errors;
  }

  public String getName() {
    return this.name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public static class Errors {

    private boolean handleHttpMediaTypeNotAcceptableException = true;

    private boolean handleMethodArgumentNotValidException = true;

    private boolean handleBindingResultErrorsException = true;

    private boolean handleConversionFailedException = true;

    private boolean handleApiExceptions = true;

    private boolean handleHttpMessageConversionException = true;

    private boolean handleConstraintViolationException = true;

    private boolean handleNonApiExceptions = true;

    private String message400 = HttpStatus.BAD_REQUEST.getReasonPhrase();

    public boolean isHandleHttpMediaTypeNotAcceptableException() {
      return this.handleHttpMediaTypeNotAcceptableException;
    }

    public void setHandleHttpMediaTypeNotAcceptableException(
      boolean handleHttpMediaTypeNotAcceptableException) {
      this.handleHttpMediaTypeNotAcceptableException = handleHttpMediaTypeNotAcceptableException;
    }

    public boolean isHandleMethodArgumentNotValidException() {
      return this.handleMethodArgumentNotValidException;
    }

    public void setHandleMethodArgumentNotValidException(boolean handleBindingResults) {
      this.handleMethodArgumentNotValidException = handleBindingResults;
    }

    public boolean isHandleNonApiExceptions() {
      return this.handleNonApiExceptions;
    }

    public void setHandleNonApiExceptions(boolean handleNonApiExceptions) {
      this.handleNonApiExceptions = handleNonApiExceptions;
    }

    public boolean isHandleConversionFailedException() {
      return this.handleConversionFailedException;
    }

    public void setHandleConversionFailedException(boolean handleConversionFailedException) {
      this.handleConversionFailedException = handleConversionFailedException;
    }

    public boolean isHandleApiExceptions() {
      return this.handleApiExceptions;
    }

    public void setHandleApiExceptions(boolean handleApiExceptions) {
      this.handleApiExceptions = handleApiExceptions;
    }

    public boolean isHandleBindingResultErrorsException() {
      return this.handleBindingResultErrorsException;
    }

    public void setHandleBindingResultErrorsException(boolean handleBindingResultErrorsExceptions) {
      this.handleBindingResultErrorsException = handleBindingResultErrorsExceptions;
    }

    public boolean isHandleHttpMessageConversionException() {
      return this.handleHttpMessageConversionException;
    }

    public void setHandleHttpMessageConversionException(
      boolean handleHttpMessageConversionException) {
      this.handleHttpMessageConversionException = handleHttpMessageConversionException;
    }

    public boolean isHandleConstraintViolationException() {
      return this.handleConstraintViolationException;
    }

    public void setHandleConstraintViolationException(boolean handleConstraintViolationException) {
      this.handleConstraintViolationException = handleConstraintViolationException;
    }

    public String getMessage400() {
      return this.message400;
    }

    public void setMessage400(String validationMessage) {
      this.message400 = validationMessage;
    }
  }

  public static class Mvc {

    private final Csv csv = new Csv();

    public Csv getCsv() {
      return this.csv;
    }

    public static class Csv {

      private boolean autoconfig = true;

      private Map<Class<?>, CsvClassProperties> csvClassProperties = new HashMap<>();

      public Map<Class<?>, CsvClassProperties> getCsvClassProperties() {
        return this.csvClassProperties;
      }

      public void setCsvClassProperties(Map<Class<?>, CsvClassProperties> csvClassProperties) {
        this.csvClassProperties = csvClassProperties;
      }

      public boolean isAutoconfig() {
        return this.autoconfig;
      }

      public void setAutoconfig(boolean autoconfig) {
        this.autoconfig = autoconfig;
      }
    }
  }
}
