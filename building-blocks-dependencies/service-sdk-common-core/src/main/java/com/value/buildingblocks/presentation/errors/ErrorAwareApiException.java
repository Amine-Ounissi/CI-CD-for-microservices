package com.value.buildingblocks.presentation.errors;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.ArrayList;
import java.util.List;
import javax.validation.Valid;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"message", "errors"})
@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class ErrorAwareApiException extends ApiErrorException {
  @JsonProperty("errors")
  @Valid
  private List<Error> errors = new ArrayList<>();
  
  public ErrorAwareApiException(String message, Throwable cause) {
    super(message, cause);
  }
  
  public ErrorAwareApiException(String message) {
    this(message, null);
  }
  
  public ErrorAwareApiException(Throwable cause) {
    this(null, cause);
  }
  
  public ErrorAwareApiException() {
    this(null, null);
  }
  
  public ErrorAwareApiException withErrors(List<Error> errors) {
    this.errors = errors;
    return this;
  }
  
  @JsonProperty("errors")
  public List<Error> getErrors() {
    return this.errors;
  }
  
  @JsonProperty("errors")
  public void setErrors(List<Error> errors) {
    this.errors = errors;
  }
  
  public boolean equals(Object o) {
    if (this == o)
      return true; 
    if (o == null || getClass() != o.getClass())
      return false; 
    ErrorAwareApiException that = (ErrorAwareApiException)o;
    return (new EqualsBuilder())
      .append(getMessage(), that.getMessage())
      .append(getErrors(), that.getErrors())
      .isEquals();
  }
  
  public int hashCode() {
    return (new HashCodeBuilder())
      .append(getMessage())
      .append(getErrors())
      .toHashCode();
  }
  
  public String toString() {
    return ToStringBuilder.reflectionToString(this);
  }
}
