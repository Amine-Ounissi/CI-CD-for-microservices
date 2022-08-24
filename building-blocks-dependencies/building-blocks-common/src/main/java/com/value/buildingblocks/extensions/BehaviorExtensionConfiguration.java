package com.value.buildingblocks.extensions;

import java.util.HashMap;
import java.util.Map;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("value")
public class BehaviorExtensionConfiguration {
  private final Map<String, BehaviorExtensionToggle> behaviorExtensions = new HashMap<>();
  
  public Map<String, BehaviorExtensionToggle> getBehaviorExtensions() {
    return this.behaviorExtensions;
  }
  
  public String toString() {
    StringBuilder mapAsString = new StringBuilder("{");
    for (Map.Entry<String, BehaviorExtensionToggle> entry : this.behaviorExtensions.entrySet())
      mapAsString.append((new StringBuilder()).append(entry.getKey()).append("=").append(entry.getValue()).append(", ").toString()); 
    return "BehaviorExtensionConfiguration{behaviorExtensions=[" + mapAsString
      .toString() + "]}";
  }
  
  public static class BehaviorExtensionToggle {
    private boolean enabled = true;
    
    public boolean isEnabled() {
      return this.enabled;
    }
    
    public void setEnabled(boolean enabled) {
      this.enabled = enabled;
    }
    
    public String toString() {
      return String.valueOf(this.enabled);
    }
  }
}
