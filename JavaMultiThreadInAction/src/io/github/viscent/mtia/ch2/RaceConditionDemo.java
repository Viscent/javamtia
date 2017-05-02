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
package io.github.viscent.mtia.ch2;

import io.github.viscent.mtia.util.Tools;


public class RaceConditionDemo {

  public static void main(String[] args) throws Exception {
    // 客户端线程数
    int numberOfThreads = args.length > 0 ? Short.valueOf(args[0]) : Runtime
        .getRuntime().availableProcessors();
    Thread[] workerThreads = new Thread[numberOfThreads];
    for (int i = 0; i < numberOfThreads; i++) {
      workerThreads[i] = new WorkerThread(i, 10);
    }

    // 待所有线程创建完毕后，再一次性将其启动，以便这些线程能够尽可能地在同一时间内运行
    for (Thread ct : workerThreads) {
      ct.start();
    }
  }

  // 模拟业务线程
  static class WorkerThread extends Thread {
    private final int requestCount;

    public WorkerThread(int id, int requestCount) {
      super("worker-" + id);
      this.requestCount = requestCount;
    }

    @Override
    public void run() {
      int i = requestCount;
      String requestID;
      RequestIDGenerator requestIDGen = RequestIDGenerator.getInstance();
      while (i-- > 0) {
        // 生成Request ID
        requestID = requestIDGen.nextID();
        processRequest(requestID);
      }
    }

    // 模拟请求处理
    private void processRequest(String requestID) {
      // 模拟请求处理耗时
      Tools.randomPause(50);
      System.out.printf("%s got requestID: %s %n",
          Thread.currentThread().getName(), requestID);
    }
  }
}