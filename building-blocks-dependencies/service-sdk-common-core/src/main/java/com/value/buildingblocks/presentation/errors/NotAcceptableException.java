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
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"message", "supportedMediaTypes"})
@JsonIgnoreProperties(ignoreUnknown = true)
@ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
public class NotAcceptableException extends ApiErrorException {
  private static final long serialVersionUID = -4834828475888685571L;
  
  @JsonProperty("supportedMediaTypes")
  @Valid
  private List<String> supportedMediaTypes = new ArrayList<>();
  
  public NotAcceptableException(String message, Throwable cause) {
    super(message, cause);
  }
  
  public NotAcceptableException(String message) {
    super(message);
  }
  
  public NotAcceptableException(Throwable cause) {
    super(cause);
  }
  
  public NotAcceptableException withMessage(String message) {
    return (NotAcceptableException)super.withMessage(message);
  }
  
  @JsonProperty("supportedMediaTypes")
  public List<String> getSupportedMediaTypes() {
    return this.supportedMediaTypes;
  }
  
  @JsonProperty("supportedMediaTypes")
  public void setSupportedMediaTypes(List<String> supportedMediaTypes) {
    this.supportedMediaTypes = supportedMediaTypes;
  }
  
  public NotAcceptableException withSupportedMediaTypes(List<String> supportedMediaTypes) {
    this.supportedMediaTypes = supportedMediaTypes;
    return this;
  }
  
  public String toString() {
    return ToStringBuilder.reflectionToString(this);
  }
  
  public int hashCode() {
    return (new HashCodeBuilder())
      .append(getMessage())
      .append(this.supportedMediaTypes)
      .toHashCode();
  }
  
  public boolean equals(Object other) {
    if (other == this)
      return true; 
    if (other == null)
      return false; 
    if (getClass() != other.getClass())
      return false; 
    NotAcceptableException rhs = (NotAcceptableException)other;
    return (new EqualsBuilder()).append(getMessage(), rhs.getMessage())
      .append(this.supportedMediaTypes, rhs.supportedMediaTypes)
      .isEquals();
  }
  
  public NotAcceptableException() {}
}
