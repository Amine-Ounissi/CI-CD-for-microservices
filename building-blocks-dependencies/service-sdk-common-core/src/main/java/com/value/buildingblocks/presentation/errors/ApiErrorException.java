package com.value.buildingblocks.presentation.errors;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"message"})
@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class ApiErrorException extends RuntimeException {
  private static final Logger log = LoggerFactory.getLogger(ApiErrorException.class);
  
  @JsonProperty("message")
  private String message;
  
  public ApiErrorException(String message, Throwable cause) {
    super(null, cause, true, log.isDebugEnabled());
    this.message = (message == null) ? "" : message;
  }
  
  public ApiErrorException(String message) {
    this(message, null);
  }
  
  public ApiErrorException(Throwable cause) {
    this(null, cause);
  }
  
  public ApiErrorException() {
    this(null, null);
  }
  
  @JsonProperty("message")
  public String getMessage() {
    return this.message;
  }
  
  @JsonProperty("message")
  public void setMessage(String message) {
    this.message = (message == null) ? "" : message;
  }
  
  public ApiErrorException withMessage(String message) {
    this.message = (message == null) ? "" : message;
    return this;
  }
  
  public String toString() {
    return ToStringBuilder.reflectionToString(this);
  }
  
  public int hashCode() {
    return (new HashCodeBuilder()).append(this.message).toHashCode();
  }
  
  public boolean equals(Object other) {
    if (other == this)
      return true; 
    if (other == null)
      return false; 
    if (!other.getClass().equals(getClass()))
      return false; 
    ApiErrorException rhs = (ApiErrorException)other;
    return (new EqualsBuilder()).append(this.message, rhs.message).isEquals();
  }
}
