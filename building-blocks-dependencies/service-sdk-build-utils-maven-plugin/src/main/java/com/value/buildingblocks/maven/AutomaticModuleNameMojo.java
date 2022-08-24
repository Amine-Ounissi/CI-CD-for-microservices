package com.value.buildingblocks.maven;

import java.io.File;
import javax.lang.model.SourceVersion;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

@Mojo(name = "automatic-module-class", defaultPhase = LifecyclePhase.INITIALIZE)
public class AutomaticModuleNameMojo extends AbstractMojo {

  @Parameter(defaultValue = "${project}", readonly = true)
  private MavenProject project;

  @Parameter(property = "automaticModuleName.propertyName", defaultValue = "Automatic-Module-Name")
  private String propertyName = "Automatic-Module-Name";

  @Parameter(property = "automaticModuleName.skip", defaultValue = "false")
  private boolean skip = false;

  public void execute() throws MojoExecutionException, MojoFailureException {
    if (isSkip()) {
      getLog().info("Skipped");
      return;
    }
    if (this.project.getProperties().containsKey(this.propertyName)) {
      getLog().info("Skipped - property already set");
      return;
    }
    if (!"jar".equalsIgnoreCase(this.project.getPackaging())) {
      getLog().info("Skipped - packaging is not jar");
      return;
    }
    for (String sourceRoot : this.project.getCompileSourceRoots()) {
      File f = new File(sourceRoot, "module-info.java");
      if (f.exists()) {
        getLog().info("Skipped - found " + f);
        return;
      }
    }
    String moduleName = this.project.getGroupId() + "." + this.project.getGroupId();
    if (!SourceVersion.isName(moduleName)) {
      getLog().info("Skipped - " + moduleName + " is not a valid identifier.");
      return;
    }
    this.project.getProperties().setProperty(this.propertyName, moduleName);
    getLog().info("Setting " + this.propertyName + "=" + moduleName);
  }

  private String sanitize(String artifactId) {
    return artifactId.replace("-", ".");
  }

  public MavenProject getProject() {
    return this.project;
  }

  public void setProject(MavenProject project) {
    this.project = project;
  }

  public String getPropertyName() {
    return this.propertyName;
  }

  public void setPropertyName(String propertyName) {
    this.propertyName = propertyName;
  }

  public boolean isSkip() {
    return this.skip;
  }

  public void setSkip(boolean overWrite) {
    this.skip = overWrite;
  }
}
