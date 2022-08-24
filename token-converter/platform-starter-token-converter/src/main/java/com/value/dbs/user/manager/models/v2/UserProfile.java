package com.value.dbs.user.manager.models.v2;

import com.value.buildingblocks.persistence.model.AdditionalPropertiesAware;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.validation.Valid;
import javax.validation.constraints.Size;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"fullName", "phone-addresses", "electronic-addresses", "postal-addresses"})
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserProfile implements AdditionalPropertiesAware {
  @JsonProperty("fullName")
  @Size(min = 1, max = 255)
  private String fullName;
  
  @JsonProperty("phone-addresses")
  @Valid
  private List<GetPhoneAddress> phoneAddresses = new ArrayList<>();
  
  @JsonProperty("electronic-addresses")
  @Valid
  private List<GetElectronicAddress> electronicAddresses = new ArrayList<>();
  
  @JsonProperty("postal-addresses")
  @Valid
  private List<GetPostalAddress> postalAddresses = new ArrayList<>();
  
  @JsonInclude(JsonInclude.Include.NON_EMPTY)
  private Map<String, String> additions = new HashMap<>();
  
  @JsonProperty("fullName")
  public String getFullName() {
    return this.fullName;
  }
  
  @JsonProperty("fullName")
  public void setFullName(String fullName) {
    this.fullName = fullName;
  }
  
  public UserProfile withFullName(String fullName) {
    this.fullName = fullName;
    return this;
  }
  
  @JsonProperty("phone-addresses")
  public List<GetPhoneAddress> getPhoneAddresses() {
    return this.phoneAddresses;
  }
  
  @JsonProperty("phone-addresses")
  public void setPhoneAddresses(List<GetPhoneAddress> phoneAddresses) {
    this.phoneAddresses = phoneAddresses;
  }
  
  public UserProfile withPhoneAddresses(List<GetPhoneAddress> phoneAddresses) {
    this.phoneAddresses = phoneAddresses;
    return this;
  }
  
  @JsonProperty("electronic-addresses")
  public List<GetElectronicAddress> getElectronicAddresses() {
    return this.electronicAddresses;
  }
  
  @JsonProperty("electronic-addresses")
  public void setElectronicAddresses(List<GetElectronicAddress> electronicAddresses) {
    this.electronicAddresses = electronicAddresses;
  }
  
  public UserProfile withElectronicAddresses(List<GetElectronicAddress> electronicAddresses) {
    this.electronicAddresses = electronicAddresses;
    return this;
  }
  
  @JsonProperty("postal-addresses")
  public List<GetPostalAddress> getPostalAddresses() {
    return this.postalAddresses;
  }
  
  @JsonProperty("postal-addresses")
  public void setPostalAddresses(List<GetPostalAddress> postalAddresses) {
    this.postalAddresses = postalAddresses;
  }
  
  public UserProfile withPostalAddresses(List<GetPostalAddress> postalAddresses) {
    this.postalAddresses = postalAddresses;
    return this;
  }
  
  public String toString() {
    return ToStringBuilder.reflectionToString(this);
  }
  
  public int hashCode() {
    return (new HashCodeBuilder()).append(this.fullName).append(this.phoneAddresses).append(this.electronicAddresses).append(this.postalAddresses).toHashCode();
  }
  
  public boolean equals(Object other) {
    if (other == this)
      return true; 
    if (!(other instanceof UserProfile))
      return false; 
    UserProfile rhs = (UserProfile)other;
    return (new EqualsBuilder()).append(this.fullName, rhs.fullName).append(this.phoneAddresses, rhs.phoneAddresses).append(this.electronicAddresses, rhs.electronicAddresses).append(this.postalAddresses, rhs.postalAddresses).isEquals();
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
