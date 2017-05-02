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
package io.github.viscent.mtia.ch5.case02;

import io.github.viscent.mtia.util.Debug;

import java.util.concurrent.CountDownLatch;

public abstract class AbstractService implements Service {
  protected boolean started = false;
  protected final CountDownLatch latch;

  public AbstractService(CountDownLatch latch) {
    this.latch = latch;
  }

  @Override
  public boolean isStarted() {
    return started;
  }

  // 留给子类实现的抽象方法，用于实现服务器的启动逻辑
  protected abstract void doStart() throws Exception;

  @Override
  public void start() {
    new ServiceStarter().start();
  }

  @Override
  public void stop() {
    // 默认什么也不做
  }

  class ServiceStarter extends Thread {
    @Override
    public void run() {
      final String serviceName = AbstractService.this.getClass()
          .getSimpleName();
      Debug.info("Starting %s", serviceName);
      try {
        doStart();
        started = true;
      } catch (Exception e) {
        e.printStackTrace();
      } finally {
        latch.countDown();
        Debug.info("Done Starting %s", serviceName);
      }
    }
  }
}