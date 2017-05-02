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

import io.github.viscent.mtia.util.Debug;
import io.github.viscent.mtia.util.Tools;

import java.io.File;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class TaskResultRetrievalDemo {
  final static int N_CPU = Runtime.getRuntime().availableProcessors();
  final ThreadPoolExecutor executor = new ThreadPoolExecutor(0, N_CPU * 2, 4,
      TimeUnit.SECONDS,
      new ArrayBlockingQueue<Runnable>(100),
      new ThreadPoolExecutor.CallerRunsPolicy());

  public static void main(String[] args) {
    TaskResultRetrievalDemo demo = new TaskResultRetrievalDemo();
    Future<String> future = demo.recognizeImage("/tmp/images/0001.png");
    // 执行其他操作
    doSomething();
    try {
      // 仅在需要相应任务的处理结果时才调用Future.get()
      Debug.info(future.get());
    } catch (InterruptedException e) {
      // 什么也不做
    } catch (ExecutionException e) {
      e.printStackTrace();
    }
  }

  private static void doSomething() {
    Tools.randomPause(200);
  }

  public Future<String> recognizeImage(final String imageFile) {
    return executor.submit(new Callable<String>() {
      @Override
      public String call() throws Exception {
        return doRecognizeImage(new File(imageFile));
      }
    });
  }

  protected String doRecognizeImage(File imageFile) {
    String result = null;
    // 模拟实际运行结果
    String[] simulatedResults = { "苏Z MM518", "苏Z XYZ618", "苏Z 007618" };
    result = simulatedResults[(int) (Math.random() * simulatedResults.length)];
    Tools.randomPause(100);
    // 省略其他代码
    return result;
  }
}