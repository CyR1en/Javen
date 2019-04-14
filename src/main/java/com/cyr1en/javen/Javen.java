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

import com.cyr1en.javen.annotation.Lib;
import com.cyr1en.javen.util.JavenUtil;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import me.tongfei.progressbar.ProgressBar;
import me.tongfei.progressbar.ProgressBarBuilder;
import me.tongfei.progressbar.ProgressBarStyle;
import org.atteo.classindex.ClassIndex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


public class Javen {

  public static final Logger LOGGER;
  private static final Method ADD_URL_METHOD;

  static {
    try {
      LOGGER = LoggerFactory.getLogger("Javen");
      ADD_URL_METHOD = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
      ADD_URL_METHOD.setAccessible(true);
    } catch (NoSuchMethodException e) {
      throw new RuntimeException(e);
    }
  }

  private Repositories repositories;
  private URLResolver resolver;
  private LibDirectory libsDir;

  public Javen(Path libPath) {
    repositories = new Repositories();
    resolver = new URLResolver(repositories);
    libsDir = new LibDirectory(libPath.toString());
  }

  public void loadDependencies(URLClassLoader classLoader) {
    downloadNeededDeps();
    File[] jars = libsDir.listJarFiles();
    try {
      for (File jar : jars) {
        URL url = jar.toURI().toURL();
        ADD_URL_METHOD.invoke(classLoader, url);
        LOGGER.info("Successfully loaded: " + url);
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

  public Map<Dependency, URL> getDepsToDownload() {
    ImmutableMap.Builder<Dependency, URL> builder = new ImmutableMap.Builder<>();
    findAllRequestedDeps().forEach(d -> {
      if (!libsDir.containsDependency(d)) {
        URL resolved = resolver.resolve(d);
        if (resolved != null)
          builder.put(d, resolved);
      }
    });
    return builder.build();
  }

  public List<Dependency> findAllRequestedDeps() {
    ImmutableList.Builder<Dependency> builder = new ImmutableList.Builder<>();
    ClassIndex.getAnnotated(Lib.class).forEach(c -> {
      for (Lib libMeta : c.getDeclaredAnnotationsByType(Lib.class))
        builder.add(new Dependency(libMeta.group(), libMeta.name(), libMeta.version()));
    });
    return builder.build().stream().distinct().collect(Collectors.toList());
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
