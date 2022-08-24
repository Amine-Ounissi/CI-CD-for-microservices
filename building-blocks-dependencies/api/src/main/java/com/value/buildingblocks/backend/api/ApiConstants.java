package com.value.buildingblocks.backend.api;

public final class ApiConstants {

  public static final String API_ERRORS_PREFIX = "value.api.errors";

  public static final int HTTP_MEDIA_TYPE_NOT_ACCEPTABLE_EXCEPTION_HANDLER_ORDER = 5;

  public static final int CONVERSION_FAILED_EXCEPTION_HANDLER_ORDER = 7;

  public static final int BINDING_RESULT_ERRORS_EXCEPTION_HANDLER_ORDER = 10;

  public static final int CONSTRAINT_VIOLATION_EXCEPTION_HANDLER_ORDER = 13;

  public static final int METHOD_ARGUMENT_NOT_VALID_EXCEPTION_HANDLER_ORDER = 20;

  public static final int HTTP_MESSAGE_CONVERSION_EXCEPTION_HANDLER_ORDER = 25;

  public static final int API_EXCEPTION_HANDLER_ORDER = 30;

  public static final int NON_API_EXCEPTION_HANDLER_ORDER = 40;

  private ApiConstants() {
    throw new AssertionError("Thou shalt not instantiate " + getClass().getName());
  }
}
