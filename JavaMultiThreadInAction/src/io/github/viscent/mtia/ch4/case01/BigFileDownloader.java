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

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 大文件下载器
 *
 * @author Viscent Huang
 */
public class BigFileDownloader {
  protected final URL requestURL;
  protected final long fileSize;
  /**
   * 负责已下载数据的存储
   */
  protected final Storage storage;
  protected final AtomicBoolean taskCanceled = new AtomicBoolean(false);

  public BigFileDownloader(String strURL) throws Exception {
    requestURL = new URL(strURL);

    // 获取待下载资源的大小（单位：字节）
    fileSize = retieveFileSize(requestURL);
    Debug.info("file total size:%s", fileSize);
    String fileName = strURL.substring(strURL.lastIndexOf('/') + 1);
    // 创建负责存储已下载数据的对象
    storage = new Storage(fileSize, fileName);
  }

  /**
   * 下载指定的文件
   *
   * @param taskCount
   *          任务个数
   * @param reportInterval
   *          下载进度报告周期
   * @throws Exception
   */
  public void download(int taskCount, long reportInterval)
      throws Exception {

    long chunkSizePerThread = fileSize / taskCount;
    // 下载数据段的起始字节
    long lowerBound = 0;
    // 下载数据段的结束字节
    long upperBound = 0;

    DownloadTask dt;
    for (int i = taskCount - 1; i >= 0; i--) {
      lowerBound = i * chunkSizePerThread;
      if (i == taskCount - 1) {
        upperBound = fileSize;
      } else {
        upperBound = lowerBound + chunkSizePerThread - 1;
      }

      // 创建下载任务
      dt = new DownloadTask(lowerBound, upperBound, requestURL, storage,
          taskCanceled);
      dispatchWork(dt, i);
    }
    // 定时报告下载进度
    reportProgress(reportInterval);
    // 清理程序占用的资源
    doCleanup();

  }

  protected void doCleanup() {
    Tools.silentClose(storage);
  }

  protected void cancelDownload() {
    if (taskCanceled.compareAndSet(false, true)) {
      doCleanup();
    }
  }

  protected void dispatchWork(final DownloadTask dt, int workerIndex) {
    // 创建下载线程
    Thread workerThread = new Thread(new Runnable() {
      @Override
      public void run() {
        try {
          dt.run();
        } catch (Exception e) {
          e.printStackTrace();
          // 取消整个文件的下载
          cancelDownload();
        }
      }
    });
    workerThread.setName("downloader-" + workerIndex);
    workerThread.start();
  }

  // 根据指定的URL获取相应文件的大小
  private static long retieveFileSize(URL requestURL) throws Exception {
    long size = -1;
    HttpURLConnection conn = null;
    try {
      conn = (HttpURLConnection) requestURL.openConnection();

      conn.setRequestMethod("HEAD");
      conn.setRequestProperty("Connection", "Keep-alive");
      conn.connect();
      int statusCode = conn.getResponseCode();
      if (HttpURLConnection.HTTP_OK != statusCode) {
        throw new Exception("Server exception,status code:" + statusCode);
      }

      String cl = conn.getHeaderField("Content-Length");
      size = Long.valueOf(cl);
    } finally {
      if (null != conn) {
        conn.disconnect();
      }
    }
    return size;
  }

  // 报告下载进度
  private void reportProgress(long reportInterval) throws InterruptedException {
    float lastCompletion;
    int completion = 0;
    while (!taskCanceled.get()) {
      lastCompletion = completion;
      completion = (int) (storage.getTotalWrites() * 100 / fileSize);
      if (completion == 100) {
        break;
      } else if (completion - lastCompletion >= 1) {
        Debug.info("Completion:%s%%", completion);
        if (completion >= 90) {
          reportInterval = 1000;
        }
      }
      Thread.sleep(reportInterval);
    }
    Debug.info("Completion:%s%%", completion);
  }
}