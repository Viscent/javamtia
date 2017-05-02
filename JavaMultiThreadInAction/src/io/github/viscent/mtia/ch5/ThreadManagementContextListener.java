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

import java.util.concurrent.atomic.AtomicBoolean;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

//@WebListener
public class ThreadManagementContextListener implements ServletContextListener {

  @Override
  public void contextDestroyed(ServletContextEvent ctxEvt) {
    // 停止所有登记的线程
    ThreadTerminationRegistry.INSTANCE.clearThreads();// 语句①
  }

  @Override
  public void contextInitialized(ServletContextEvent ctxEvt) {
    // 创建并启动一个数据库监控线程
    AbstractMonitorThread databaseMonitorThread;
    databaseMonitorThread = new AbstractMonitorThread(
        2000) {
      @Override
      protected void doMonitor() {
        Debug.info("Monitoring database...");
        // ...

        // 模拟实际的时间消耗
        Tools.randomPause(100);
      }
    };

    databaseMonitorThread.start();
  }

  /**
   * 抽象监控线程
   *
   * @author Viscent Huang
   */
  static abstract class AbstractMonitorThread extends Thread {
    // 监控周期
    private final long interval;
    // 线程停止标记
    final AtomicBoolean terminationToken = new AtomicBoolean(false);

    public AbstractMonitorThread(long interval) {
      this.interval = interval;
      // 设置为守护线程!
      setDaemon(true);
      ThreadTerminationRegistry.Handler handler;
      handler = new ThreadTerminationRegistry.Handler() {
        @Override
        public void terminate() {
          terminationToken.set(true);
          AbstractMonitorThread.this.interrupt();
        }
      }; // 语句②
      ThreadTerminationRegistry.INSTANCE.register(handler); // 语句③
    }

    @Override
    public void run() {
      try {
        while (!terminationToken.get()) {
          doMonitor();
          Thread.sleep(interval);
        }
      } catch (InterruptedException e) {
        // 什么也不做
      }
      Debug.info("terminated:%s", Thread.currentThread());
    }

    // 子类覆盖该方法来实现监控逻辑
    protected abstract void doMonitor();
  }
}