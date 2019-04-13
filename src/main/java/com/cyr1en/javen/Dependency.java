package com.cyr1en.javen;

public class Dependency {

  private String group;
  private String name;
  private String version;

  public Dependency(String group, String name, String version) {
    this.group = group;
    this.name = name;
    this.version = version;
  }

  public String asURL() {
    StringBuilder builder = new StringBuilder();
    String formattedGroup = group.replaceAll("\\.", "/");
    String artifactName = name + "-" + version;
    builder.append(formattedGroup).append("/");
    builder.append(name).append("/");
    builder.append(version).append("/").append(artifactName).append(".jar");
    return builder.toString();
  }

  public String asJarName() {
    return getName() + "-" + getVersion() + ".jar";
  }

  public String getGroup() {
    return group;
  }

  public String getName() {
    return name;
  }

  public String getVersion() {
    return version;
  }

  @Override
  public String toString() {
    String fullName = getGroup() + ":" + getName() + ":" + getVersion();
    return this.getClass().getSimpleName() + "[" + fullName + "]";
  }
}
