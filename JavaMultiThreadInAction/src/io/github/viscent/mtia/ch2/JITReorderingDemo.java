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

import io.github.viscent.mtia.util.stf.Actor;
import io.github.viscent.mtia.util.stf.ConcurrencyTest;
import io.github.viscent.mtia.util.stf.Expect;
import io.github.viscent.mtia.util.stf.Observer;
import io.github.viscent.mtia.util.stf.TestRunner;

/**
 * 再现JIT指令重排序的Demo
 *
 * @author Viscent Huang
 */
@ConcurrencyTest(iterations = 200000)
public class JITReorderingDemo {
  private int externalData = 1;
  private Helper helper;

  @Actor
  public void createHelper() {
    helper = new Helper(externalData);
  }

  @Observer({
      @Expect(desc = "Helper is null", expected = -1),
      @Expect(desc = "Helper is not null,but it is not initialized",
          expected = 0),
      @Expect(desc = "Only 1 field of Helper instance was initialized",
          expected = 1),
      @Expect(desc = "Only 2 fields of Helper instance were initialized",
          expected = 2),
      @Expect(desc = "Only 3 fields of Helper instance were initialized",
          expected = 3),
      @Expect(desc = "Helper instance was fully initialized", expected = 4) })
  public int consume() {
    int sum = 0;

    /*
     * 由于我们未对共享变量helper进行任何处理（比如采用volatile关键字修饰该变量），
     * 因此，这里可能存在可见性问题，即当前线程读取到的变量值可能为null。
     */
    final Helper observedHelper = helper;
    if (null == observedHelper) {
      sum = -1;
    } else {
      sum = observedHelper.payloadA + observedHelper.payloadB
          + observedHelper.payloadC + observedHelper.payloadD;
    }

    return sum;
  }

  static class Helper {
    int payloadA;
    int payloadB;
    int payloadC;
    int payloadD;

    public Helper(int externalData) {
      this.payloadA = externalData;
      this.payloadB = externalData;
      this.payloadC = externalData;
      this.payloadD = externalData;
    }

    @Override
    public String toString() {
      return "Helper [" + payloadA + ", " + payloadB + ", " + payloadC + ", "
          + payloadD + "]";
    }

  }

  public static void main(String[] args) throws InstantiationException,
      IllegalAccessException {
    // 调用测试工具运行测试代码
    TestRunner.runTest(JITReorderingDemo.class);
  }
}
