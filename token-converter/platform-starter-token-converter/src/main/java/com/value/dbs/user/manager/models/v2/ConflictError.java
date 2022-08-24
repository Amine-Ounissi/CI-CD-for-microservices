package com.value.dbs.user.manager.models.v2;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.ArrayList;
import java.util.List;
import javax.validation.Valid;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"message", "errors"})
@JsonIgnoreProperties(ignoreUnknown = true)
@ResponseStatus(HttpStatus.CONFLICT)
public class ConflictError extends RuntimeException {
  @JsonProperty("message")
  private String message;
  
  @JsonProperty("errors")
  @Valid
  private List<Error> errors = new ArrayList<>();
  
  @JsonProperty("message")
  public String getMessage() {
    return this.message;
  }
  
  @JsonProperty("message")
  public void setMessage(String message) {
    this.message = message;
  }
  
  public ConflictError withMessage(String message) {
    this.message = message;
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
  
  public ConflictError withErrors(List<Error> errors) {
    this.errors = errors;
    return this;
  }
  
  public String toString() {
    return ToStringBuilder.reflectionToString(this);
  }
  
  public int hashCode() {
    return (new HashCodeBuilder()).append(this.message).append(this.errors).toHashCode();
  }
  
  public boolean equals(Object other) {
    if (other == this)
      return true; 
    if (!(other instanceof ConflictError))
      return false; 
    ConflictError rhs = (ConflictError)other;
    return (new EqualsBuilder()).append(this.message, rhs.message).append(this.errors, rhs.errors).isEquals();
  }
}
