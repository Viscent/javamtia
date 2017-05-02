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

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public final class RequestIDGenerator implements CircularSeqGenerator {
  /**
   * 保存该类的唯一实例
   */
  private final static RequestIDGenerator INSTANCE = new RequestIDGenerator();
  private final static short SEQ_UPPER_LIMIT = 999;
  private short sequence = -1;

  // 私有构造器
  private RequestIDGenerator() {
    // 什么也不做
  }

  /**
   * 生成循环递增序列号
   *
   * @return
   */
  @Override
  public short nextSequence() {
    if (sequence >= SEQ_UPPER_LIMIT) {
      sequence = 0;
    } else {
      sequence++;
    }
    return sequence;
  }

  /**
   * 生成一个新的Request ID
   *
   * @return
   */
  public String nextID() {
    SimpleDateFormat sdf = new SimpleDateFormat("yyMMddHHmmss");
    String timestamp = sdf.format(new Date());
    DecimalFormat df = new DecimalFormat("000");

    // 生成请求序列号
    short sequenceNo = nextSequence();

    return "0049" + timestamp + df.format(sequenceNo);
  }

  /**
   * 返回该类的唯一实例
   *
   * @return
   */
  public static RequestIDGenerator getInstance() {
    return INSTANCE;
  }
}