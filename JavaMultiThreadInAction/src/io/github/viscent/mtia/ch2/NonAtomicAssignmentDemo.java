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

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * 本Demo必须使用32位Java虚拟机才能看到非原子操作的效果. <br>
 * 运行本Demo时也可以指定虚拟机参数“-client”
 *
 * @author Viscent Huang
 */
public class NonAtomicAssignmentDemo implements Runnable {
  static long value = 0;
  private final long valueToSet;

  public NonAtomicAssignmentDemo(long valueToSet) {
    this.valueToSet = valueToSet;
  }

  public static void main(String[] args) {
    // 线程updateThread1将data更新为0
    Thread updateThread1 = new Thread(new NonAtomicAssignmentDemo(0L));
    // 线程updateThread2将data更新为-1
    Thread updateThread2 = new Thread(new NonAtomicAssignmentDemo(-1L));
    updateThread1.start();
    updateThread2.start();
    // 不进行实际输出的OutputStream
    final DummyOutputStream dos = new DummyOutputStream();
    try (PrintStream dummyPrintSteam = new PrintStream(dos);) {
      // 共享变量value的快照（即瞬间值）
      long snapshot;
      while (0 == (snapshot = value) || -1 == snapshot) {
        // 不进行实际的输出，仅仅是为了阻止JIT编译器做循环不变表达式外提优化
        dummyPrintSteam.print(snapshot);
      }
      System.err.printf("Unexpected data: %d(0x%016x)", snapshot, snapshot);
    }
    System.exit(0);
  }

  static class DummyOutputStream extends OutputStream {
    @Override
    public void write(int b) throws IOException {
      // 不实际进行输出
    }
  }

  @SuppressFBWarnings(value = "ST_WRITE_TO_STATIC_FROM_INSTANCE_METHOD",
      justification = "特意为之")
  @Override
  public void run() {
    for (;;) {
      value = valueToSet;
    }
  }
}