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
package io.github.viscent.mtia.ch9;

import io.github.viscent.mtia.util.Debug;
import io.github.viscent.mtia.util.Tools;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class ScheduledTaskDemo {
  static ScheduledExecutorService ses = Executors.newScheduledThreadPool(2);

  public static void main(String[] args) throws InterruptedException {
    final int argc = args.length;
    // 任务执行最大耗时
    int maxConsumption;
    // 任务执行最小耗时
    int minConsumption;
    if (argc >= 2) {
      minConsumption = Integer.valueOf(args[0]);
      maxConsumption = Integer.valueOf(args[1]);
    } else {
      maxConsumption = minConsumption = 1000;
    }
    ses.scheduleAtFixedRate(new SimulatedTask(minConsumption, maxConsumption,
        "scheduleAtFixedRate"), 0, 2, TimeUnit.SECONDS);
    ses.scheduleWithFixedDelay(new SimulatedTask(minConsumption,
        maxConsumption,
        "scheduleWithFixedDelay"), 0, 1, TimeUnit.SECONDS);
    Thread.sleep(20000);

    ses.shutdown();
  }

  static class SimulatedTask implements Runnable {
    private String name;
    // 模拟任务执行耗时
    private final int maxConsumption;
    private final int minConsumption;
    private final AtomicInteger seq = new AtomicInteger(0);

    public SimulatedTask(int minConsumption, int maxConsumption, String name) {
      this.maxConsumption = maxConsumption;
      this.minConsumption = minConsumption;
      this.name = name;
    }

    @Override
    public void run() {
      try {
        // 模拟任务执行耗时
        Tools.randomPause(maxConsumption, minConsumption);
        Debug.info(name + " run-" + seq.incrementAndGet());
      } catch (Exception e) {
        e.printStackTrace();
      }
    }// run结束
  }
}