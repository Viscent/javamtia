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
import java.io.InputStream;

public class MultithreadedStatTask extends AbstractStatTask {
  // 日志文件输入缓冲大小
  protected final int inputBufferSize;
  // 日志记录集大小
  protected final int batchSize;
  // 日志文件输入流
  protected final InputStream in;

  /* 实例初始化块 */
  {
    String strBufferSize = System.getProperty("x.input.buffer");
    inputBufferSize = null != strBufferSize ? Integer.valueOf(strBufferSize)
        : 8192;
    String strBatchSize = System.getProperty("x.batch.size");
    batchSize = null != strBatchSize ? Integer.valueOf(strBatchSize) : 2000;
  }

  public MultithreadedStatTask(int sampleInterval,
      StatProcessor recordProcessor) {
    super(sampleInterval, recordProcessor);
    this.in = null;
  }

  public MultithreadedStatTask(InputStream in, int sampleInterval,
      int traceIdDiff,
      String expectedOperationName, String expectedExternalDeviceList) {
    super(sampleInterval, traceIdDiff, expectedOperationName,
        expectedExternalDeviceList);
    this.in = in;
  }

  @Override
  protected void doCalculate() throws IOException, InterruptedException {
    final AbstractLogReader logReaderThread = createLogReader();
    // 启动工作者线程
    logReaderThread.start();
    RecordSet recordSet;
    String record;
    for (;;) {
      recordSet = logReaderThread.nextBatch();
      if (null == recordSet) {
        break;
      }
      while (null != (record = recordSet.nextRecord())) {
        // 实例变量recordProcessor是在AbstractStatTask中定义的
        recordProcessor.process(record);
      }
    }// for循环结束
  }

  protected AbstractLogReader createLogReader() {
    AbstractLogReader logReader = new LogReaderThread(in, inputBufferSize,
        batchSize);
    return logReader;
  }
}