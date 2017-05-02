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

import java.util.Date;
import java.util.Random;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class TimeoutWaitWithCondition {
  private static final Lock lock = new ReentrantLock();
  private static final Condition condition = lock.newCondition();
  private static boolean ready = false;
  protected static final Random random = new Random();

  public static void main(String[] args) throws InterruptedException {
    Thread t = new Thread() {
      @Override
      public void run() {
        for (;;) {
          lock.lock();
          try {
            ready = random.nextInt(100) < 5 ? true : false;
            if (ready) {
              condition.signal();
            }
          } finally {
            lock.unlock();
          }

          // 使当前线程暂停一段（随机）时间
          Tools.randomPause(500);
        }// for循环结束
      }
    };
    t.setDaemon(true);
    t.start();
    waiter(1000);
  }

  public static void waiter(final long timeOut) throws InterruptedException {
    if (timeOut < 0) {
      throw new IllegalArgumentException();
    }
    // 计算等待的最后期限
    final Date deadline = new Date(System.currentTimeMillis() + timeOut);
    // 是否继续等待
    boolean continueToWait = true;
    lock.lock();
    try {
      while (!ready) {
        Debug.info("still not ready,continue to wait:%s", continueToWait);
        // 等待未超时，继续等待
        if (!continueToWait) {
          // 等待超时退出
          Debug.error("Wait timed out,unable to execution target action!");
          return;
        }
        continueToWait = condition.awaitUntil(deadline);
      }// while循环结束

       // 执行目标动作
      guarededAction();
    } finally {
      lock.unlock();
    }
  }

  private static void guarededAction() {
    Debug.info("Take some action.");
    // ...
  }
}
