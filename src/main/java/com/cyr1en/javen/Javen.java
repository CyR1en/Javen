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
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;


public class Javen {

  public static final Logger LOGGER;
  private static final Method ADD_URL;

  static {
    try {
      LOGGER = LoggerFactory.getLogger(Javen.class);
      ADD_URL = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
      ADD_URL.setAccessible(true);
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

  public void downloadNeededDeps() {
    Map<Dependency, URL> need = getDepsToDownload();
    need.forEach((dep, url) -> {

      ByteArrayOutputStream out = new ByteArrayOutputStream();
      PBStream pbStream = new PBStream(out);
      int size = JavenUtil.getFileSizeKB(url);
      ProgressBarBuilder pbb = new ProgressBarBuilder()
              .setTaskName("Downloading " + dep.asJarName())
              .setStyle(ProgressBarStyle.ASCII)
              .setUpdateIntervalMillis(100)
              .setInitialMax(size)
              .setPrintStream(pbStream);
      try (BufferedInputStream inputStream = new BufferedInputStream(url.openStream());
           FileOutputStream fileOS = new FileOutputStream(new File(libsDir, dep.asJarName()));
           ProgressBar pb = pbb.build()) {

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
      if(!libsDir.containsDependency(d)) {
        URL resolved = resolver.resolve(d);
        builder.put(d, resolved);
      }
    });
    return builder.build();
  }

  public List<Dependency> findAllRequestedDeps() {
    ImmutableList.Builder<Dependency> builder = new ImmutableList.Builder<>();
    ClassIndex.getAnnotated(Lib.class).forEach(c -> {
      for (Lib libMeta : c.getDeclaredAnnotationsByType(Lib.class)) {
        builder.add(new Dependency(libMeta.group(), libMeta.name(), libMeta.version()));
      }
    });
    return builder.build();
  }

  public void addRepository(Repository repository) {
    repositories.addRepo(repository);
  }

  public void addRepository(String id, String url) {
    Repository repo = new Repository(id, url);
    addRepository(repo);
  }

  public void loadDependencies() {

  }

  public LibDirectory getLibsDir() {
    return libsDir;
  }
}
