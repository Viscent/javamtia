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
package io.github.viscent.mtia.ch4.case01;

import io.github.viscent.mtia.util.Debug;
import io.github.viscent.mtia.util.Tools;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 下载子任务
 *
 * @author Viscent Huang
 */
public class DownloadTask implements Runnable {
  private final long lowerBound;
  private final long upperBound;
  private final DownloadBuffer xbuf;
  private final URL requestURL;
  private final AtomicBoolean cancelFlag;

  public DownloadTask(long lowerBound, long upperBound, URL requestURL,
      Storage storage, AtomicBoolean cancelFlag) {
    this.lowerBound = lowerBound;
    this.upperBound = upperBound;
    this.requestURL = requestURL;
    this.xbuf = new DownloadBuffer(lowerBound, upperBound, storage);
    this.cancelFlag = cancelFlag;
  }

  // 对指定的URL发起HTTP分段下载请求
  private static InputStream issueRequest(URL requestURL, long lowerBound,
      long upperBound) throws IOException {
    Thread me = Thread.currentThread();
    Debug.info(me + "->[" + lowerBound + "," + upperBound + "]");
    final HttpURLConnection conn;
    InputStream in = null;
    conn = (HttpURLConnection) requestURL.openConnection();
    String strConnTimeout = System.getProperty("x.dt.conn.timeout");
    int connTimeout = null == strConnTimeout ? 60000 : Integer
        .valueOf(strConnTimeout);
    conn.setConnectTimeout(connTimeout);

    String strReadTimeout = System.getProperty("x.dt.read.timeout");
    int readTimeout = null == strReadTimeout ? 60000 : Integer
        .valueOf(strReadTimeout);
    conn.setReadTimeout(readTimeout);

    conn.setRequestMethod("GET");
    conn.setRequestProperty("Connection", "Keep-alive");
    // Range: bytes=0-1024
    conn.setRequestProperty("Range", "bytes=" + lowerBound + "-" + upperBound);
    conn.setDoInput(true);
    conn.connect();

    int statusCode = conn.getResponseCode();
    if (HttpURLConnection.HTTP_PARTIAL != statusCode) {
      conn.disconnect();
      throw new IOException("Server exception,status code:" + statusCode);
    }

    Debug.info(me + "-Content-Range:" + conn.getHeaderField("Content-Range")
        + ",connection:" + conn.getHeaderField("connection"));

    in = new BufferedInputStream(conn.getInputStream()) {
      @Override
      public void close() throws IOException {
        try {
          super.close();
        } finally {
          conn.disconnect();
        }
      }
    };

    return in;
  }

  @Override
  public void run() {
    if (cancelFlag.get()) {
      return;
    }
    ReadableByteChannel channel = null;
    try {
      channel = Channels.newChannel(issueRequest(requestURL, lowerBound,
          upperBound));
      ByteBuffer buf = ByteBuffer.allocate(1024);
      while (!cancelFlag.get() && channel.read(buf) > 0) {
        // 将从网络读取的数据写入缓冲区
        xbuf.write(buf);
        buf.clear();
      }
    } catch (Exception e) {
      throw new RuntimeException(e);
    } finally {
      Tools.silentClose(channel, xbuf);
    }
  }
}