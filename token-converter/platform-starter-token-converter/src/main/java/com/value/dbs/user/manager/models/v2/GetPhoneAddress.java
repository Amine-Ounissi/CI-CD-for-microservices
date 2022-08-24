package com.value.dbs.user.manager.models.v2;

import com.value.buildingblocks.persistence.model.AdditionalPropertiesAware;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.HashMap;
import java.util.Map;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"key", "type", "primary", "number"})
@JsonIgnoreProperties(ignoreUnknown = true)
public class GetPhoneAddress implements AdditionalPropertiesAware {
  @JsonProperty("key")
  @Size(min = 1, max = 70)
  @NotNull
  private String key;
  
  @JsonProperty("type")
  @Size(min = 1, max = 36)
  @NotNull
  private String type;
  
  @JsonProperty("primary")
  private Boolean primary = Boolean.valueOf(false);
  
  @JsonProperty("number")
  @Size(min = 1, max = 30)
  @NotNull
  private String number;
  
  @JsonInclude(JsonInclude.Include.NON_EMPTY)
  private Map<String, String> additions = new HashMap<>();
  
  @JsonProperty("key")
  public String getKey() {
    return this.key;
  }
  
  @JsonProperty("key")
  public void setKey(String key) {
    this.key = key;
  }
  
  public GetPhoneAddress withKey(String key) {
    this.key = key;
    return this;
  }
  
  @JsonProperty("type")
  public String getType() {
    return this.type;
  }
  
  @JsonProperty("type")
  public void setType(String type) {
    this.type = type;
  }
  
  public GetPhoneAddress withType(String type) {
    this.type = type;
    return this;
  }
  
  @JsonProperty("primary")
  public Boolean getPrimary() {
    return this.primary;
  }
  
  @JsonProperty("primary")
  public void setPrimary(Boolean primary) {
    this.primary = primary;
  }
  
  public GetPhoneAddress withPrimary(Boolean primary) {
    this.primary = primary;
    return this;
  }
  
  @JsonProperty("number")
  public String getNumber() {
    return this.number;
  }
  
  @JsonProperty("number")
  public void setNumber(String number) {
    this.number = number;
  }
  
  public GetPhoneAddress withNumber(String number) {
    this.number = number;
    return this;
  }
  
  public String toString() {
    return ToStringBuilder.reflectionToString(this);
  }
  
  public int hashCode() {
    return (new HashCodeBuilder()).append(this.key).append(this.type).append(this.primary).append(this.number).toHashCode();
  }
  
  public boolean equals(Object other) {
    if (other == this)
      return true; 
    if (!(other instanceof GetPhoneAddress))
      return false; 
    GetPhoneAddress rhs = (GetPhoneAddress)other;
    return (new EqualsBuilder()).append(this.key, rhs.key).append(this.type, rhs.type).append(this.primary, rhs.primary).append(this.number, rhs.number).isEquals();
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
