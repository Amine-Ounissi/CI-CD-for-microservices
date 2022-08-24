package com.value.authentication.tokenconverter.utils;

import com.value.authentication.tokenconverter.configuration.TokenConverterProperties;
import java.util.List;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ClaimValueValidationValidator implements ConstraintValidator<ValidValueValidation, TokenConverterProperties> {
  public void initialize(ValidValueValidation constraintAnnotation) {}
  
  public boolean isValid(TokenConverterProperties properties, ConstraintValidatorContext constraintValidatorContext) {
    TokenConverterProperties.ClaimValueValidation claimValueValidation = properties.getClaimValueValidation();
    if (claimValueValidation == null || !claimValueValidation.isEnabled())
      return true; 
    List<TokenConverterProperties.ClaimValueValidation.ClaimDenyEntry> claimDenyEntries = claimValueValidation.getClaimDenyEntries();
    if (claimValueValidation.isEnabled() && claimDenyEntries.isEmpty())
      return false; 
    return claimDenyEntries.stream()
      .allMatch(TokenConverterProperties.ClaimValueValidation.ClaimDenyEntry::isValid);
  }
}
