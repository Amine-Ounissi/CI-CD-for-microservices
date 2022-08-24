package com.value.dbs.user.manager.models.v2;

import com.value.buildingblocks.persistence.model.AdditionalPropertiesAware;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.HashMap;
import java.util.Map;
import javax.validation.constraints.NotNull;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"path", "code", "message"})
public class Error implements AdditionalPropertiesAware {
  @JsonProperty("path")
  private String path;
  
  @JsonProperty("code")
  @NotNull
  private String code;
  
  @JsonProperty("message")
  private String message;
  
  @JsonInclude(JsonInclude.Include.NON_EMPTY)
  private Map<String, String> additions = new HashMap<>();
  
  @JsonProperty("path")
  public String getPath() {
    return this.path;
  }
  
  @JsonProperty("path")
  public void setPath(String path) {
    this.path = path;
  }
  
  public Error withPath(String path) {
    this.path = path;
    return this;
  }
  
  @JsonProperty("code")
  public String getCode() {
    return this.code;
  }
  
  @JsonProperty("code")
  public void setCode(String code) {
    this.code = code;
  }
  
  public Error withCode(String code) {
    this.code = code;
    return this;
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
  
  public String toString() {
    return ToStringBuilder.reflectionToString(this);
  }
  
  public int hashCode() {
    return (new HashCodeBuilder()).append(this.path).append(this.code).append(this.message).toHashCode();
  }
  
  public boolean equals(Object other) {
    if (other == this)
      return true; 
    if (!(other instanceof Error))
      return false; 
    Error rhs = (Error)other;
    return (new EqualsBuilder()).append(this.path, rhs.path).append(this.code, rhs.code).append(this.message, rhs.message).isEquals();
  }
  
  @JsonProperty("additions")
  public Map<String, String> getAdditions() {
    return this.additions;
  }
  
  @JsonProperty("additions")
  public void setAdditions(Map<String, String> additions) {
    this.additions = additions;
  }
}
