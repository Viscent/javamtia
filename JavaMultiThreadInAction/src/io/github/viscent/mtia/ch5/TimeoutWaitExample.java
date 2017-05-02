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

import java.util.Random;

public class TimeoutWaitExample {
  private static final Object lock = new Object();
  private static boolean ready = false;
  protected static final Random random = new Random();

  public static void main(String[] args) throws InterruptedException {
    Thread t = new Thread() {
      @Override
      public void run() {
        for (;;) {
          synchronized (lock) {
            ready = random.nextInt(100) < 5 ? true : false;
            if (ready) {
              lock.notify();
            }
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

    long start = System.currentTimeMillis();
    long waitTime;
    long now;
    synchronized (lock) {
      while (!ready) {
        now = System.currentTimeMillis();
        // 计算剩余等待时间
        waitTime = timeOut - (now - start);
        Debug.info("Remaining time to wait:%sms", waitTime);
        if (waitTime <= 0) {
          // 等待超时退出
          break;
        }
        lock.wait(waitTime);
      }// while循环结束

      if (ready) {
        // 执行目标动作
        guardedAction();
      } else {
        // 等待超时，保护条件未成立
        Debug.error("Wait timed out,unable to execution target action!");
      }
    }// 同步块结束
  }

  private static void guardedAction() {
    Debug.info("Take some action.");
    // ...
  }
}
