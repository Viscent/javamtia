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

import java.util.HashMap;
import java.util.Map;


public class NaiveRequestRegistry implements RequestRegistry {
  @SuppressWarnings({ "rawtypes" })
  private final Map/* <String, RequestMessage> */requests = new HashMap();

  // 注册请求消息
  @SuppressWarnings("unchecked")
  @Override
  public synchronized void registerRequest(RequestMessage request) {
    String requestID = request.getID();
    requests.put(requestID, request);
  }

  // 取消请求消息注册
  @Override
  public synchronized void unregisterRequest(RequestMessage request) {
    String requestID = request.getID();
    requests.remove(requestID);
  }

  // 请求发送线程可调用该方法等待指定请求消息对应的响应消息
  @Override
  public synchronized ResponseMessage
      waitForResponse(RequestMessage request, long timeOut)
          throws TimeoutException, InterruptedException {
    ResponseMessage res = null;
    long start = System.currentTimeMillis();
    long waitTime;
    long now;
    boolean isTimedout = false;
    while (null == (res = request.getResponse())) {
      now = System.currentTimeMillis();
      // 计算剩余等待时间
      waitTime = timeOut - (now - start);
      if (waitTime <= 0) {
        // 等待超时退出
        isTimedout = true;
        break;
      }
      wait(waitTime);
    }// while循环结束
    if (isTimedout) {
      unregisterRequest(request);
      throw new TimeoutException(timeOut, request.toString());
    }
    return res;
  }

  // 响应消息接收线程接收到消息后会调用该方法
  @Override
  public synchronized void responseReceived(ResponseMessage response) {
    String requestID = response.getRequestID();
    RequestMessage request = (RequestMessage) requests.get(requestID);
    // request为null说明响应没有在规定的时间内达到当前系统
    if (null != request) {
      requests.remove(requestID);
      request.setResponse(response);
      notifyAll();
    }
  }
}
