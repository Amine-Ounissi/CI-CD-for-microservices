package com.value.buildingblocks.backend.validation;

import com.value.buildingblocks.persistence.model.AdditionalPropertiesAware;
import javax.validation.ConstraintValidator;

public interface AdditionalPropertiesValidator extends ConstraintValidator<AdditionalProperties, AdditionalPropertiesAware> {}
