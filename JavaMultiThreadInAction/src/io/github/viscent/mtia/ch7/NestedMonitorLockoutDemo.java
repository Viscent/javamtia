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
package io.github.viscent.mtia.ch7;

import io.github.viscent.mtia.util.Tools;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * 本程序演示嵌套监视器锁死（线程活性故障）现象。
 * 
 * @author Viscent Huang
 */
public class NestedMonitorLockoutDemo {
  private final BlockingQueue<String> queue = new ArrayBlockingQueue<String>(10);
  private int processed = 0;
  private int accepted = 0;

  public static void main(String[] args) throws InterruptedException {
    NestedMonitorLockoutDemo demo = new NestedMonitorLockoutDemo();
    demo.start();
    int i = 0;
    while (i-- < 100000) {
      demo.accept("message" + i);
      Tools.randomPause(100);
    }

  }

  public synchronized void accept(String message) throws InterruptedException {
    // 不要在临界区内调用BlockingQueue的阻塞方法！那样会导致嵌套监视器锁死
    queue.put(message);
    accepted++;
  }

  protected synchronized void doProcess() throws InterruptedException {
    // 不要在临界区内调用BlockingQueue的阻塞方法！那样会导致嵌套监视器锁死
    String msg = queue.take();
    System.out.println("Process:" + msg);
    processed++;
  }

  public void start() {
    new WorkerThread().start();
  }

  public synchronized int[] getStat() {
    return new int[] { accepted, processed };
  }

  class WorkerThread extends Thread {
    @Override
    public void run() {
      try {
        while (true) {
          doProcess();
        }
      } catch (InterruptedException e) {
        ;
      }
    }
  }
}