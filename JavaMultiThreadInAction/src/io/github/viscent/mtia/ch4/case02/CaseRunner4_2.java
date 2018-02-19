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

import io.github.viscent.mtia.util.AppWrapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.SequenceInputStream;
import java.lang.reflect.Constructor;
import java.util.Enumeration;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 运行程序前请先解压缩数据文件： src/data/ch4case02/InputFiles.zip。 <br>
 * 参考命令如下。
 * java -Xms96m -Xmx128m -XX:NewSize=64m -XX:SurvivorRatio=32
 * -Dx.stat.task=io.github.viscent.mtia.ch4.case02.MultithreadedStatTask -Dx.input.buffer=8192
 * -Dx.block.size=2000 io.github.viscent.mtia.ch4.case02.CaseRunner4_2
 *
 * @author Viscent Huang
 */
public class CaseRunner4_2 {

  public static void main(String[] args) throws Exception {
    AppWrapper.invokeMain0(CaseRunner4_2.class, args, false);
  }

  public static void main0(String[] args) throws Exception {
    int argc = args.length;
    // 根据指定的日志文件创建唯一一个输入流
    InputStream in = createInputStream();
    // 一对请求与响应之间的消息唯一标识的后3位值之差
    int traceIdDiff;
    // 待统计的操作名称
    String expectedOperationName;
    // 可选参数：采样周期（单位：秒）
    int sampleInterval;
    /*
     * 可选参数：指定一个以逗号分割的列表，仅发送给该列表中的设备的请求才会被统计在内。 默认值"*"表示不对外部设备做要求。
     */
    String expectedExternalDeviceList;

    traceIdDiff = argc >= 1 ? Integer.valueOf(args[0]) : 3;
    expectedOperationName = argc >= 2 ? args[1] : "sendSms";
    sampleInterval = argc >= 3 ? Integer.valueOf(args[2]) : 10;
    expectedExternalDeviceList = argc >= 4 ? args[3] : "*";

    // 创建执行统计的任务实例
    Runnable task = createTask(in, sampleInterval, traceIdDiff,
        expectedOperationName, expectedExternalDeviceList);

    // 直接在main线程中执行统计任务
    task.run();
  }

  private static Runnable createTask(InputStream in, int sampleInterval,
      int traceIdDiff, String expectedOperationName,
      String expectedExternalDeviceList) throws Exception {
    String taskClazz = System.getProperty("x.stat.task");

    taskClazz = null == taskClazz ? "io.github.viscent.mtia.ch4.case02.MultithreadedStatTask"
        : taskClazz;

    Class<?> clazz = Class.forName(taskClazz);
    Constructor<?> constructor = clazz.getConstructor(new Class[] {
        InputStream.class, int.class, int.class, String.class, String.class });

    Runnable st = (Runnable) constructor.newInstance(new Object[] { in,
        sampleInterval, traceIdDiff, expectedOperationName,
        expectedExternalDeviceList });
    return st;
  }

  private static InputStream createInputStream() {
    final AtomicBoolean readerClosed = new AtomicBoolean(false);
    InputStream dataIn = CaseRunner4_2.class
        .getResourceAsStream("/data/ch4case02/in.dat");
    final BufferedReader bfr = new BufferedReader(new InputStreamReader(
        dataIn)) {
      @Override
      public void close() throws IOException {
        super.close();
        readerClosed.set(true);
      }
    };
    SequenceInputStream si = new SequenceInputStream(
        new Enumeration<InputStream>() {
          String fileName = null;

          @Override
          public boolean hasMoreElements() {
            if (readerClosed.get()) {
              return false;
            }
            try {
              fileName = bfr.readLine();
              if (null == fileName) {
                bfr.close();
              }
            } catch (IOException e) {
              e.printStackTrace();
              fileName = null;
            }
            return null != fileName;
          }

          @Override
          public InputStream nextElement() {
            InputStream in = null;
            if (null != fileName) {
              try {
                in = CaseRunner4_2.class.getResourceAsStream("/data/ch4case02/"
                    + fileName);
              } catch (Exception e) {
                e.printStackTrace();
              }
            }
            return in;
          }

        });

    return si;

  }

}
