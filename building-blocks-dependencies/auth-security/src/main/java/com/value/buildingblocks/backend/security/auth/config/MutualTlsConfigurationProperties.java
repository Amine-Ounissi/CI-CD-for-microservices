package com.value.buildingblocks.backend.security.auth.config;

import java.util.ArrayList;
import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "value.security.mtls")
public class MutualTlsConfigurationProperties {

  public static final String DEFAULT_INTEGRATION_API_PATH = "/integration-api/**";

  private boolean enabled = true;

  private boolean validateClient = false;

  private String subjectPrincipalRegex = "CN=(.*?)(?:,|$)";

  private List<TrustedClient> trustedClients = new ArrayList<>();

  private List<String> paths = new ArrayList<>();

  public MutualTlsConfigurationProperties() {
    this.paths.add("/integration-api/**");
  }

  public boolean isEnabled() {
    return this.enabled;
  }

  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }

  public boolean isValidateClient() {
    return this.validateClient;
  }

  public void setValidateClient(boolean validateClient) {
    this.validateClient = validateClient;
  }

  public List<TrustedClient> getTrustedClients() {
    return this.trustedClients;
  }

  public void setTrustedClients(List<TrustedClient> trustedClients) {
    this.trustedClients = trustedClients;
  }

  public String getSubjectPrincipalRegex() {
    return this.subjectPrincipalRegex;
  }

  public void setSubjectPrincipalRegex(String subjectPrincipalRegex) {
    this.subjectPrincipalRegex = subjectPrincipalRegex;
  }

  public List<String> getPaths() {
    return this.paths;
  }

  public void setPaths(List<String> paths) {
    this.paths = paths;
  }

  public static class TrustedClient {

    private String subject;

    public TrustedClient() {
    }

    public TrustedClient(String subject) {
      this.subject = subject;
    }

    public String getSubject() {
      return this.subject;
    }

    public void setSubject(String subject) {
      this.subject = subject;
    }
  }
}
