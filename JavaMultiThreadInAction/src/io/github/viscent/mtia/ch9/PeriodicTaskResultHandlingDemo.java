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

import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class PeriodicTaskResultHandlingDemo {
  final static ScheduledExecutorService ses = Executors.newScheduledThreadPool(2);

  public static void main(String[] args) throws InterruptedException {
    final String host = args[0];
    final AsyncTask<Integer> asyncTask = new AsyncTask<Integer>(ses) {
      final Random rnd = new Random();
      final String targetHost = host;

      @Override
      public Integer call() throws Exception {
        return pingHost();
      }

      private Integer pingHost() throws Exception {
        // 模拟实际操作耗时
        Tools.randomPause(2000);
        // 模拟的探测结果码
        Integer r = Integer.valueOf(rnd.nextInt(4));
        return r;
      }

      @Override
      protected void onResult(Integer result) {
        // 将结果保存到数据库
        saveToDatabase(result);
      }

      private void saveToDatabase(Integer result) {
        Debug.info(targetHost + " status:" + String.valueOf(result));
        // 省略其他代码
      }

      @Override
      public String toString() {
        return "Ping " + targetHost + "," + super.toString();
      }
    };

    ses.scheduleAtFixedRate(asyncTask, 0, 3, TimeUnit.SECONDS);

    Tools.delayedAction("The ScheduledExecutorService will be shutdown", new Runnable() {
      @Override
      public void run() {
        ses.shutdown();
      }
    }, 60);
  }
}