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
package io.github.viscent.mtia.ch10;

import static org.junit.Assert.assertTrue;
import io.github.viscent.mtia.ch4.case02.LogReaderThread;
import io.github.viscent.mtia.ch4.case02.RecordSet;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class LogReaderThreadTest {
  private LogReaderThread logReader;
  private StringBuilder sdb;

  @Before
  public void setUp() throws Exception {
    sdb = new StringBuilder();
    sdb.append("2016-03-30 09:33:04.644|SOAP|request|SMS|sendSms|OSG|ESB|00200000000|192.168.1.102|13612345678|136712345670");
    sdb.append("\n2016-03-30 09:33:04.688|SOAP|response|SMS|sendSmsRsp|ESB|OSG|00200000000|192.168.1.102|13612345678|136712345670");
    sdb.append("\n2016-03-30 09:33:04.732|SOAP|request|SMS|sendSms|ESB|NIG|00210000001|192.168.1.102|13612345678|136712345670");
    sdb.append("\n2016-03-30 09:33:04.772|SOAP|response|SMS|sendSmsRsp|NIG|ESB|00210000004|192.168.1.102|13612345678|136712345670\n");

    InputStream in = new ByteArrayInputStream(sdb.toString().getBytes("UTF-8"));
    logReader = new LogReaderThread(in, 1024, 4);
    logReader.start();
  }

  @After
  public void tearDown() throws Exception {
    logReader.interrupt();
  }

  @Test
  public void testNextBatch() {
    try {
      RecordSet rs = logReader.nextBatch();
      StringBuilder contents = new StringBuilder();
      String record;
      while (null != (record = rs.nextRecord())) {
        contents.append(record).append("\n");
      }
      assertTrue(contents.toString().equals(sdb.toString()));
    } catch (InterruptedException ignored) {
    }
  }
}
