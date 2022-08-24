package com.value.buildingblocks.backend.validation;

import org.apache.commons.validator.routines.checkdigit.IBANCheckDigit;

public class IbanValidator {
  private IBANCheckDigit ibanCheckDigit = new IBANCheckDigit();
  
  public boolean isValid(String iban) {
    if (iban == null || iban.isEmpty())
      return false; 
    String ibanWithNoSpaces = iban.replaceAll("\\s", "");
    return this.ibanCheckDigit.isValid(ibanWithNoSpaces);
  }
}
