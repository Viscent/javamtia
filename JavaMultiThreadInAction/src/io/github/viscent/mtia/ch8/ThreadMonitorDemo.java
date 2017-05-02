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

import io.github.viscent.mtia.util.Debug;
import io.github.viscent.mtia.util.Tools;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ThreadMonitorDemo {
  volatile boolean inited = false;
  static int threadIndex = 0;
  final static Logger LOGGER = Logger.getAnonymousLogger();
  final BlockingQueue<String> channel = new ArrayBlockingQueue<String>(100);

  public static void main(String[] args) throws InterruptedException {
    ThreadMonitorDemo demo = new ThreadMonitorDemo();
    demo.init();
    for (int i = 0; i < 100; i++) {
      demo.service("test-" + i);
    }

    Thread.sleep(2000);
    System.exit(0);
  }

  public synchronized void init() {
    if (inited) {
      return;
    }
    Debug.info("init...");
    WokrerThread t = new WokrerThread();
    t.setName("Worker0-" + threadIndex++);
    // 为线程t关联一个UncaughtExceptionHandler
    t.setUncaughtExceptionHandler(new ThreadMonitor());
    t.start();
    inited = true;
  }

  public void service(String message) throws InterruptedException {
    channel.put(message);
  }

  private class ThreadMonitor implements Thread.UncaughtExceptionHandler {
    @Override
    public void uncaughtException(Thread t, Throwable e) {
      Debug.info("Current thread is `t`:%s, it is still alive:%s",
          Thread.currentThread() == t, t.isAlive());

      // 将线程异常终止的相关信息记录到日志中
      String threadInfo = t.getName();
      LOGGER.log(Level.SEVERE, threadInfo + " terminated:", e);

      // 创建并启动替代线程
      LOGGER.info("About to restart " + threadInfo);
      // 重置线程启动标记
      inited = false;
      init();
    }

  }// 类ThreadMonitor定义结束

  private class WokrerThread extends Thread {
    @Override
    public void run() {
      Debug.info("Do something important...");
      String msg;
      try {
        for (;;) {
          msg = channel.take();
          process(msg);
        }
      } catch (InterruptedException e) {
        // 什么也不做
      }
    }

    private void process(String message) {
      Debug.info(message);
      // 模拟随机性异常
      int i = (int) (Math.random() * 100);
      if (i < 2) {
        throw new RuntimeException("test");
      }
      Tools.randomPause(100);
    }
  }// 类ThreadMonitorDemo定义结束
}