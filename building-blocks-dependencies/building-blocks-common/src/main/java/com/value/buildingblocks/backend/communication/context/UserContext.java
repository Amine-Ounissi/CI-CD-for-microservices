package com.value.buildingblocks.backend.communication.context;

public class UserContext {
  private final String serviceAgreementId;
  
  private final String legalEntityId;
  
  public UserContext(String serviceAgreementId, String legalEntityId) {
    this.serviceAgreementId = serviceAgreementId;
    this.legalEntityId = legalEntityId;
  }
  
  public String getServiceAgreementId() {
    return this.serviceAgreementId;
  }
  
  public String getLegalEntityId() {
    return this.legalEntityId;
  }
  
  public String toString() {
    return "UserContext [serviceAgreementId=" + this.serviceAgreementId + ", legalEntityId=" + this.legalEntityId + "]";
  }
}
