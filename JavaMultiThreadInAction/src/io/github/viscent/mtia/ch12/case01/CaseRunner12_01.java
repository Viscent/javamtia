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
package io.github.viscent.mtia.ch12.case01;

import io.github.viscent.mtia.util.Debug;
import io.github.viscent.mtia.util.Tools;

import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class CaseRunner12_01 {
  static final RequestRegistry rr = RequestRegistryHolder.INSTANCE.getRegistry();
  static BlockingQueue<RequestMessage> generatedRequests = new ArrayBlockingQueue<>(100);

  public static void main(String[] args) throws InterruptedException {
    int N = Runtime.getRuntime().availableProcessors() * 8;
    Thread[] simulatorThreads = new Thread[N * 2];
    Long[] monitorTargetThreadIds = new Long[N];
    for (int i = 0; i < N; i++) {
      simulatorThreads[i] = new RequestMessageSenderSimulator();
      monitorTargetThreadIds[i] = simulatorThreads[i].getId();
    }

    for (int i = 0; i < N; i++) {
      simulatorThreads[N + i] = new ResponseMessageReceiverSimulator();
    }

    for (Thread t : simulatorThreads) {
      t.start();
    }

    Tools.delayedAction("The program will be teriminated", new Runnable() {
      @Override
      public void run() {
        System.exit(0);
      }
    }, 4);
  }

  // 模拟请求消息发送线程
  static class RequestMessageSenderSimulator extends Thread {
    RequestMessageSenderSimulator() {
      super("reqSimulator");
    }

    @Override
    public void run() {
      for (;;) {
        // 构造请求
        RequestMessage request = constructRequest();
        // ...
        // 异步发送（并注册）请求
        asyncSendRequest(request);
        // 模拟执行其他操作
        Tools.randomPause(100, 50);
        // 等待响应
        try {
          ResponseMessage response = rr.waitForResponse(request, 2000);
          processResponse(response);
        } catch (TimeoutException e) {
          e.printStackTrace();
        } catch (InterruptedException ignored) {
        }

        Tools.randomPause(500, 100);
      }
    }// run结束

    RequestMessage constructRequest() {
      RequestMessage request = new RequestMessage();
      request.setMessageType(2);
      // ...
      return request;
    }

    void asyncSendRequest(RequestMessage request) {
      request.setRequestTime(System.currentTimeMillis());
      // 注册请求
      rr.registerRequest(request);
      try {
        generatedRequests.put(request);
      } catch (InterruptedException ignored) {

      }
      Debug.info("sending request %s", request.toString());
      // ...
    }

    void processResponse(ResponseMessage response) {
      Debug.info(response.toString());
      // ...
    }
  }// RequestMessageSenderSimulator结束

  // 模拟响应消息接收线程
  static class ResponseMessageReceiverSimulator extends Thread {
    final Random rnd = new Random();

    ResponseMessageReceiverSimulator() {
      super("respSimulator");
    }

    @Override
    public void run() {
      RequestMessage request;
      try {
        for (;;) {
          request = generatedRequests.take();
          processRequest(request);
        }
      } catch (InterruptedException ignored) {
      }
    }// run结束

    private void processRequest(RequestMessage request) {
      // 模拟执行其他操作
      Tools.randomPause(200, 80);
      ResponseMessage response = new ResponseMessage();
      response.setRequestID(request.getID());
      response.setResultCode(rnd.nextInt(4));
      response.setReceiveTime(System.currentTimeMillis());
      rr.responseReceived(response);
    }
  }
}
