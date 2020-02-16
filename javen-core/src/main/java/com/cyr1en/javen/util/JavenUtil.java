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

package com.cyr1en.javen.util;

import com.cyr1en.javen.Dependency;
import com.cyr1en.javen.annotation.Lib;
import org.atteo.classindex.ClassIndex;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class JavenUtil {

  public static boolean validURL(String url) {
    try {
      URL url1 = new URL(url);
      return validURL(url1);
    } catch (MalformedURLException e) {
      e.printStackTrace();
      return false;
    }
  }

  public static boolean validURL(URL url) {
    HttpURLConnection httpURLConnection = buildConnection(url);
    try {
      int responseCode = httpURLConnection.getResponseCode();
      return responseCode == HttpURLConnection.HTTP_OK;
    } catch (IOException e) {
      e.printStackTrace();
      return false;
    } finally {
      httpURLConnection.disconnect();
    }
  }

  private static HttpURLConnection buildConnection(URL url) {
    try {
      HttpURLConnection.setFollowRedirects(true);
      HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
      httpURLConnection.setRequestMethod("HEAD");

      httpURLConnection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) " +
              "AppleWebKit/537.36 (KHTML, like Gecko) Chrome/60.0.3112.113 Safari/537.36");
      return httpURLConnection;
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public static int getFileSizeKB(URL url) {
    HttpURLConnection httpURLConnection = buildConnection(url);
    httpURLConnection.setRequestProperty("Accept-Encoding", "identity");
    try {
      return httpURLConnection.getContentLength() / 1024;
    } finally {
      httpURLConnection.disconnect();
    }
  }

  public static List<Dependency> findAllRequestedDeps(ClassLoader... classLoaders) {
    List<Dependency> builder = new ArrayList<>();
    for (Class<?> c : ClassIndex.getAnnotated(Lib.class)) {
      for (Lib libMeta : c.getDeclaredAnnotationsByType(Lib.class)) {
        String url = FastStrings.isBlank(libMeta.directURL()) ? null : libMeta.directURL();
        builder.add(new Dependency(libMeta.group(), libMeta.name(), libMeta.version(), url));
      }
    }
    for (ClassLoader classLoader : classLoaders) {
      for (Class<?> c : ClassIndex.getAnnotated(Lib.class, classLoader))
        for (Lib libMeta : c.getDeclaredAnnotationsByType(Lib.class)) {
          String url = FastStrings.isBlank(libMeta.directURL()) ? null : libMeta.directURL();
          builder.add(new Dependency(libMeta.group(), libMeta.name(), libMeta.version(), url));
        }
    }
    return builder.stream().distinct().collect(Collectors.toList());
  }

  public static Dependency dependencyByArtifactName(String artifactName) {
    return findAllRequestedDeps().stream()
            .filter(d -> d.getArtifactId().equals(artifactName)).findFirst().orElse(null);
  }

  public static Dependency dependencyByFileName(String fileName) {
    String artifactName = fileName.substring(0, fileName.indexOf("-"));
    return dependencyByArtifactName(artifactName);
  }

}
