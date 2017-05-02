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

import io.github.viscent.mtia.util.Debug;
import io.github.viscent.mtia.util.Tools;

import java.util.concurrent.CountDownLatch;

public class CountDownLatchExample {
  private static final CountDownLatch latch = new CountDownLatch(4);
  private static int data;

  public static void main(String[] args) throws InterruptedException {
    Thread workerThread = new Thread() {
      @Override
      public void run() {
        for (int i = 1; i < 10; i++) {
          data = i;
          latch.countDown();
          // 使当前线程暂停（随机）一段时间
          Tools.randomPause(1000);
        }

      };
    };
    workerThread.start();
    latch.await();
    Debug.info("It's done. data=%d", data);
  }
}
