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

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Semaphore;

/**
 * 基于Semaphore的支持流量控制的传输通道实现。
 *
 * @author Viscent Huang
 *
 * @param <P>
 *          “产品”类型
 */
public class SemaphoreBasedChannel<P> implements Channel<P> {
  private final BlockingQueue<P> queue;
  private final Semaphore semaphore;

  /**
   * @param queue
   *          阻塞队列，通常是一个无界阻塞队列。
   * @param flowLimit
   *          流量限制数
   */
  public SemaphoreBasedChannel(BlockingQueue<P> queue, int flowLimit) {
    this(queue, flowLimit, false);
  }

  public SemaphoreBasedChannel(BlockingQueue<P> queue, int flowLimit,
      boolean isFair) {
    this.queue = queue;
    this.semaphore = new Semaphore(flowLimit, isFair);
  }

  @Override
  public P take() throws InterruptedException {
    return queue.take();
  }

  @Override
  public void put(P product) throws InterruptedException {
    semaphore.acquire();// 申请一个配额
    try {
      queue.put(product);// 访问虚拟资源
    } finally {
      semaphore.release();// 返还一个配额
    }
  }
}
