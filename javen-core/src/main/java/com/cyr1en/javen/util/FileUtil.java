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

import java.io.*;
import java.util.jar.JarFile;
import java.util.zip.ZipFile;

public class FileUtil {

  public static boolean isJarFile(File file) {
    try {
      if (!isZipFile(file)) {
        return false;
      }
      ZipFile zip = new ZipFile(file);
      boolean manifest = zip.getEntry("META-INF/MANIFEST.MF") != null;
      zip.close();
      return manifest;
    } catch (IOException e) {
      e.printStackTrace();
      return false;
    }
  }

  public static boolean isZipFile(File file) {
    try {
      if (!file.canRead() || file.isDirectory()) {
        return false;
      }
      if (file.length() < 4) {
        return false;
      }
      DataInputStream in = new DataInputStream(new BufferedInputStream(new FileInputStream(file)));
      int test = in.readInt();
      in.close();
      return test == 0x504b0304;
    } catch (IOException e) {
      e.printStackTrace();
      return false;
    }
  }

  public static JarFile getJarFile(File file) {
    if (!isJarFile(file))
      throw new IllegalArgumentException("Only jar files can be converted to JarFile");
    try {
      return new JarFile(file);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public static String getSimpleName(File file) {
    if (file == null) return "null";
    return file.getName().substring(file.getName().lastIndexOf("\\") + 1);
  }
}
