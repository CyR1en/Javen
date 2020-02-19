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

import com.cyr1en.javen.util.JavenUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

import java.net.URL;
import java.nio.file.Path;
import java.util.*;

public class Javen {

  public static Logger LOGGER;

  static {
    LOGGER = LoggerFactory.getLogger(Javen.class);
  }

  private Repositories repositories;
  private URLResolver resolver;
  private LibDirectory libsDir;
  private JarDownloader downloader;
  private List<Dependency> loadedDependency;
  private List<ClassLoader> classLoaders;
  private static Loader loader;

  public Javen(Path libPath) {
    repositories = new Repositories();
    resolver = new URLResolver(repositories);
    libsDir = new LibDirectory(libPath.toString());
    downloader = new JarDownloader(libsDir, resolver);
    loadedDependency = new ArrayList<>();
    classLoaders = new ArrayList<>();
    loader = new Loader(this);
  }

  public static synchronized void loadDependencies(File[] files) {
    for (File file : files)
      loader.addJarToClassPath(file);
  }

  public synchronized void loadDependencies() {
    downloadNeededDeps();
    for (Map.Entry<Dependency, File> entry : libsDir.listDepsToLoad(classLoaders.toArray(new ClassLoader[0])).entrySet()) {
      loader.addJarToClassPath(entry.getValue());
      loadedDependency.add(entry.getKey());
    }
  }

  public void downloadNeededDeps() {
    Map<Dependency, URL> need = getDepsToDownload();
    need.forEach((dep, url) -> {
      if (libsDir.containsDiffVersionOf(dep))
        libsDir.deleteDifferentVersion(dep);
      downloader.downloadJar(dep);
    });
  }

  public Map<Dependency, URL> getDepsToDownload() {
    Map<Dependency, URL> deps = new HashMap<>();
    JavenUtil.findAllRequestedDeps(classLoaders.toArray(new ClassLoader[0])).forEach(d -> {
      if (!libsDir.containsDependency(d)) {
        URL resolved = resolver.resolve(d);
        if (resolved != null)
          deps.put(d, resolved);
      }
    });
    return deps;
  }

  public void addRepository(Repository repository) {
    repositories.addRepo(repository);
  }

  public void addRepository(String id, String url) {
    Repository repo = new Repository(id, url);
    addRepository(repo);
  }

  public LibDirectory getLibsDir() {
    return libsDir;
  }

  public Repositories getRepositories() {
    return repositories;
  }

  public URLResolver getResolver() {
    return this.resolver;
  }

  public void delegateLogger(Logger logger) {
    LOGGER = logger;
  }

  public JarDownloader getDownloader() {
    return this.downloader;
  }

  public void addClassLoader(ClassLoader... cls) {
    classLoaders.addAll(Arrays.asList(cls));
  }

}
