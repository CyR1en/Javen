package com.cyr1en.javen.test;

import com.cyr1en.javen.Dependency;
import com.cyr1en.javen.Javen;
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
  private Dependency targetDep;

  @Before
  public void before() {
    Path libsDir = Paths.get("src/test/resources/testLibDir");
    javen = new Javen(libsDir);
    javen.addRepository("jcenter", "https://jcenter.bintray.com");
    targetDep = new Dependency("com.google.guava", "guava", "27.1-jre");
  }

  @Test
  public void testRequestedDeps() {
    List<Dependency> requested = javen.findAllRequestedDeps();
    Assertions.assertThat(requested.size() > 0).isTrue();
    Assertions.assertThat(requested.get(0).getName()).isEqualTo("guava");
  }

  @Test
  public void testGetDepsToDownload() {
    Map<Dependency, URL> needToDownload = javen.getDepsToDownload();
    Assertions.assertThat(needToDownload.size() > 0).isTrue();
    Assertions.assertThat(needToDownload.get(targetDep)).isNotNull();
  }

  @Test
  public void testDownloadNeededDeps() {
    Assertions.assertThatCode(() -> javen.downloadNeededDeps()).doesNotThrowAnyException();
    Assertions.assertThat(javen.getLibsDir().containsDependency(targetDep)).isTrue();
  }

  @After
  public void after() {
    javen.getLibsDir().deleteDependency(targetDep);
  }

  @Lib(group = "com.google.guava", name = "guava", version = "27.1-jre")
  @Lib(group = "net.dv8tion", name = "JDA", version = "3.8.3_460")
  private class TestClass {

  }
}
