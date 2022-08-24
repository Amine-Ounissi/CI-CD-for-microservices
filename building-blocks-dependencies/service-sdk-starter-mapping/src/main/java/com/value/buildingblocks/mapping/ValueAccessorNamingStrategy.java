package com.value.buildingblocks.mapping;

import javax.lang.model.element.ExecutableElement;
import org.mapstruct.ap.spi.DefaultAccessorNamingStrategy;
import org.mapstruct.ap.spi.util.IntrospectorUtils;

public class ValueAccessorNamingStrategy extends DefaultAccessorNamingStrategy {
  public String getPropertyName(ExecutableElement getterOrSetterMethod) {
    String methodName = getterOrSetterMethod.getSimpleName().toString();
    if (methodName.startsWith("with"))
      return IntrospectorUtils.decapitalize(methodName.substring(4)); 
    return super.getPropertyName(getterOrSetterMethod);
  }
}
