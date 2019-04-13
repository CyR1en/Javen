package com.cyr1en.javen;

import com.cyr1en.javen.util.FileUtil;
import com.google.common.collect.ImmutableList;

import java.io.File;

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

  public File[] listJarFiles() {
    File[] files = listFiles();
    if(files == null) return new File[0];
    ImmutableList.Builder<File> builder = new ImmutableList.Builder<>();
    for (File file : files) {
      if(FileUtil.isJarFile(file))
        builder.add(file);
    }
    return builder.build().toArray(new File[0]);
  }

  public File[] listJarFilesMatching(Dependency dependency) {
    File[] files = listJarFiles();
    if(files.length == 0) return files;
    ImmutableList.Builder<File> builder = new ImmutableList.Builder<>();
    for(File jarFile : files) {
      String jarName = FileUtil.getSimpleName(jarFile).toLowerCase();
      String depName = dependency.getName().toLowerCase();
      if(jarName.equalsIgnoreCase(dependency.asJarName()) || jarName.contains(depName))
        builder.add(jarFile);
    }
    return builder.build().toArray(new File[0]);
  }

  public boolean containsDiffVersionOf(Dependency dependency) {
    if(!containsDependency(dependency)) return false;
    if(listJarFilesMatching(dependency).length > 1) return true;
    return true;
  }

  public void removeOldVersionOf(Dependency dependency) {
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
        if(!b)
          LOGGER.warn("Was not able to delete: " + dependency.asJarName());
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
