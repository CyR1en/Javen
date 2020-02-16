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
import me.tongfei.progressbar.DelegatingProgressBarConsumer;
import me.tongfei.progressbar.ProgressBar;
import me.tongfei.progressbar.ProgressBarBuilder;
import me.tongfei.progressbar.ProgressBarStyle;

import java.io.*;
import java.net.URL;

public class JarDownloader {

  private LibDirectory libsDir;

  public JarDownloader(LibDirectory libDir) {
    this.libsDir = libDir;
  }

  public void downloadJar(Dependency dep, URL url) {
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
  }

  private ProgressBar buildDownloadPB(String jarName, int size) {
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    PBStream pbStream = new PBStream(out);
    ProgressBarBuilder pbb = new ProgressBarBuilder()
            .setTaskName("Downloading " + jarName)
            .setStyle(ProgressBarStyle.ASCII)
            .setConsumer(new DelegatingProgressBarConsumer(pbStream::print))
            .setUpdateIntervalMillis(100)
            .setInitialMax(size);
    return pbb.build();
  }
}
