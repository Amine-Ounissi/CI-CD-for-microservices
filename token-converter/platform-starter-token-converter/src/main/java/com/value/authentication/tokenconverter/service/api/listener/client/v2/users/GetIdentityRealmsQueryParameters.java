package com.value.authentication.tokenconverter.service.api.listener.client.v2.users;

public class GetIdentityRealmsQueryParameters {
  private String legalEntityId;
  
  public String getLegalEntityId() {
    return this.legalEntityId;
  }
  
  public void setLegalEntityId(String legalEntityId) {
    this.legalEntityId = legalEntityId;
  }
  
  public GetIdentityRealmsQueryParameters withLegalEntityId(String legalEntityId) {
    this.legalEntityId = legalEntityId;
    return this;
  }
}
