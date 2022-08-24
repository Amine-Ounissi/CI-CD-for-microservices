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
@JsonPropertyOrder({"postal-addresses"})
@JsonIgnoreProperties(ignoreUnknown = true)
public class GetPostalAddresses implements AdditionalPropertiesAware {
  @JsonProperty("postal-addresses")
  @Valid
  private List<GetPostalAddress> postalAddresses = new ArrayList<>();
  
  @JsonInclude(JsonInclude.Include.NON_EMPTY)
  private Map<String, String> additions = new HashMap<>();
  
  @JsonProperty("postal-addresses")
  public List<GetPostalAddress> getPostalAddresses() {
    return this.postalAddresses;
  }
  
  @JsonProperty("postal-addresses")
  public void setPostalAddresses(List<GetPostalAddress> postalAddresses) {
    this.postalAddresses = postalAddresses;
  }
  
  public GetPostalAddresses withPostalAddresses(List<GetPostalAddress> postalAddresses) {
    this.postalAddresses = postalAddresses;
    return this;
  }
  
  public String toString() {
    return ToStringBuilder.reflectionToString(this);
  }
  
  public int hashCode() {
    return (new HashCodeBuilder()).append(this.postalAddresses).toHashCode();
  }
  
  public boolean equals(Object other) {
    if (other == this)
      return true; 
    if (!(other instanceof GetPostalAddresses))
      return false; 
    GetPostalAddresses rhs = (GetPostalAddresses)other;
    return (new EqualsBuilder()).append(this.postalAddresses, rhs.postalAddresses).isEquals();
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
