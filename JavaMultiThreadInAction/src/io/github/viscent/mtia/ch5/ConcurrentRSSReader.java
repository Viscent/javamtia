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
package io.github.viscent.mtia.ch5;

import io.github.viscent.mtia.util.Tools;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

public class ConcurrentRSSReader {

  public static void main(String[] args) throws Exception {
    final int argc = args.length;
    String url = argc > 0 ? args[0] : "http://lorem-rss.herokuapp.com/feed";

    // 从网络加载RSS数据
    InputStream in = loadRSS(url);
    // 从输入流中解析XML数据
    Document document = parseXML(in);

    // 读取XML中的数据
    Element eleRss = (Element) document.getFirstChild();
    Element eleChannel = (Element) eleRss.getElementsByTagName("channel").item(
        0);
    Node ndTtile = eleChannel.getElementsByTagName("title").item(0);
    String title = ndTtile.getFirstChild().getNodeValue();
    System.out.println(title);
    // 省略其他代码
  }

  private static Document parseXML(InputStream in)
      throws ParserConfigurationException, SAXException, IOException {
    DocumentBuilder db = DocumentBuilderFactory.newInstance()
        .newDocumentBuilder();
    Document document = db.parse(in);
    return document;
  }

  private static InputStream loadRSS(final String url) throws IOException {
    final PipedInputStream in = new PipedInputStream();
    // 以in为参数创建PipedOutputStream实例
    final PipedOutputStream pos = new PipedOutputStream(in);

    Thread workerThread = new Thread(new Runnable() {
      @Override
      public void run() {
        try {
          doDownload(url, pos);
        } catch (Exception e) {
          // RSS数据下载过程中出现异常时，关闭相关输出流和输入流。
          // 注意，此处我们不能像平常那样在finally块中关闭相关输出流
          Tools.silentClose(pos, in);
          e.printStackTrace();
        }
      } // run方法结束
    }, "rss-loader");

    workerThread.start();
    return in;
  }

  static BufferedInputStream issueRequest(String url) throws Exception {
    URL requestURL = new URL(url);
    final HttpURLConnection conn = (HttpURLConnection) requestURL
        .openConnection();
    conn.setConnectTimeout(2000);
    conn.setReadTimeout(2000);
    conn.setRequestMethod("GET");
    conn.setRequestProperty("Connection", "close");
    conn.setDoInput(true);
    conn.connect();
    int statusCode = conn.getResponseCode();
    if (HttpURLConnection.HTTP_OK != statusCode) {
      conn.disconnect();
      throw new Exception("Server exception,status code:" + statusCode);
    }

    BufferedInputStream in = new BufferedInputStream(conn.getInputStream()) {
      // 覆盖BufferedInputStream的close方法，使得输入流被关闭的时候HTTP连接也随之被关闭
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

  static void doDownload(String url, OutputStream os) throws Exception {
    ReadableByteChannel readChannel = null;
    WritableByteChannel writeChannel = null;
    try {
      // 对指定的URL发起HTTP请求
      BufferedInputStream in = issueRequest(url);
      readChannel = Channels.newChannel(in);
      ByteBuffer buf = ByteBuffer.allocate(1024);
      writeChannel = Channels.newChannel(os);
      while (readChannel.read(buf) > 0) {
        buf.flip();
        writeChannel.write(buf);
        buf.clear();
      }
    } finally {
      Tools.silentClose(readChannel, writeChannel);
    }
  } // doDownload方法结束
}