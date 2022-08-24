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
@JsonPropertyOrder({"phone-addresses"})
@JsonIgnoreProperties(ignoreUnknown = true)
public class GetPhoneAddresses implements AdditionalPropertiesAware {
  @JsonProperty("phone-addresses")
  @Valid
  private List<GetPhoneAddress> phoneAddresses = new ArrayList<>();
  
  @JsonInclude(JsonInclude.Include.NON_EMPTY)
  private Map<String, String> additions = new HashMap<>();
  
  @JsonProperty("phone-addresses")
  public List<GetPhoneAddress> getPhoneAddresses() {
    return this.phoneAddresses;
  }
  
  @JsonProperty("phone-addresses")
  public void setPhoneAddresses(List<GetPhoneAddress> phoneAddresses) {
    this.phoneAddresses = phoneAddresses;
  }
  
  public GetPhoneAddresses withPhoneAddresses(List<GetPhoneAddress> phoneAddresses) {
    this.phoneAddresses = phoneAddresses;
    return this;
  }
  
  public String toString() {
    return ToStringBuilder.reflectionToString(this);
  }
  
  public int hashCode() {
    return (new HashCodeBuilder()).append(this.phoneAddresses).toHashCode();
  }
  
  public boolean equals(Object other) {
    if (other == this)
      return true; 
    if (!(other instanceof GetPhoneAddresses))
      return false; 
    GetPhoneAddresses rhs = (GetPhoneAddresses)other;
    return (new EqualsBuilder()).append(this.phoneAddresses, rhs.phoneAddresses).isEquals();
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
