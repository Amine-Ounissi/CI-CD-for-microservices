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
@JsonPropertyOrder({"type", "primary", "address"})
@JsonIgnoreProperties(ignoreUnknown = true)
public class ElectronicAddress implements AdditionalPropertiesAware {
  @JsonProperty("type")
  @Size(min = 1, max = 36)
  @NotNull
  private String type;
  
  @JsonProperty("primary")
  private Boolean primary = Boolean.valueOf(false);
  
  @JsonProperty("address")
  @Size(min = 1, max = 255)
  @NotNull
  private String address;
  
  @JsonInclude(JsonInclude.Include.NON_EMPTY)
  private Map<String, String> additions = new HashMap<>();
  
  @JsonProperty("type")
  public String getType() {
    return this.type;
  }
  
  @JsonProperty("type")
  public void setType(String type) {
    this.type = type;
  }
  
  public ElectronicAddress withType(String type) {
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
  
  public ElectronicAddress withPrimary(Boolean primary) {
    this.primary = primary;
    return this;
  }
  
  @JsonProperty("address")
  public String getAddress() {
    return this.address;
  }
  
  @JsonProperty("address")
  public void setAddress(String address) {
    this.address = address;
  }
  
  public ElectronicAddress withAddress(String address) {
    this.address = address;
    return this;
  }
  
  public String toString() {
    return ToStringBuilder.reflectionToString(this);
  }
  
  public int hashCode() {
    return (new HashCodeBuilder()).append(this.type).append(this.primary).append(this.address).toHashCode();
  }
  
  public boolean equals(Object other) {
    if (other == this)
      return true; 
    if (!(other instanceof ElectronicAddress))
      return false; 
    ElectronicAddress rhs = (ElectronicAddress)other;
    return (new EqualsBuilder()).append(this.type, rhs.type).append(this.primary, rhs.primary).append(this.address, rhs.address).isEquals();
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
