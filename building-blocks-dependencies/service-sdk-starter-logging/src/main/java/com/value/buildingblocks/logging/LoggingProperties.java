package com.value.buildingblocks.logging;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "value.logging")
public class LoggingProperties {
  private CrlfProtectionProperties crlfProtection = new CrlfProtectionProperties();

  public CrlfProtectionProperties getCrlfProtection() {
    return this.crlfProtection;
  }

  public void setCrlfProtection(CrlfProtectionProperties crlfProtection) {
    this.crlfProtection = crlfProtection;
  }

  static class CrlfProtectionProperties {
    private boolean enabled = true;

    private String target = "\n";

    private String replacement = "\n+[ ";

    public boolean isEnabled() {
      return this.enabled;
    }

    public void setEnabled(boolean enabled) {
      this.enabled = enabled;
    }

    public String getTarget() {
      return this.target;
    }

    public void setTarget(String target) {
      this.target = target;
    }

    public String getReplacement() {
      return this.replacement;
    }

    public void setReplacement(String replacement) {
      this.replacement = replacement;
    }
  }
}
