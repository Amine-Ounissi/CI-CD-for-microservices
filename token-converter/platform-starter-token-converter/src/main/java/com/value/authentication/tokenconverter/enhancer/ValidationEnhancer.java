package com.value.authentication.tokenconverter.enhancer;

import com.value.authentication.tokenconverter.exception.TokenConverterValidationException;
import java.util.Map;

public interface ValidationEnhancer {
  void validate(Map<String, Object> paramMap) throws TokenConverterValidationException;
}
