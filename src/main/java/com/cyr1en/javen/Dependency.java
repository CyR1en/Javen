/*
 * MIT License
 *
 * Copyright (c) 2019 Ethan Bacurio
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.cyr1en.javen;

import java.util.Objects;

public class Dependency {

  private String groupId;
  private String artifactId;
  private String version;
  private String directURL;

  public Dependency(String group, String name, String version) {
    this(group, name, version, null);
  }

  public Dependency(String group, String name, String version, String directURL) {
    this.groupId = group;
    this.artifactId = name;
    this.version = version;
    this.directURL = directURL == null ? URLResolver.UNRESOLVED : directURL;
  }

  public String asURL() {
    StringBuilder builder = new StringBuilder();
    String formattedGroup = groupId.replaceAll("\\.", "/");
    String artifactName = artifactId + "-" + version;
    builder.append(formattedGroup).append("/");
    builder.append(artifactId).append("/");
    builder.append(version).append("/").append(artifactName).append(".jar");
    return builder.toString();
  }

  public String asJarName() {
    return getArtifactId() + "-" + getVersion() + ".jar";
  }

  public String getGroupId() {
    return groupId;
  }

  public String getArtifactId() {
    return artifactId;
  }

  public String getVersion() {
    return version;
  }

  public String getDirectURL() {
    return directURL;
  }

  public String getCanonicalName() {
    return getGroupId() + ":" + getArtifactId() + ":" + getVersion();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Dependency)) return false;
    Dependency that = (Dependency) o;
    return Objects.equals(groupId, that.groupId) &&
            Objects.equals(artifactId, that.artifactId) &&
            Objects.equals(version, that.version) &&
            Objects.equals(directURL, that.directURL);
  }

  @Override
  public int hashCode() {
    return Objects.hash(groupId, artifactId, version);
  }

  @Override
  public String toString() {
    return "Dependency{" +
            "groupId='" + groupId + '\'' +
            ", artifactId='" + artifactId + '\'' +
            ", version='" + version + '\'' +
            ", directURL='" + directURL + '\'' +
            '}';
  }
}
