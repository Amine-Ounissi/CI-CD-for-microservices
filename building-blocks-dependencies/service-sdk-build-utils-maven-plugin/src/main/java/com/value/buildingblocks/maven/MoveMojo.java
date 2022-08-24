package com.value.buildingblocks.maven;

import java.io.IOException;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.FileAttribute;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectHelper;

@Mojo(name = "move-file", threadSafe = true)
public class MoveMojo extends AbstractMojo {

  @Parameter(property = "movefile.source", required = true)
  private String source;

  @Parameter(property = "movefile.dest", required = true)
  private String dest;

  @Parameter(property = "movefile.attach", defaultValue = "false")
  private boolean attach = false;

  @Parameter(property = "movefile.fail.if.missing", defaultValue = "true")
  private boolean failIfMissing = true;

  @Parameter(defaultValue = "${project}", readonly = true, required = true)
  private MavenProject project;

  @Parameter(property = "movefile.skip", defaultValue = "false")
  boolean skip = false;

  @Component
  private MavenProjectHelper projectHelper;

  @Parameter(property = "movefile.skip", defaultValue = "false")
  boolean copy = false;

  public void execute() throws MojoExecutionException, MojoFailureException {
    if (isSkip()) {
      getLog().info("Skipped");
      return;
    }
    if (StringUtils.isEmpty(getSource())) {
      throw new MojoExecutionException("Source file not set.");
    }
    if (StringUtils.isEmpty(getDest())) {
      throw new MojoExecutionException("Destination file not set.");
    }
    Path targetRootPath = Paths.get(this.project.getBuild().getDirectory(), new String[0]);
    Path sourcePath = targetRootPath.resolve(getSource());
    Path destPath = targetRootPath.resolve(getDest());
    if (!sourcePath.toFile().exists()) {
      if (isFailIfMissing()) {
        throw new MojoExecutionException("source '" + sourcePath + "' does not exist");
      }
      getLog().info("" + sourcePath + " to " + sourcePath + ": Source file not found");
      return;
    }
    try {
      Files.createDirectories(destPath.getParent(), (FileAttribute<?>[]) new FileAttribute[0]);
    } catch (IOException e) {
      throw new MojoExecutionException("Cannot create dest directory", e);
    }
    if (isCopy()) {
      copyFile(sourcePath, destPath);
    } else {
      moveFile(sourcePath, destPath);
    }
    if (isAttach()) {
      attachArtifact(destPath);
    }
  }

  private void copyFile(Path sourcePath, Path destPath) throws MojoFailureException {
    try {
      Files.copy(sourcePath, destPath, new CopyOption[]{StandardCopyOption.REPLACE_EXISTING});
      getLog().info("Copied " + sourcePath + " to " + destPath);
    } catch (IOException ex) {
      String message = "Failed to copy " + sourcePath + " to " + destPath + ": " + ex.getMessage();
      throw new MojoFailureException(message, ex);
    }
  }

  private void moveFile(Path sourcePath, Path destPath) throws MojoFailureException {
    try {
      Files.move(sourcePath, destPath, new CopyOption[]{StandardCopyOption.REPLACE_EXISTING});
      getLog().info("Moved " + sourcePath + " to " + destPath);
    } catch (IOException ex) {
      String message = "Failed to move " + sourcePath + " to " + destPath + ": " + ex.getMessage();
      throw new MojoFailureException(message, ex);
    }
  }

  private void attachArtifact(Path file) throws MojoExecutionException {
    getLog().info("Attempting to attach file " + file);
    String filename = file.getFileName().toString();
    String extension = FilenameUtils.getExtension(filename);
    if (extension.isEmpty()) {
      String message =
        "Unable to determine package of filename " + filename + ": File has no extension";
      throw new MojoExecutionException(message);
    }
    String version = this.project.getVersion();
    String classifier = null;
    int versionPosition = filename.lastIndexOf(version);
    if (versionPosition < 0) {
      String message = "Unable to extract classifier from filename " + filename
        + ": Project version not found in filename";
      getLog().info(message);
    } else {
      int classifierStart = versionPosition + version.length() + 1;
      int filenameEnd = filename.length() - extension.length() + 1;
      if (classifierStart >= filenameEnd) {
        String message = "Could not find classifier in filename " + filename;
        getLog().info(message);
      } else {
        classifier = filename.substring(classifierStart, filenameEnd);
      }
    }
    this.projectHelper
      .attachArtifact(this.project, extension.toLowerCase(), classifier, file.toFile());
  }

  public void setSource(String source) {
    this.source = source;
  }

  public String getSource() {
    return this.source;
  }

  public String getDest() {
    return this.dest;
  }

  public MavenProject getProject() {
    return this.project;
  }

  public void setDest(String dest) {
    this.dest = dest;
  }

  public void setProjectHelper(MavenProjectHelper projectHelper) {
    this.projectHelper = projectHelper;
  }

  public boolean isSkip() {
    return this.skip;
  }

  public void setSkip(boolean skip) {
    this.skip = skip;
  }

  public boolean isFailIfMissing() {
    return this.failIfMissing;
  }

  public void setFailIfMissing(boolean failIfMissing) {
    this.failIfMissing = failIfMissing;
  }

  public boolean isAttach() {
    return this.attach;
  }

  public void setAttach(boolean attach) {
    this.attach = attach;
  }

  public void setProject(MavenProject project) {
    this.project = project;
  }

  public MavenProjectHelper getProjectHelper() {
    return this.projectHelper;
  }

  public boolean isCopy() {
    return this.copy;
  }

  public void setCopy(boolean copy) {
    this.copy = copy;
  }
}
