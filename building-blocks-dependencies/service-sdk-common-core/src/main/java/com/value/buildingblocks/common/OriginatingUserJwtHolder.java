package com.value.buildingblocks.common;

import com.value.buildingblocks.jwt.internal.token.InternalJwt;
import java.util.Optional;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

public final class OriginatingUserJwtHolder {
  private OriginatingUserJwtHolder() {
    throw new AssertionError("No OriginatingUserJwtHolder instances for you!");
  }
  
  public static Optional<InternalJwt> getOriginatingUserJwt() {
    RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
    if (attributes != null) {
      Object attribute = attributes.getAttribute("com.value.buildingblocks.backend.security.auth.config.SecurityContextUtil.ORIGINATING_USER_JWT", 0);
      if (attribute instanceof Optional) {
        Optional<InternalJwt> optionalAttribute = (Optional)attribute;
        if (optionalAttribute.isPresent()) {
          Object wrappedAttribute = optionalAttribute.get();
          if (wrappedAttribute instanceof InternalJwt)
            return optionalAttribute; 
        } 
      } 
    } 
    return Optional.empty();
  }
  
  public static void setOriginatingUserJwt(InternalJwt internalJwt) {
    RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
    if (attributes != null) {
      attributes.setAttribute("com.value.buildingblocks.backend.security.auth.config.SecurityContextUtil.ORIGINATING_USER_JWT", Optional.ofNullable(internalJwt), 0);
    } else {
      throw new IllegalStateException("No RequestAttributes, is this running in a thread which has not inherited the context?");
    } 
  }
  
  public static void clearContext() {
    RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
    if (attributes != null)
      attributes.removeAttribute("com.value.buildingblocks.backend.security.auth.config.SecurityContextUtil.ORIGINATING_USER_JWT", 0);
  }
}
