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

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

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
      System.out.println(url.toString() + ": " + responseCode);
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

}
