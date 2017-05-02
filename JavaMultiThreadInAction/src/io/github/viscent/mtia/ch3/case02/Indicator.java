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
package io.github.viscent.mtia.ch3.case02;

import java.util.concurrent.atomic.AtomicLong;

public class Indicator {
  // 保存当前类的唯一实例
  private static final Indicator INSTANCE = new Indicator();
  /**
   * 记录请求总数
   */
  private final AtomicLong requestCount = new AtomicLong(0);

  /**
   * 记录处理成功总数
   */
  private final AtomicLong successCount = new AtomicLong(0);

  /**
   * 记录处理失败总数
   */
  private final AtomicLong failureCount = new AtomicLong(0);

  private Indicator() {
    // 什么也不做
  }

  // 返回该类的唯一实例
  public static Indicator getInstance() {
    return INSTANCE;
  }

  public void newRequestReceived() {
    // 使总请求数增加1。 这里无需加锁。
    requestCount.incrementAndGet();
  }

  public void newRequestProcessed() {
    // 使总请求数增加1。 这里无需加锁。
    successCount.incrementAndGet();
  }

  public void requestProcessedFailed() {
    // 使总请求数增加1。 这里无需加锁。
    failureCount.incrementAndGet();
  }

  public long getRequestCount() {
    return requestCount.get();
  }

  public long getSuccessCount() {
    return successCount.get();
  }

  public long getFailureCountCount() {
    return failureCount.get();
  }

  public void reset() {
    requestCount.set(0);
    successCount.set(0);
    failureCount.set(0);
  }

  @Override
  public String toString() {
    return "Counter [requestCount=" + requestCount + ", successCount="
        + successCount + ", failureCount=" + failureCount + "]";
  }
}
