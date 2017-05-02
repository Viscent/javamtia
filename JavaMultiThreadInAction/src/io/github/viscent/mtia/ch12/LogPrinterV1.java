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
package io.github.viscent.mtia.ch12;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

public class LogPrinterV1 {
  final static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat(
      "yyMMddHHmm");
  final static DecimalFormat DECIMAL_FORMAT = new DecimalFormat("00");
  final static int MAX_RECORDS_PER_FILE = 10000;
  private PrintWriter pwr = null;
  private int recordsInFile = MAX_RECORDS_PER_FILE;
  private int fileSeq = 0;

  public void print(String record) {
    PrintWriter writer;
    try {
      synchronized (this) {
        writer = getPrintWriter();
        writer.println(record);
        recordsInFile++;
      }

    } catch (Exception e) {
      e.printStackTrace();
    }

  }

  public synchronized void shutdown() {
    if (null != pwr) {
      pwr.close();
    }
  }

  private PrintWriter getPrintWriter() throws IOException {
    PrintWriter writer = null;
    if (MAX_RECORDS_PER_FILE == recordsInFile) {
      String newFileName = retrieveFileName();
      writer = new PrintWriter(newFileName);
      recordsInFile = 0;
      if (null != pwr) {
        pwr.flush();
        pwr.close();
      }
      pwr = writer;
    } else {
      writer = pwr;
    }
    return writer;
  }

  @SuppressFBWarnings(value = "STCAL_INVOKE_ON_STATIC_DATE_FORMAT_INSTANCE",
      justification = "该方法最终是通过一个同步块访问的，因此对DATE_FORMAT的访问并无线程安全问题")
  protected String retrieveFileName() {
    String fileName;
    fileName = "/home/viscent/tmp/logs/" + DATE_FORMAT.format(new Date())
        + DECIMAL_FORMAT.format(fileSeq) + ".log";
    if (++fileSeq > 99) {
      fileSeq = 0;
    }
    return fileName;
  }
}