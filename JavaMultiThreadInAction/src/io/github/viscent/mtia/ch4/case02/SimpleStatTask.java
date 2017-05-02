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

public class SimpleStatTask extends AbstractStatTask {
  private final InputStream in;

  public SimpleStatTask(InputStream in, int sampleInterval, int traceIdDiff,
      String expectedOperationName, String expectedExternalDeviceList) {
    super(sampleInterval, traceIdDiff, expectedOperationName,
        expectedExternalDeviceList);
    this.in = in;
  }

  @Override
  protected void doCalculate() throws IOException, InterruptedException {
    String strBufferSize = System.getProperty("x.input.buffer");
    int inputBufferSize = null != strBufferSize ? Integer
        .valueOf(strBufferSize) : 8192 * 4;
    final BufferedReader logFileReader = new BufferedReader(
        new InputStreamReader(in), inputBufferSize);
    String record;
    try {
      while ((record = logFileReader.readLine()) != null) {
        // 实例变量recordProcessor是在AbstractStatTask中定义的
        recordProcessor.process(record);
      }
    } finally {
      Tools.silentClose(logFileReader);
    }
  }
}