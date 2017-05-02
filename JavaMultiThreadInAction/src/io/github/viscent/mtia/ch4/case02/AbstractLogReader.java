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

import io.github.viscent.mtia.util.Tools;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * 日志文件读取线程。
 *
 * @author Viscent Huang
 */
public abstract class AbstractLogReader extends Thread {
  protected final BufferedReader logFileReader;
  // 表示日志文件是否读取结束
  protected volatile boolean isEOF = false;
  protected final int batchSize;

  public AbstractLogReader(InputStream in, int inputBufferSize, int batchSize) {
    logFileReader = new BufferedReader(new InputStreamReader(in),
        inputBufferSize);
    this.batchSize = batchSize;
  }

  protected RecordSet getNextToFill() {
    return new RecordSet(batchSize);
  }

  /* 留给子类实现的抽象方法 */
  // 获取下一个记录集
  protected abstract RecordSet nextBatch()
      throws InterruptedException;

  // 发布指定的记录集
  protected abstract void publish(RecordSet recordBatch)
      throws InterruptedException;

  @Override
  public void run() {
    RecordSet recordSet;
    boolean eof = false;
    try {
      while (true) {
        recordSet = getNextToFill();
        recordSet.reset();
        eof = doFill(recordSet);
        publish(recordSet);
        if (eof) {
          if (!recordSet.isEmpty()) {
            publish(new RecordSet(1));
          }
          isEOF = eof;
          break;
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      Tools.silentClose(logFileReader);
    }
  }

  protected boolean doFill(final RecordSet recordSet) throws IOException {
    final int capacity = recordSet.capacity;
    String record;
    for (int i = 0; i < capacity; i++) {
      record = logFileReader.readLine();
      if (null == record) {
        return true;
      }
      // 将读取到的日志记录存入指定的记录集
      recordSet.putRecord(record);
    }
    return false;
  }
}