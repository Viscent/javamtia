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

import java.util.concurrent.atomic.AtomicLong;

public class RequestMessage {
  private ResponseMessage response;
  private String ID;
  private int messageType;
  private long requestTime;
  static AtomicLong seq = new AtomicLong(-1L);

  public RequestMessage() {
    ID = String.format("%06d", seq.incrementAndGet());
  }

  public String getID() {

    return ID;
  }

  public ResponseMessage getResponse() {
    return response;
  }

  public void setResponse(ResponseMessage response) {
    this.response = response;
  }

  // ...

  public int getMessageType() {
    return messageType;
  }

  public void setMessageType(int messageType) {
    this.messageType = messageType;
  }

  public long getRequestTime() {
    return requestTime;
  }

  public void setRequestTime(long requestTime) {
    this.requestTime = requestTime;
  }

  @Override
  public String toString() {
    return "Request [response=" + response + ", ID=" + ID + ", messageType=" + messageType
        + ", requestTime=" + requestTime + "]";
  }

}
