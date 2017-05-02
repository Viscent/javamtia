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
package io.github.viscent.mtia.ch3.case03;

import io.github.viscent.mtia.util.Debug;

import java.util.concurrent.atomic.AtomicBoolean;

public enum AlarmMgr implements Runnable {
  // 保存该类的唯一实例
  INSTANCE;
  private final AtomicBoolean initializating = new AtomicBoolean(false);
  boolean initInProgress;

  AlarmMgr() {
    // 什么也不做
  }

  public void init() {
    // 使用AtomicBoolean的CAS操作确保工作者线程只会被创建（并启动）一次
    if (initializating.compareAndSet(false, true)) {
      Debug.info("initializating...");
      // 创建并启动工作者线程
      new Thread(this).start();
    }
  }

  public int sendAlarm(String message) {
    int result = 0;
    // ...
    return result;
  }

  @Override
  public void run() {
    // ...
  }
}