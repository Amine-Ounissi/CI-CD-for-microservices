package com.value.authentication.tokenconverter.utils;

import com.value.authentication.tokenconverter.configuration.TokenConverterProperties;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import org.springframework.util.StringUtils;

public class SignatureValidator implements ConstraintValidator<ValidSignature, TokenConverterProperties> {
  public void initialize(ValidSignature constraintAnnotation) {}
  
  public boolean isValid(TokenConverterProperties properties, ConstraintValidatorContext constraintValidatorContext) {
    return ((properties.getJwt().getSignatureMode().equalsIgnoreCase("symmetric-key") && 
      checkSymmetricKeyConfigCorrect(properties)) || (properties
      .getJwt().getSignatureMode().equalsIgnoreCase("asymmetric-key-pair") && 
      checkAsymmetricKeyConfigCorrect(properties)));
  }
  
  private boolean checkSymmetricKeyConfigCorrect(TokenConverterProperties properties) {
    return !StringUtils.isEmpty(properties.getJwt().getSigningKey());
  }
  
  private boolean checkAsymmetricKeyConfigCorrect(TokenConverterProperties properties) {
    return (!StringUtils.isEmpty(properties.getJwt().getKeyPair().getLocation()) && 
      !StringUtils.isEmpty(properties.getJwt().getKeyPair().getPassword()) && 
      !StringUtils.isEmpty(properties.getJwt().getKeyPair().getAlias()));
  }
}
