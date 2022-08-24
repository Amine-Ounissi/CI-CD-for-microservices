package com.value.dbs.user.manager.models.v2;

import com.value.buildingblocks.persistence.model.AdditionalPropertiesAware;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"resourceId", "status", "errors"})
@JsonIgnoreProperties(ignoreUnknown = true)
public class BatchResponseItem implements AdditionalPropertiesAware {
  @JsonProperty("resourceId")
  @Size(min = 1)
  @NotNull
  private String resourceId;
  
  @JsonProperty("status")
  @NotNull
  private Status status;
  
  @JsonProperty("errors")
  @Valid
  private List<String> errors = new ArrayList<>();
  
  @JsonInclude(JsonInclude.Include.NON_EMPTY)
  private Map<String, String> additions = new HashMap<>();
  
  @JsonProperty("resourceId")
  public String getResourceId() {
    return this.resourceId;
  }
  
  @JsonProperty("resourceId")
  public void setResourceId(String resourceId) {
    this.resourceId = resourceId;
  }
  
  public BatchResponseItem withResourceId(String resourceId) {
    this.resourceId = resourceId;
    return this;
  }
  
  @JsonProperty("status")
  public Status getStatus() {
    return this.status;
  }
  
  @JsonProperty("status")
  public void setStatus(Status status) {
    this.status = status;
  }
  
  public BatchResponseItem withStatus(Status status) {
    this.status = status;
    return this;
  }
  
  @JsonProperty("errors")
  public List<String> getErrors() {
    return this.errors;
  }
  
  @JsonProperty("errors")
  public void setErrors(List<String> errors) {
    this.errors = errors;
  }
  
  public BatchResponseItem withErrors(List<String> errors) {
    this.errors = errors;
    return this;
  }
  
  public String toString() {
    return ToStringBuilder.reflectionToString(this);
  }
  
  public int hashCode() {
    return (new HashCodeBuilder()).append(this.resourceId).append(this.status).append(this.errors).toHashCode();
  }
  
  public boolean equals(Object other) {
    if (other == this)
      return true; 
    if (!(other instanceof BatchResponseItem))
      return false; 
    BatchResponseItem rhs = (BatchResponseItem)other;
    return (new EqualsBuilder()).append(this.resourceId, rhs.resourceId).append(this.status, rhs.status).append(this.errors, rhs.errors).isEquals();
  }
  
  @JsonProperty("additions")
  public Map<String, String> getAdditions() {
    return this.additions;
  }
  
  @JsonProperty("additions")
  public void setAdditions(Map<String, String> additions) {
    this.additions = additions;
  }
  
  public enum Status {
    HTTP_STATUS_OK("200"),
    HTTP_STATUS_CREATED("201"),
    HTTP_STATUS_NO_CONTENT("204"),
    HTTP_STATUS_BAD_REQUEST("400"),
    HTTP_STATUS_NOT_FOUND("404"),
    HTTP_STATUS_INTERNAL_SERVER_ERROR("500");
    
    private final String value;
    
    private static final Map<String, Status> CONSTANTS = new HashMap<>();
    
    static {
      for (Status c : values())
        CONSTANTS.put(c.value, c); 
    }
    
    Status(String value) {
      this.value = value;
    }
    
    @JsonValue
    public String toString() {
      return this.value;
    }
    
    @JsonCreator
    public static Status fromValue(String value) {
      Status constant = CONSTANTS.get(value);
      if (constant == null)
        throw new IllegalArgumentException(value); 
      return constant;
    }
  }
}
