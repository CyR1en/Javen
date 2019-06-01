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
import me.tongfei.progressbar.ProgressBar;
import me.tongfei.progressbar.ProgressBarBuilder;
import me.tongfei.progressbar.ProgressBarStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.util.*;


public class Javen {

  public static final Logger LOGGER;
  public static final Method ADD_URL_METHOD;

  static {
    try {
      LOGGER = LoggerFactory.getLogger(Javen.class);
      ADD_URL_METHOD = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
      ADD_URL_METHOD.setAccessible(true);
    } catch (NoSuchMethodException e) {
      throw new RuntimeException(e);
    }
  }

  private Repositories repositories;
  private URLResolver resolver;
  private LibDirectory libsDir;
  private Map<Dependency, URLClassLoader> loadedDependency;
  private List<ClassLoader> classLoaders;

  public Javen(Path libPath) {
    repositories = new Repositories();
    resolver = new URLResolver(repositories);
    libsDir = new LibDirectory(libPath.toString());
    loadedDependency = new LinkedHashMap<>();
    classLoaders = new ArrayList<>();
  }

  public static synchronized void loadDependencies(File[] files) {
    for (File file : files) {
      String name = file.getName();
      URLClassLoader cl = (URLClassLoader) Javen.class.getClassLoader();
      try {
        ADD_URL_METHOD.invoke(cl, file.toURI().toURL());
        LOGGER.info("Successfully loaded: " + name);
      } catch (IllegalAccessException | InvocationTargetException | MalformedURLException e) {
        e.printStackTrace();
      }
    }
  }
  
  public synchronized void loadDependencies() {
    downloadNeededDeps();
    try {
      for(Map.Entry<Dependency, File> entry : libsDir.listDepsToLoad(classLoaders.toArray(new ClassLoader[0])).entrySet()) {
        URL url = entry.getValue().toURI().toURL();
        URLClassLoader cl = (URLClassLoader) this.getClass().getClassLoader();
        ADD_URL_METHOD.invoke(cl, url);
        loadedDependency.put(entry.getKey(), cl);
        LOGGER.info("Successfully loaded: " + entry.getKey().asJarName());
      }
    } catch (IllegalAccessException | InvocationTargetException | MalformedURLException e) {
      e.printStackTrace();
    }
  }

  public void downloadNeededDeps() {
    Map<Dependency, URL> need = getDepsToDownload();
    need.forEach((dep, url) -> {
      if (libsDir.containsDiffVersionOf(dep))
        libsDir.deleteDifferentVersion(dep);
      int size = JavenUtil.getFileSizeKB(url);
      try (BufferedInputStream inputStream = new BufferedInputStream(url.openStream());
           FileOutputStream fileOS = new FileOutputStream(new File(libsDir, dep.asJarName()));
           ProgressBar pb = buildDownloadPB(dep.asJarName(), size)) {
        byte[] data = new byte[1024];
        int byteContent;
        while ((byteContent = inputStream.read(data, 0, 1024)) != -1) {
          fileOS.write(data, 0, byteContent);
          pb.step();
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    });
  }

  public void unloadDependency(Dependency dependency) {
    if(!loadedDependency.containsKey(dependency))
      return;
    try {
      loadedDependency.get(dependency).close();
    } catch (IOException e) {
      e.printStackTrace();
    }
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

  public void addClassLoader(ClassLoader... cls) {
    classLoaders.addAll(Arrays.asList(cls));
  }

  private ProgressBar buildDownloadPB(String jarName, int size) {
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    PBStream pbStream = new PBStream(out);
    ProgressBarBuilder pbb = new ProgressBarBuilder()
            .setTaskName("Downloading " + jarName)
            .setStyle(ProgressBarStyle.ASCII)
            .setUpdateIntervalMillis(100)
            .setInitialMax(size)
            .setPrintStream(pbStream);
    return pbb.build();
  }
}
