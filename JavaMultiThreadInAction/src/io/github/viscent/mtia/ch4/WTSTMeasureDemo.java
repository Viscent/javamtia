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
package io.github.viscent.mtia.ch4;

import io.github.viscent.mtia.util.DESEncryption;
import io.github.viscent.mtia.util.Tools;

public class WTSTMeasureDemo implements Runnable {
  final long waitTime;

  public WTSTMeasureDemo(long waitTime) {
    this.waitTime = waitTime;
  }

  public static void main(String[] args) throws Exception {
    main0(args);
  }

  public static void main0(String[] args) throws Exception {
    final int argc = args.length;
    int nThreads = argc > 0 ? Integer.valueOf(args[0]) : 1;
    long waitTime = argc >= 1 ? Long.valueOf(args[0]) : 4000L;
    WTSTMeasureDemo demo = new WTSTMeasureDemo(waitTime);
    Thread[] threads = new Thread[nThreads];
    for (int i = 0; i < nThreads; i++) {
      threads[i] = new Thread(demo);
    }
    long s = System.currentTimeMillis();
    Tools.startAndWaitTerminated(threads);
    long duration = System.currentTimeMillis() - s;
    long serviceTime = duration - waitTime;
    System.out.printf(
        "WT/ST: %-4.2f, waitTime：%dms, serviceTime：%dms, duration：%4.2fs%n",
        waitTime * 1.0f / serviceTime,
        waitTime, serviceTime,
        duration * 1.0f / 1000);
  }

  @Override
  public void run() {
    try {
      // 模拟I/O操作
      Thread.sleep(waitTime);

      // 模拟实际执行计算
      String result = null;
      for (int i = 0; i < 400000; i++) {
        result = DESEncryption.encryptAsString(
            "it is a cpu-intensive task" + i,
            "12345678");
      }
      System.out.printf("result:%s%n", result);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}