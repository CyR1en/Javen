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

package com.cyr1en.javen.test;

import com.cyr1en.javen.Dependency;
import com.cyr1en.javen.Javen;
import com.cyr1en.javen.Repository;
import com.cyr1en.javen.annotation.Lib;
import org.assertj.core.api.Assertions;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

public class JavenTest {

  private Javen javen;

  private Repository jCenterRepo;

  private Dependency mavenCentralTarget;
  private Dependency jCenterTarget;

  @Before
  public void before() {
    Path libsDir = Paths.get("src/test/resources/testLibDir");
    javen = new Javen(libsDir);
    jCenterRepo = new Repository("jCenter", "https://jcenter.bintray.com/");
    javen.addRepository(jCenterRepo);
    mavenCentralTarget = new Dependency("com.google.guava", "guava", "27.1-jre");
    jCenterTarget = new Dependency("net.dv8tion", "JDA", "3.8.3_462");
  }

  @Test
  public void testContainsJCenter() {
    Assertions.assertThat(javen.getRepositories().contains(jCenterRepo)).isTrue();
  }

  @Test
  public void testRequestedIsDistinct() {
    List<Dependency> requested = javen.findAllRequestedDeps();
    Assertions.assertThat(requested.size()).isEqualTo(2);
  }

  @Test
  public void testRequestedDeps() {
    List<Dependency> requested = javen.findAllRequestedDeps();
    Assertions.assertThat(requested.contains(mavenCentralTarget) &&
            requested.contains(jCenterTarget)).isTrue();
  }

  @Test
  public void testGetDepsToDownload() {
    Map<Dependency, URL> needToDownload = javen.getDepsToDownload();
    System.out.println(needToDownload);
    Assertions.assertThat(needToDownload.size() > 0).isTrue();
    Assertions.assertThat(needToDownload.get(mavenCentralTarget)).isNotNull();
  }

  @Test
  public void testDownloadNeededDeps() {
    Assertions.assertThatCode(() -> javen.downloadNeededDeps()).doesNotThrowAnyException();
    Assertions.assertThat(javen.getLibsDir().containsDependency(mavenCentralTarget)).isTrue();
  }

  @After
  public void after() {
    javen.getLibsDir().deleteDependency(mavenCentralTarget);
    javen.getLibsDir().deleteDependency(jCenterTarget);
  }

  @Lib(group = "com.google.guava", name = "guava", version = "27.1-jre")
  @Lib(group = "com.google.guava", name = "guava", version = "27.1-jre")
  @Lib(group = "net.dv8tion", name = "JDA", version = "3.8.3_462")
  private class TestClass {

  }
}
