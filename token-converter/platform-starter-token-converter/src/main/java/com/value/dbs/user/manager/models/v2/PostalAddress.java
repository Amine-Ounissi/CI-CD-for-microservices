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
@JsonPropertyOrder({"type", "primary", "department", "subDepartment", "addressLine", "buildingNumber", "streetName", "townName", "postalCode", "countrySubDivision", "country"})
@JsonIgnoreProperties(ignoreUnknown = true)
public class PostalAddress implements AdditionalPropertiesAware {
  @JsonProperty("type")
  @Size(min = 1, max = 36)
  @NotNull
  private String type;
  
  @JsonProperty("primary")
  private Boolean primary = Boolean.valueOf(false);
  
  @JsonProperty("department")
  @Size(max = 70)
  private String department;
  
  @JsonProperty("subDepartment")
  @Size(max = 70)
  private String subDepartment;
  
  @JsonProperty("addressLine")
  @Size(max = 70)
  private String addressLine;
  
  @JsonProperty("buildingNumber")
  @Size(max = 255)
  private String buildingNumber;
  
  @JsonProperty("streetName")
  @Size(max = 255)
  private String streetName;
  
  @JsonProperty("townName")
  @Size(max = 35)
  private String townName;
  
  @JsonProperty("postalCode")
  @Size(max = 16)
  private String postalCode;
  
  @JsonProperty("countrySubDivision")
  @Size(max = 35)
  private String countrySubDivision;
  
  @JsonProperty("country")
  @Size(max = 3)
  private String country;
  
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
  
  public PostalAddress withType(String type) {
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
  
  public PostalAddress withPrimary(Boolean primary) {
    this.primary = primary;
    return this;
  }
  
  @JsonProperty("department")
  public String getDepartment() {
    return this.department;
  }
  
  @JsonProperty("department")
  public void setDepartment(String department) {
    this.department = department;
  }
  
  public PostalAddress withDepartment(String department) {
    this.department = department;
    return this;
  }
  
  @JsonProperty("subDepartment")
  public String getSubDepartment() {
    return this.subDepartment;
  }
  
  @JsonProperty("subDepartment")
  public void setSubDepartment(String subDepartment) {
    this.subDepartment = subDepartment;
  }
  
  public PostalAddress withSubDepartment(String subDepartment) {
    this.subDepartment = subDepartment;
    return this;
  }
  
  @JsonProperty("addressLine")
  public String getAddressLine() {
    return this.addressLine;
  }
  
  @JsonProperty("addressLine")
  public void setAddressLine(String addressLine) {
    this.addressLine = addressLine;
  }
  
  public PostalAddress withAddressLine(String addressLine) {
    this.addressLine = addressLine;
    return this;
  }
  
  @JsonProperty("buildingNumber")
  public String getBuildingNumber() {
    return this.buildingNumber;
  }
  
  @JsonProperty("buildingNumber")
  public void setBuildingNumber(String buildingNumber) {
    this.buildingNumber = buildingNumber;
  }
  
  public PostalAddress withBuildingNumber(String buildingNumber) {
    this.buildingNumber = buildingNumber;
    return this;
  }
  
  @JsonProperty("streetName")
  public String getStreetName() {
    return this.streetName;
  }
  
  @JsonProperty("streetName")
  public void setStreetName(String streetName) {
    this.streetName = streetName;
  }
  
  public PostalAddress withStreetName(String streetName) {
    this.streetName = streetName;
    return this;
  }
  
  @JsonProperty("townName")
  public String getTownName() {
    return this.townName;
  }
  
  @JsonProperty("townName")
  public void setTownName(String townName) {
    this.townName = townName;
  }
  
  public PostalAddress withTownName(String townName) {
    this.townName = townName;
    return this;
  }
  
  @JsonProperty("postalCode")
  public String getPostalCode() {
    return this.postalCode;
  }
  
  @JsonProperty("postalCode")
  public void setPostalCode(String postalCode) {
    this.postalCode = postalCode;
  }
  
  public PostalAddress withPostalCode(String postalCode) {
    this.postalCode = postalCode;
    return this;
  }
  
  @JsonProperty("countrySubDivision")
  public String getCountrySubDivision() {
    return this.countrySubDivision;
  }
  
  @JsonProperty("countrySubDivision")
  public void setCountrySubDivision(String countrySubDivision) {
    this.countrySubDivision = countrySubDivision;
  }
  
  public PostalAddress withCountrySubDivision(String countrySubDivision) {
    this.countrySubDivision = countrySubDivision;
    return this;
  }
  
  @JsonProperty("country")
  public String getCountry() {
    return this.country;
  }
  
  @JsonProperty("country")
  public void setCountry(String country) {
    this.country = country;
  }
  
  public PostalAddress withCountry(String country) {
    this.country = country;
    return this;
  }
  
  public String toString() {
    return ToStringBuilder.reflectionToString(this);
  }
  
  public int hashCode() {
    return (new HashCodeBuilder()).append(this.type).append(this.primary).append(this.department).append(this.subDepartment).append(this.addressLine).append(this.buildingNumber).append(this.streetName).append(this.townName).append(this.postalCode).append(this.countrySubDivision).append(this.country).toHashCode();
  }
  
  public boolean equals(Object other) {
    if (other == this)
      return true; 
    if (!(other instanceof PostalAddress))
      return false; 
    PostalAddress rhs = (PostalAddress)other;
    return (new EqualsBuilder()).append(this.type, rhs.type).append(this.primary, rhs.primary).append(this.department, rhs.department).append(this.subDepartment, rhs.subDepartment).append(this.addressLine, rhs.addressLine).append(this.buildingNumber, rhs.buildingNumber).append(this.streetName, rhs.streetName).append(this.townName, rhs.townName).append(this.postalCode, rhs.postalCode).append(this.countrySubDivision, rhs.countrySubDivision).append(this.country, rhs.country).isEquals();
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
