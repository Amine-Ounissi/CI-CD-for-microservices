package com.value.buildingblocks.maven;

import java.io.File;
import java.io.IOException;
import org.apache.commons.lang3.StringUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.springframework.boot.loader.tools.MainClassFinder;

@Mojo(name = "find-main-class", defaultPhase = LifecyclePhase.PREPARE_PACKAGE)
public class FindMainClassMojo extends AbstractMojo {

  @Parameter(defaultValue = "${project}", readonly = true)
  private MavenProject project;

  @Parameter(property = "findMainClass.annotationName", defaultValue = "org.springframework.boot.autoconfigure.SpringBootApplication")
  private String annotationName = "org.springframework.boot.autoconfigure.SpringBootApplication";

  @Parameter(property = "findMainClass.propertyName", defaultValue = "main-class")
  private String propertyName = "main-class";

  @Parameter(property = "findMainClass.skip", defaultValue = "false")
  private boolean skip = false;

  @Parameter(defaultValue = "${project.build.outputDirectory}", required = true)
  private File classesDirectory;

  public void execute() throws MojoExecutionException, MojoFailureException {
    if (isSkip()) {
      getLog().info("Skipped");
      return;
    }
    if (StringUtils.isBlank(this.project.getProperties().getProperty(this.propertyName))) {
      try {
        String mainClass = MainClassFinder
          .findSingleMainClass(this.classesDirectory, this.annotationName);
        if (!StringUtils.isEmpty(mainClass)) {
          this.project.getProperties().setProperty(this.propertyName, mainClass);
          getLog().info(
            String.format("\"%s\" is set to \"%s\"", new Object[]{this.propertyName, this.project
              .getProperties().get(this.propertyName)}));
        } else {
          getLog().warn("No main class found!");
        }
      } catch (IOException e) {
        throw new MojoExecutionException("Cannot find main class", e);
      }
    } else {
      getLog().info(String
        .format("Skipped. \"%s\" was set to \"%s\"", new Object[]{this.propertyName, this.project
          .getProperties().get(this.propertyName)}));
    }
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

  public File getClassesDirectory() {
    return this.classesDirectory;
  }

  public void setClassesDirectory(File classesDirectory) {
    this.classesDirectory = classesDirectory;
  }

  public String getAnnotationName() {
    return this.annotationName;
  }

  public void setAnnotationName(String annotationName) {
    this.annotationName = annotationName;
  }
}
