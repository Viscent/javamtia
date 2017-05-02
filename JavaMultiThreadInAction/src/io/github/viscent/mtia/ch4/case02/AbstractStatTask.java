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
package io.github.viscent.mtia.ch4.case02;

import java.io.IOException;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Map;
import java.util.SimpleTimeZone;

/**
 * 对统计程序的算法步骤进行抽象。
 *
 * @author Viscent Huang
 */
public abstract class AbstractStatTask implements Runnable {
  private static final String TIME_STAMP_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";
  private final Calendar calendar;
  // 此处是单线程访问，故其使用是线程安全的
  private final SimpleDateFormat sdf;
  // 采样周期，单位：s
  private final int sampleInterval;
  // 统计处理逻辑类
  protected final StatProcessor recordProcessor;

  public AbstractStatTask(int sampleInterval, int traceIdDiff,
      String expectedOperationName, String expectedExternalDeviceList) {
    this(sampleInterval, new RecordProcessor(sampleInterval,
        traceIdDiff,
        expectedOperationName, expectedExternalDeviceList));
  }

  public AbstractStatTask(int sampleInterval,
      StatProcessor recordProcessor) {
    SimpleTimeZone stz = new SimpleTimeZone(0, "UTC");
    this.sdf = new SimpleDateFormat(TIME_STAMP_FORMAT);
    sdf.setTimeZone(stz);
    this.calendar = Calendar.getInstance(stz);
    this.sampleInterval = sampleInterval;
    this.recordProcessor = recordProcessor;
  }

  /**
   * 留给子类用于实现统计操作的抽象方法。
   */
  protected abstract void doCalculate() throws IOException,
      InterruptedException;

  @Override
  public void run() {
    // 执行统计逻辑
    try {
      doCalculate();
    } catch (Exception e) {
      e.printStackTrace();
      return;
    }
    // 获取统计结果
    Map<Long, DelayItem> result = recordProcessor.getResult();
    // 输出统计结果
    report(result);
  }

  protected void report(Map<Long, DelayItem> summaryResult) {
    int sampleCount;
    final PrintStream ps = System.out;
    ps.printf("%s\t\t%s\t%s\t%s%n",
        "Timestamp", "AvgDelay(ms)", "TPS", "SampleCount");
    for (DelayItem delayStatData : summaryResult.values()) {
      sampleCount = delayStatData.getSampleCount().get();
      ps.printf("%s%8d%8d%8d%n",
          getUTCTimeStamp(delayStatData
              .getTimeStamp()), delayStatData.getTotalDelay().get()
              / sampleCount,
          sampleCount
              / sampleInterval, sampleCount);
    }
  }

  private String getUTCTimeStamp(long timeStamp) {
    calendar.setTimeInMillis(timeStamp);
    String tempTs = sdf.format(calendar.getTime());
    return tempTs;
  }
}