package com.value.buildingblocks.context;

import java.util.LinkedHashSet;
import java.util.Set;
import org.springframework.boot.context.properties.source.ConfigurationPropertyName;

final class ContextSupportUtil {
  private ContextSupportUtil() {
    throw new IllegalStateException("Private constructor!");
  }
  
  public static String[] deContextualisePropertyNames(ContextQualifier context, String[] originalNames) {
    if ((context.getContext()).length == 0)
      return originalNames; 
    Set<String> contextNames = new LinkedHashSet<>();
    for (String string : originalNames) {
      if (string.contains("@")) {
        for (String contextKey : context.getContext()) {
          if (string.endsWith(contextKey)) {
            contextNames.add(string.substring(0, string.length() - contextKey.length() - "@"
                  .length()));
            break;
          } 
        } 
      } else {
        contextNames.add(string);
      } 
    } 
    return contextNames.<String>toArray(new String[contextNames.size()]);
  }
  
  public static void checkName(String name) {
    if (name.contains("@"))
      ConfigurationPropertyName.of(name.substring(0, name.indexOf("@"))); 
  }
}
