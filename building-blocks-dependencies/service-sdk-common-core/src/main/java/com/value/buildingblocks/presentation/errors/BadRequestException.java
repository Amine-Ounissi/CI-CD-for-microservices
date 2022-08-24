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
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"message", "errors"})
@JsonIgnoreProperties(ignoreUnknown = true)
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class BadRequestException extends ApiErrorException {
  private static final long serialVersionUID = 1L;
  
  @JsonProperty("errors")
  @Valid
  private List<Error> errors = new ArrayList<>();
  
  public BadRequestException(String message, Throwable cause) {
    super(message, cause);
  }
  
  public BadRequestException(Throwable cause) {
    super(cause);
  }
  
  public BadRequestException(String message) {
    super(message);
  }
  
  public BadRequestException withMessage(String message) {
    return (BadRequestException)super.withMessage(message);
  }
  
  @JsonProperty("errors")
  public List<Error> getErrors() {
    return this.errors;
  }
  
  @JsonProperty("errors")
  public void setErrors(List<Error> errors) {
    this.errors = errors;
  }
  
  public BadRequestException withErrors(List<Error> errors) {
    this.errors = errors;
    return this;
  }
  
  public int hashCode() {
    return (new HashCodeBuilder())
      .append(getMessage())
      .append(getErrors())
      .toHashCode();
  }
  
  public boolean equals(Object other) {
    if (other == this)
      return true; 
    if (other == null)
      return false; 
    if (getClass() != other.getClass())
      return false; 
    BadRequestException rhs = (BadRequestException)other;
    return (new EqualsBuilder())
      .append(getMessage(), rhs.getMessage())
      .append(getErrors(), rhs.getErrors())
      .isEquals();
  }
  
  public BadRequestException() {}
}
