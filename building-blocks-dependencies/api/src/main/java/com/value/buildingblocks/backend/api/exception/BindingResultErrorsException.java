package com.value.buildingblocks.backend.api.exception;

import org.springframework.validation.BindingResult;

public class BindingResultErrorsException extends RuntimeException {

  private static final long serialVersionUID = 8119143833173619499L;

  private BindingResult bindingResult;

  public BindingResultErrorsException(BindingResult bindingResult) {
    super("Binding Result Errors");
    this.bindingResult = bindingResult;
  }

  public BindingResult getBindingResult() {
    return this.bindingResult;
  }

  public void setBindingResult(BindingResult bindingResult) {
    this.bindingResult = bindingResult;
  }
}
