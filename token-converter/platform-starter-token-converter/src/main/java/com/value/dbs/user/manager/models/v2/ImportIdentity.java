package com.value.dbs.user.manager.models.v2;

import com.value.buildingblocks.persistence.model.AdditionalPropertiesAware;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.HashMap;
import java.util.Map;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"externalId", "legalEntityInternalId"})
@JsonIgnoreProperties(ignoreUnknown = true)
public class ImportIdentity implements AdditionalPropertiesAware {
  @JsonProperty("externalId")
  @Pattern(regexp = "^\\S(.*(\\S))?$")
  @Size(min = 1, max = 64)
  @NotNull
  private String externalId;
  
  @JsonProperty("legalEntityInternalId")
  @Pattern(regexp = "^\\S+$")
  @Size(min = 1, max = 36)
  @NotNull
  private String legalEntityInternalId;
  
  @JsonInclude(JsonInclude.Include.NON_EMPTY)
  private Map<String, String> additions = new HashMap<>();
  
  @JsonProperty("externalId")
  public String getExternalId() {
    return this.externalId;
  }
  
  @JsonProperty("externalId")
  public void setExternalId(String externalId) {
    this.externalId = externalId;
  }
  
  public ImportIdentity withExternalId(String externalId) {
    this.externalId = externalId;
    return this;
  }
  
  @JsonProperty("legalEntityInternalId")
  public String getLegalEntityInternalId() {
    return this.legalEntityInternalId;
  }
  
  @JsonProperty("legalEntityInternalId")
  public void setLegalEntityInternalId(String legalEntityInternalId) {
    this.legalEntityInternalId = legalEntityInternalId;
  }
  
  public ImportIdentity withLegalEntityInternalId(String legalEntityInternalId) {
    this.legalEntityInternalId = legalEntityInternalId;
    return this;
  }
  
  public String toString() {
    return ToStringBuilder.reflectionToString(this);
  }
  
  public int hashCode() {
    return (new HashCodeBuilder()).append(this.externalId).append(this.legalEntityInternalId).toHashCode();
  }
  
  public boolean equals(Object other) {
    if (other == this)
      return true; 
    if (!(other instanceof ImportIdentity))
      return false; 
    ImportIdentity rhs = (ImportIdentity)other;
    return (new EqualsBuilder()).append(this.externalId, rhs.externalId).append(this.legalEntityInternalId, rhs.legalEntityInternalId).isEquals();
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
