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
package io.github.viscent.mtia.ch12;

import io.github.viscent.mtia.util.Tools;

/**
 * 伪共享Demo
 *
 * @author Viscent Huang
 */
public class FalseSharingDemo extends Thread {
  final CountingTask task;

  public FalseSharingDemo(CountingTask task) {
    this.task = task;
  }

  @Override
  public void run() {
    final CountingTask t = task;
    final long count = t.getIterations();
    for (long i = 0; i < count; i++) {
      t.setValue(t.getValue() + i);
    }
  }

  public static void main(String[] args) throws Exception {
    int argc = args.length;
    int N;// 工作者线程数
    N = argc > 0 ? Integer.valueOf(args[0]) : Runtime.getRuntime()
        .availableProcessors();
    long iterations;
    iterations = argc > 1 ? Long.valueOf(args[1])
        : 400 * 1000 * 1000L;

    String taskImplClassName;
    taskImplClassName = System.getProperty("x.task.impl");
    if (null == taskImplClassName) {
      taskImplClassName = "DefaultCountingTask";
    }

    CountingTask[] tasks = createTasks(taskImplClassName, N, iterations);
    Thread[] demoThreads = new Thread[N];
    for (int i = 0; i < N; i++) {
      demoThreads[i] = new FalseSharingDemo(tasks[i]);
    }
    long start = System.currentTimeMillis();
    // 启动并等待指定的线程终止
    Tools.startAndWaitTerminated(demoThreads);
    System.out
        .printf("Duration: %,d ms %n", System.currentTimeMillis() - start);
  }

  private static CountingTask[] createTasks(String taskImplClassName, int N,
      long iterations) {
    CountingTask[] tasks = new CountingTask[N];
    // 这里必须连续创建多个XXCountingTask实例,
    // 创建这些实例期间不能创建其他实例以提高Java虚拟机为这些对象分配连续的内存空间的几率。
    if ("DefaultCountingTask".equals(taskImplClassName)) {
      for (int i = 0; i < N; i++) {
        tasks[i] = new DefaultCountingTask(iterations);
      }
    } else if ("AutoPaddedCountingTask".equals(taskImplClassName)) {
      for (int i = 0; i < N; i++) {
        tasks[i] = new AutoPaddedCountingTask(iterations);
      }
    } else {
      for (int i = 0; i < N; i++) {
        tasks[i] = new ManuallyPaddedCountingTask(iterations);
      }
    }
    return tasks;
  }
}