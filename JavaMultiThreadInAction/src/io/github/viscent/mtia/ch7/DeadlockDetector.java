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

import io.github.viscent.mtia.util.Debug;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;

public class DeadlockDetector extends Thread {
  static final ThreadMXBean tmb = ManagementFactory.getThreadMXBean();
  /**
   * 检测周期（单位为毫秒）
   */
  private final long monitorInterval;

  public DeadlockDetector(long monitorInterval) {
    super("DeadLockDetector");
    setDaemon(true);
    this.monitorInterval = monitorInterval;
  }

  public DeadlockDetector() {
    this(2000);
  }

  public static ThreadInfo[] findDeadlockedThreads() {
    long[] ids = tmb.findDeadlockedThreads();
    return null == tmb.findDeadlockedThreads() ?
        new ThreadInfo[0] : tmb.getThreadInfo(ids);
  }

  public static Thread findThreadById(long threadId) {
    for (Thread thread : Thread.getAllStackTraces().keySet()) {
      if (thread.getId() == threadId) {
        return thread;
      }
    }
    return null;
  }

  public static boolean interruptThread(long threadID) {
    Thread thread = findThreadById(threadID);
    if (null != thread) {
      thread.interrupt();
      return true;
    }
    return false;
  }

  @Override
  public void run() {
    ThreadInfo[] threadInfoList;
    ThreadInfo ti;
    int i = 0;
    try {
      for (;;) {
        // 检测系统中是否存在死锁
        threadInfoList = DeadlockDetector.findDeadlockedThreads();
        if (threadInfoList.length > 0) {
          // 选取一个任意的死锁线程
          ti = threadInfoList[i++ % threadInfoList.length];
          Debug.error("Deadlock detected,trying to recover"
                      + " by interrupting%n thread(%d,%s)%n",
                  ti.getThreadId(),
                  ti.getThreadName());
          // 给选中的死锁线程发送中断
          DeadlockDetector.interruptThread(ti.getThreadId());
          continue;
        } else {
          Debug.info("No deadlock found!");
          i = 0;
        }
        Thread.sleep(monitorInterval);
      }// for循环结束
    } catch (InterruptedException e) {
      // 什么也不做
      ;
    }
  }
}