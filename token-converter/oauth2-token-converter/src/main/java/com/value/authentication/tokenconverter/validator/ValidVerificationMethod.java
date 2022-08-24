package com.value.authentication.tokenconverter.validator;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.Payload;

@Documented
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {ConfigurationValidator.class})
public @interface ValidVerificationMethod {
  String message() default "{com.value.authentication.tokenconverter.validator.ValidVerificationMethod.message}";
  
  Class<?>[] groups() default {};
  
  Class<? extends Payload>[] payload() default {};
}
