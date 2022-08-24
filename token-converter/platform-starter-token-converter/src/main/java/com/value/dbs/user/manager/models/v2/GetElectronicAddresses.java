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
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"electronic-addresses"})
@JsonIgnoreProperties(ignoreUnknown = true)
public class GetElectronicAddresses implements AdditionalPropertiesAware {
  @JsonProperty("electronic-addresses")
  @Valid
  private List<GetElectronicAddress> electronicAddresses = new ArrayList<>();
  
  @JsonInclude(JsonInclude.Include.NON_EMPTY)
  private Map<String, String> additions = new HashMap<>();
  
  @JsonProperty("electronic-addresses")
  public List<GetElectronicAddress> getElectronicAddresses() {
    return this.electronicAddresses;
  }
  
  @JsonProperty("electronic-addresses")
  public void setElectronicAddresses(List<GetElectronicAddress> electronicAddresses) {
    this.electronicAddresses = electronicAddresses;
  }
  
  public GetElectronicAddresses withElectronicAddresses(List<GetElectronicAddress> electronicAddresses) {
    this.electronicAddresses = electronicAddresses;
    return this;
  }
  
  public String toString() {
    return ToStringBuilder.reflectionToString(this);
  }
  
  public int hashCode() {
    return (new HashCodeBuilder()).append(this.electronicAddresses).toHashCode();
  }
  
  public boolean equals(Object other) {
    if (other == this)
      return true; 
    if (!(other instanceof GetElectronicAddresses))
      return false; 
    GetElectronicAddresses rhs = (GetElectronicAddresses)other;
    return (new EqualsBuilder()).append(this.electronicAddresses, rhs.electronicAddresses).isEquals();
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
