package com.value.authentication.tokenconverter.utils;

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
@Constraint(validatedBy = {SignatureValidator.class})
public @interface ValidSignature {
  String message() default "{com.value.authentication.tokenconverter.utils.ValidSignature.message}";
  
  Class<?>[] groups() default {};
  
  Class<? extends Payload>[] payload() default {};
}
