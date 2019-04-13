package com.cyr1en.javen.util;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class JavenUtil {

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
      HttpURLConnection.setFollowRedirects(false);
      HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
      httpURLConnection.setRequestMethod("HEAD");

      httpURLConnection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 6.0; " +
              "en-US; rv:1.9.1.2) Gecko/20090729 Firefox/3.5.2 (.NET CLR 3.5.30729)");
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

}
