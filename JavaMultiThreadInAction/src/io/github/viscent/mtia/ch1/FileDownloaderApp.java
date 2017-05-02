/*
授权声明：
本源码系《Java多线程编程实战指南（核心篇）》一书（ISBN：978-7-121-31065-2，以下称之为“原书”）的配套源码，
欲了解本代码的更多细节，请参考原书。
本代码仅为原书的配套说明之用，并不附带任何承诺（如质量保证和收益）。
以任何形式将本代码之部分或者全部用于营利性用途需经版权人书面同意。
将本代码之部分或者全部用于非营利性用途需要在代码中保留本声明。
任何对本代码的修改需在代码中以注释的形式注明修改人、修改时间以及修改内容。
本代码可以从以下网址下载：
https://github.com/Viscent/javamtia
http://www.broadview.com.cn/31065
*/
package io.github.viscent.mtia.ch1;

import io.github.viscent.mtia.util.Debug;
import io.github.viscent.mtia.util.Tools;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;

public class FileDownloaderApp {

  public static void main(String[] args) {
    Thread downloaderThread = null;
    for (String url : args) {
      // 创建文件下载器线程
      downloaderThread = new Thread(new FileDownloader(url));
      // 启动文件下载器线程
      downloaderThread.start();
    }
  }

  // 文件下载器
  static class FileDownloader implements Runnable {
    private final String fileURL;

    public FileDownloader(String fileURL) {
      this.fileURL = fileURL;
    }

    @Override
    public void run() {
      Debug.info("Downloading from " + fileURL);
      String fileBaseName = fileURL.substring(fileURL.lastIndexOf('/') + 1);
      try {
        URL url = new URL(fileURL);
        String localFileName = System.getProperty("java.io.tmpdir")
            + "/viscent-"
            + fileBaseName;
        Debug.info("Saving to: " + localFileName);
        downloadFile(url, new FileOutputStream(
            localFileName), 1024);
      } catch (Exception e) {
        e.printStackTrace();
      }
      Debug.info("Done downloading from " + fileURL);
    }

    // 从指定的URL下载文件，并将其保存到指定的输出流中
    private void downloadFile(URL url, OutputStream outputStream, int bufSize)
        throws MalformedURLException, IOException {
      // 建立HTTP连接
      final HttpURLConnection httpConn = (HttpURLConnection) url
          .openConnection();
      httpConn.setRequestMethod("GET");
      ReadableByteChannel inChannel = null;
      WritableByteChannel outChannel = null;
      try {
        // 获取HTTP响应码
        int responseCode = httpConn.getResponseCode();
        // HTTP响应非正常:响应码不为2开头
        if (2 != responseCode / 100) {
          throw new IOException("Error: HTTP " + responseCode);
        }

        if (0 == httpConn.getContentLength()) {
          Debug.info("Nothing to be downloaded " + fileURL);
          return;
        }
        inChannel = Channels
            .newChannel(new BufferedInputStream(httpConn.getInputStream()));
        outChannel = Channels
            .newChannel(new BufferedOutputStream(outputStream));
        ByteBuffer buf = ByteBuffer.allocate(bufSize);
        while (-1 != inChannel.read(buf)) {
          buf.flip();
          outChannel.write(buf);
          buf.clear();
        }
      } finally {
        // 关闭指定的Channel以及HttpURLConnection
        Tools.silentClose(inChannel, outChannel);
        httpConn.disconnect();
      }
    }// downloadFile结束
  }// FileDownloader结束
}