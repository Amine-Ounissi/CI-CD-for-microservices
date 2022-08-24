package com.value.buildingblocks.backend.api.validation;

import java.util.HashSet;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;

public final class ValidationUtils {

  public static Set<ConstraintViolation<Object>> validateObject(Object o) {
    if (o != null) {
      return Validation.buildDefaultValidatorFactory().getValidator().validate(o, new Class[0]);
    }
    return new HashSet<>();
  }

  private ValidationUtils() {
    throw new AssertionError("Thou shalt not instantiate " + getClass().getName());
  }
}
