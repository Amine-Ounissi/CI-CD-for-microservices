package com.value.buildingblocks.backend.api.exceptionhandler;

import com.value.buildingblocks.backend.api.config.ApiProperties;
import com.value.buildingblocks.backend.security.auth.AuthLevel;
import com.value.buildingblocks.backend.security.auth.AuthStatus;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.http.HttpStatus;
import org.springframework.web.context.request.WebRequest;

public class SecureErrorAttributes extends DefaultErrorAttributes {

  private static final Logger log = LoggerFactory.getLogger(SecureErrorAttributes.class);

  private static final String ERROR_ATTRIBUTE = DefaultErrorAttributes.class.getName() + ".ERROR";

  private final ApiProperties apiProperties;

  public SecureErrorAttributes(ApiProperties apiProperties) {
    this.apiProperties = apiProperties;
  }

  public Map<String, Object> getErrorAttributes(WebRequest requestAttributes,
    ErrorAttributeOptions options) {
    Map<String, Object> errorAttributes = super.getErrorAttributes(requestAttributes, options);
    Throwable throwable = getThrowable(requestAttributes);
    if (options.isIncluded(ErrorAttributeOptions.Include.MESSAGE)) {
      HttpStatus status = getStatus(errorAttributes);
      if (this.apiProperties.isOverride5xxErrorMessages() && status.is5xxServerError()) {
        String newMessage = status.getReasonPhrase();
        String oldMessage = String.valueOf(errorAttributes.put("message", newMessage));
        log.info("Replacing message [{}] with generic message [{}]", oldMessage, newMessage);
      }
    }
    if (throwable instanceof AuthStatus) {
      AuthLevel level = ((AuthStatus) throwable).getAuthLevel();
      if (level != null) {
        errorAttributes.put("auth_level", level.toString());
        log.info("Auth level [{}] added to the error response object", level);
      }
    }
    return errorAttributes;
  }

  private Throwable getThrowable(WebRequest requestAttributes) {
    Throwable throwable = (Throwable) requestAttributes.getAttribute(ERROR_ATTRIBUTE, 0);
    if (throwable == null) {
      throwable = (Throwable) requestAttributes.getAttribute("javax.servlet.error.exception", 0);
    }
    return throwable;
  }

  private HttpStatus getStatus(Map<String, Object> errorAttributes) {
    int status = 500;
    Object attribute = errorAttributes.get("status");
    if (attribute instanceof Integer) {
      status = ((Integer) attribute).intValue();
    }
    try {
      return HttpStatus.valueOf(status);
    } catch (IllegalArgumentException e) {
      return HttpStatus.INTERNAL_SERVER_ERROR;
    }
  }
}
