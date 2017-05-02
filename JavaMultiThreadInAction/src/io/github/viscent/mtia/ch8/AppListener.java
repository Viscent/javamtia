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


import java.lang.Thread.UncaughtExceptionHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class AppListener implements ServletContextListener {
  final static Logger LOGGER = Logger.getAnonymousLogger();

  @Override
  public void contextInitialized(ServletContextEvent contextEvent) {
    // 设置默认UncaughtExceptionHandler
    UncaughtExceptionHandler ueh = new LoggingUncaughtExceptionHandler();
    Thread.setDefaultUncaughtExceptionHandler(ueh);

    // 启动若干工作者线程
    startServices();
  }

  static class LoggingUncaughtExceptionHandler implements
      UncaughtExceptionHandler {
    @Override
    public void uncaughtException(Thread t, Throwable e) {
      String threadInfo = "Thread[" + t.getName() + "," + t.getId() + ","
          + t.getThreadGroup().getName() + ",@" + t.hashCode() + "]";

      // 将线程异常终止的相关信息记录到日志中
      LOGGER.log(Level.SEVERE, threadInfo + " terminated:", e);
    }
  }

  protected void startServices() {
    // 省略其他代码
  }

  protected void stopServices() {
    // 省略其他代码
  }

  @Override
  public void contextDestroyed(ServletContextEvent contextEvent) {
    Thread.setDefaultUncaughtExceptionHandler(null);
    stopServices();
  }
}