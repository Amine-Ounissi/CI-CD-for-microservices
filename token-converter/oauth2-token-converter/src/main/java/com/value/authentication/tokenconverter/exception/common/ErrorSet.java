package com.value.authentication.tokenconverter.exception.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class ErrorSet {
  @JsonInclude(JsonInclude.Include.NON_NULL)
  private String error;
  
  @JsonInclude(JsonInclude.Include.NON_NULL)
  private String errorDescription;
  
  public ErrorSet(String error, String errorDescription) {
    this.error = error;
    this.errorDescription = errorDescription;
  }
  
  public ErrorSet() {}
  
  public String getError() {
    return this.error;
  }
  
  public void setError(String error) {
    this.error = error;
  }
  
  public String getErrorDescription() {
    return this.errorDescription;
  }
  
  public void setErrorDescription(String errorDescription) {
    this.errorDescription = errorDescription;
  }
  
  public String toString() {
    return (new ToStringBuilder(this))
      .append("error", this.error)
      .append("errorDescription", this.errorDescription)
      .toString();
  }
}
