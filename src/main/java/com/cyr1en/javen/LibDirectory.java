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

import com.cyr1en.javen.util.FileUtil;
import com.cyr1en.javen.util.JavenUtil;

import java.io.File;
import java.util.*;

import static com.cyr1en.javen.Javen.LOGGER;

public class LibDirectory extends File {

  private static final String DEFAULT_PATH = System.getProperty("user.dir") + "/libs";

  public LibDirectory() {
    this(DEFAULT_PATH);
  }

  public LibDirectory(String pathname) {
    super(pathname);
    if(!exists())
      prepareDir();
    assertDirectory();
  }

  public Map<Dependency, File> listDepsToLoad(ClassLoader... classLoaders) {
    File[] files = listFiles();
    if(files == null) return Collections.emptyMap();
    Map<Dependency, File> builder = new HashMap<>();
    for(Dependency d : JavenUtil.findAllRequestedDeps(classLoaders)) {
      for (File file : files)
        if(FileUtil.isJarFile(file) && FileUtil.getSimpleName(file).equals(d.asJarName()))
          builder.put(d, file);
    }
    return builder;
  }

  public File[] listJarFiles() {
    File[] files = listFiles();
    if(files == null) return new File[0];
    List<File> builder = new ArrayList<>();
    for (File file : files) {
      if(FileUtil.isJarFile(file))
        builder.add(file);
    }
    return builder.toArray(new File[0]);
  }

  public File[] listJarFilesMatching(Dependency dependency) {
    File[] files = listJarFiles();
    if(files.length == 0) return files;
    List<File> builder = new ArrayList<>();
    for(File jarFile : files) {
      String jarName = FileUtil.getSimpleName(jarFile).toLowerCase();
      String depName = dependency.getName().toLowerCase();
      if(jarName.equalsIgnoreCase(dependency.asJarName()) || jarName.contains(depName))
        builder.add(jarFile);
    }
    return builder.toArray(new File[0]);
  }

  public boolean containsDiffVersionOf(Dependency dependency) {
    if(!containsDependency(dependency)) return false;
    if(listJarFilesMatching(dependency).length > 1) return true;
    return true;
  }

  public void deleteDifferentVersion(Dependency dependency) {
    File[] match = listJarFilesMatching(dependency);
    if(!(match.length > 1)) return;

    for(File jar : match) {
      String version = getVersion(jar);
      LOGGER.info("Checking: " + version + " & " + dependency.getVersion());
      if(!dependency.getVersion().equalsIgnoreCase(version)) {
        boolean b = jar.delete();
        if(!b)
          LOGGER.warn("Wasn't able to remove " + FileUtil.getSimpleName(jar));
      }
    }
  }

  public void deleteDependency(Dependency dependency) {
    for(File jar : listJarFiles()) {
      String jarName = FileUtil.getSimpleName(jar);
      if(jarName.equalsIgnoreCase(dependency.asJarName())) {
        boolean b = jar.delete();
        if(!b) {
          LOGGER.warn("Was not able to delete {}, deleting on program exit.", dependency.asJarName());
          jar.deleteOnExit();
        }
      }
    }
  }

  public boolean containsDependency(Dependency dependency) {
    File[] jars = listJarFiles();
    for(File jar : jars) {
      String jarName = FileUtil.getSimpleName(jar);
      if(jarName.equalsIgnoreCase(dependency.asJarName()))
        return true;
    }
    return false;
  }

  private static String getVersion(File jarFile) {
    String sName = FileUtil.getSimpleName(jarFile);
    return sName.substring(sName.indexOf("-") + 1).replace(".jar", "");
  }

  private void assertDirectory() {
    if(!isDirectory())
      throw new IllegalStateException("LibDirectory can only be a directory file!");
  }

  private void prepareDir() {
    boolean b = mkdir();
    if(!b)
      throw new RuntimeException("Wasn't able to prepare LibDirectory!");
  }
}
