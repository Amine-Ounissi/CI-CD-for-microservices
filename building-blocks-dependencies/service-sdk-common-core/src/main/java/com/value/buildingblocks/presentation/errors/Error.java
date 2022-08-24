package com.value.buildingblocks.presentation.errors;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.io.Serializable;
import java.util.Map;
import javax.validation.Valid;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"message", "key", "context"})
public class Error implements Serializable {
  public static final String CONTEXT_KEY_VALUE = "rejectedValue";
  
  public static final String CONTEXT_KEY_PATTERN = "pattern";
  
  public static final String PARSE_ERROR_KEY = "api.parse.error";
  
  public static final String INVALID_INPUT_MESSAGE = "Invalid Input";
  
  private static final long serialVersionUID = 1L;
  
  @JsonProperty("message")
  private String message;
  
  @JsonProperty("key")
  private String key;
  
  @JsonProperty("context")
  @Valid
  private Map<String, String> context;
  
  public Error() {}
  
  public Error(String key, String message, Map<String, String> context) {
    this.message = message;
    this.key = key;
    this.context = context;
  }
  
  @JsonProperty("message")
  public String getMessage() {
    return this.message;
  }
  
  @JsonProperty("message")
  public void setMessage(String message) {
    this.message = message;
  }
  
  public Error withMessage(String message) {
    this.message = message;
    return this;
  }
  
  @JsonProperty("key")
  public String getKey() {
    return this.key;
  }
  
  @JsonProperty("key")
  public void setKey(String key) {
    this.key = key;
  }
  
  public Error withKey(String key) {
    this.key = key;
    return this;
  }
  
  @JsonProperty("context")
  public Map<String, String> getContext() {
    return this.context;
  }
  
  @JsonProperty("context")
  public void setContext(Map<String, String> context) {
    this.context = context;
  }
  
  public Error withContext(Map<String, String> context) {
    this.context = context;
    return this;
  }
  
  public String toString() {
    return ToStringBuilder.reflectionToString(this);
  }
  
  public int hashCode() {
    return (new HashCodeBuilder()).append(this.message).append(this.key).append(this.context).toHashCode();
  }
  
  public boolean equals(Object other) {
    if (other == this)
      return true; 
    if (!(other instanceof Error))
      return false; 
    Error rhs = (Error)other;
    return (new EqualsBuilder()).append(this.message, rhs.message).append(this.key, rhs.key).append(this.context, rhs.context)
      .isEquals();
  }
}
