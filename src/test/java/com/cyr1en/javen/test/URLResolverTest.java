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
import com.cyr1en.javen.Repositories;
import com.cyr1en.javen.Repository;
import com.cyr1en.javen.URLResolver;
import org.junit.Before;
import org.junit.Test;

import java.net.MalformedURLException;
import java.net.URL;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class URLResolverTest {

  private static final URL guavaSample;
  private static final URL brigadierSample;
  private static final URL enumToYamlSample;

  static {
    try {
      guavaSample = new URL("https://repo.maven.apache.org/maven2/com/google/guava/guava/27.1-jre/guava-27.1-jre.jar");
      brigadierSample = new URL("https://libraries.minecraft.net/com/mojang/brigadier/1.0.14/brigadier-1.0.14.jar");
      enumToYamlSample = new URL("https://nexus.articdive.de/repository/maven-public/de/articdive/EnumToYAML/1.0-SNAPSHOT/EnumToYAML-1.0-20190129.130317-1.jar");
    } catch (MalformedURLException e) {
      throw new RuntimeException(e);
    }
  }

  private Repositories repos;
  private URLResolver resolver;

  @Before
  public void before() {
    repos = new Repositories();
    resolver = new URLResolver(repos);
  }

  @Test
  public void testValidURLResolve() {
    URL resolved = resolver.resolve(new Dependency("com.google.guava", "guava", "27.1-jre"));
    URL resolved1 = resolver.resolve(new Dependency("de.articdive", "EnumToYAML", "1.0-20190129.130317-1", "https://nexus.articdive.de/repository/maven-public/de/articdive/EnumToYAML/1.0-SNAPSHOT/EnumToYAML-1.0-20190129.130317-1.jar"));
    assertEquals(guavaSample, resolved);
    assertEquals(enumToYamlSample, resolved1);
  }

  @Test
  public void testInvalidURLResolve() {
    URL resolved = resolver.resolve(new Dependency("com.github.cyr1en", "FlatDB", "1.0.5"));
    assertNull(resolved);
  }

  @Test
  public void addRepoAndResolveURL() {
    repos.add(new Repository("mojang-libraries", "https://libraries.minecraft.net/"));
    URL resolved = resolver.resolve(new Dependency("com.mojang", "brigadier", "1.0.14"));
    assertEquals(resolved, brigadierSample);
  }
}
