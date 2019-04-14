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
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Dependency)) return false;
    Dependency that = (Dependency) o;
    return Objects.equals(group, that.group) &&
            Objects.equals(name, that.name) &&
            Objects.equals(version, that.version);
  }

  @Override
  public int hashCode() {
    return Objects.hash(group, name, version);
  }

  @Override
  public String toString() {
    return this.getClass().getSimpleName() + "(" + asURL() + ")";
  }
}
