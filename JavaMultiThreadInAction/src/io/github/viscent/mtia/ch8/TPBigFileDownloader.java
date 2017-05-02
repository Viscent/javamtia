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
package io.github.viscent.mtia.ch8;

import io.github.viscent.mtia.ch4.case01.BigFileDownloader;
import io.github.viscent.mtia.ch4.case01.DownloadTask;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class TPBigFileDownloader extends BigFileDownloader {
  final static int N_CPU = Runtime.getRuntime().availableProcessors();
  final ThreadPoolExecutor executor = new ThreadPoolExecutor(2, N_CPU * 2, 4,
      TimeUnit.SECONDS,
      new ArrayBlockingQueue<Runnable>(N_CPU * 8),
      new ThreadPoolExecutor.CallerRunsPolicy());

  public TPBigFileDownloader(String file) throws Exception {
    super(file);
  }

  public static void main(String[] args) throws Exception {
    final int argc = args.length;
    TPBigFileDownloader downloader = new TPBigFileDownloader(args[0]);
    long reportInterval = argc >= 2 ? Integer.valueOf(args[1]) : 10;

    // 平均每个处理器执行8个下载子任务
    final int taskCount = N_CPU * 8;
    downloader.download(taskCount, reportInterval * 1000);
  }

  @Override
  protected void dispatchWork(final DownloadTask dt, int workerIndex) {
    executor.submit(new Runnable() {
      @Override
      public void run() {
        try {
          dt.run();
        } catch (Exception e) {
          e.printStackTrace();
          // 任何一个下载子任务出现异常就取消整个下载任务
          cancelDownload();
        }
      }
    });
  }

  @Override
  protected void doCleanup() {
    executor.shutdownNow();
    super.doCleanup();
  }
}