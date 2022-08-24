package com.value.authentication.tokenconverter.validator;

import com.value.authentication.tokenconverter.config.Oauth2TokenConverterConfig;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import org.apache.commons.lang3.StringUtils;

public class ConfigurationValidator implements ConstraintValidator<ValidVerificationMethod, Oauth2TokenConverterConfig> {
  public void initialize(ValidVerificationMethod constraintAnnotation) {}
  
  public boolean isValid(Oauth2TokenConverterConfig config, ConstraintValidatorContext constraintValidatorContext) {
    if (!validVerificationMethod(config))
      return false; 
    return config.isJwksVerification() ? (
      !StringUtils.isEmpty(config.getJwksUsernameClaimName())) : (
      !StringUtils.isEmpty(config.getUserinfoUsernameClaimName()));
  }
  
  private boolean validVerificationMethod(Oauth2TokenConverterConfig config) {
    return (config.isUserinfoVerification() || config.isJwksVerification());
  }
}
