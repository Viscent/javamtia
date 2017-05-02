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
import java.util.concurrent.LinkedBlockingQueue;

public class TaskRunner {
  protected final BlockingQueue<Runnable> channel;
  protected volatile Thread workerThread;
  public TaskRunner(BlockingQueue<Runnable> channel) {
    this.channel = channel;
    this.workerThread = new WorkerThread();
  }

  public TaskRunner() {
    this(new LinkedBlockingQueue<Runnable>());
  }

  public void init() {
    final Thread t = workerThread;
    if (null != t) {
      t.start();
    }
  }

  public void submit(Runnable task) throws InterruptedException {
    channel.put(task);
  }

  class WorkerThread extends Thread {
    @Override
    public void run() {
      Runnable task = null;
      // 注意：下面这种代码写法实际上可能导致工作者线程永远无法终止！
      // “5.6 对不起，打扰一下：线程中断机制”中我们将会解决这个问题。
      try {
        for (;;) {
          task = channel.take();
          try {
            task.run();
          } catch (Throwable e) {
            e.printStackTrace();
          }
        }// for循环结束
      } catch (InterruptedException e) {
        // 什么也不做
      }
    }// run方法结束
  }// WorkerThread结束
}