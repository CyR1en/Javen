package com.cyr1en.javen.test;

import com.cyr1en.javen.Dependency;
import com.cyr1en.javen.LibDirectory;
import org.assertj.core.api.Assertions;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.atomic.AtomicReference;
import java.util.jar.JarFile;

public class LibDirectoryTest {

  private LibDirectory defaultLib;
  private Dependency validDependency;
  private Dependency invalidDependency;
  private JarFile[] emptyJarArray;

  @Before
  public void before() {
    defaultLib = new LibDirectory();
    validDependency = new Dependency("com.github.cyr1en", "flatdb", "1.0.5");
    invalidDependency = new Dependency("com.github.cyr1en", "test-lib", "0.0.1");
    emptyJarArray = new JarFile[0];
  }

  @Test
  public void testEmpty() {
    Assert.assertTrue(defaultLib.exists());
    Assert.assertTrue(defaultLib.isDirectory());
    Assert.assertArrayEquals(defaultLib.listJarFiles(), emptyJarArray);
  }

  @Test
  public void testFalseConstruct() {
    Assertions.assertThatCode(() -> new LibDirectory("src/test/resources/DummyFile"))
            .isInstanceOf(IllegalStateException.class)
            .hasNoCause();
  }

  @Test
  public void testExistingDir() {
    AtomicReference<LibDirectory> testLib = new AtomicReference<>();
    Assertions.assertThatCode(() -> testLib.set(new LibDirectory("src/test/resources/testLibDir")))
            .doesNotThrowAnyException();
    LibDirectory nonAtomic = testLib.get();
    Assertions.assertThat(nonAtomic).isNotNull();
    Assertions.assertThat(nonAtomic.listJarFiles().length).isEqualTo(2);
    Assertions.assertThat(nonAtomic.containsDependency(validDependency)).isTrue();

    Assertions.assertThat(nonAtomic.containsDiffVersionOf(validDependency)).isTrue();
    Assertions.assertThat(nonAtomic.listJarFilesMatching(validDependency).length > 1).isTrue();

    Dependency oldDependency = new Dependency("com.github.cyr1en", "flatdb", "1.0.4");
    Assertions.assertThatCode(() -> nonAtomic.removeOldVersionOf(validDependency)).doesNotThrowAnyException();
    Assertions.assertThat(nonAtomic.containsDependency(oldDependency)).isFalse();
  }

  @Test
  public void testContainsDep() {
    Assert.assertFalse(defaultLib.containsDependency(invalidDependency));
  }

  @After
  public void after() throws IOException {
    defaultLib.deleteOnExit();
    Path backup = Paths.get("src/test/resources/backup/flatdb-1.0.4.jar");
    Path libsDir = Paths.get("src/test/resources/testLibDir/flatdb-1.0.4.jar");
    if(!Files.exists(libsDir))
      Files.copy(backup, libsDir);
  }
}
