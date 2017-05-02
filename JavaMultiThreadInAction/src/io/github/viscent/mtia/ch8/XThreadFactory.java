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
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

public class XThreadFactory implements ThreadFactory {
  final static Logger LOGGER = Logger.getAnonymousLogger();
  private final UncaughtExceptionHandler ueh;
  private final AtomicInteger threadNumber = new AtomicInteger(1);
  // 所创建的线程的线程名前缀
  private final String namePrefix;

  public XThreadFactory(UncaughtExceptionHandler ueh, String name) {
    this.ueh = ueh;
    this.namePrefix = name;
  }

  public XThreadFactory(String name) {
    this(new LoggingUncaughtExceptionHandler(), name);
  }

  public XThreadFactory(UncaughtExceptionHandler ueh) {
    this(ueh, "thread");
  }

  public XThreadFactory() {
    this(new LoggingUncaughtExceptionHandler(), "thread");
  }

  protected Thread doMakeThread(final Runnable r) {
    return new Thread(r) {
      @Override
      public String toString() {
        // 返回对问题定位更加有益的信息
        ThreadGroup group = getThreadGroup();
        String groupName = null == group ? "" : group.getName();
        String threadInfo = getClass().getSimpleName() + "[" + getName() + ","
            + getId() + ","
            + groupName + "]@" + hashCode();
        return threadInfo;
      }
    };
  }

  @Override
  public Thread newThread(Runnable r) {
    Thread t = doMakeThread(r);
    t.setUncaughtExceptionHandler(ueh);
    t.setName(namePrefix + "-" + threadNumber.getAndIncrement());
    if (t.isDaemon()) {
      t.setDaemon(false);
    }
    if (t.getPriority() != Thread.NORM_PRIORITY) {
      t.setPriority(Thread.NORM_PRIORITY);
    }
    if (LOGGER.isLoggable(Level.FINE)) {
      LOGGER.fine("new thread created" + t);
    }
    return t;
  }

  static class LoggingUncaughtExceptionHandler implements
      UncaughtExceptionHandler {
    @Override
    public void uncaughtException(Thread t, Throwable e) {
      // 将线程异常终止的相关信息记录到日志中
      LOGGER.log(Level.SEVERE, t + " terminated:", e);
    }
  }// LoggingUncaughtExceptionHandler类定义结束
}