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

public class SpeculativeLoadExample {
  private boolean ready = false;
  private int[] data = new int[] { 1, 2, 3, 4, 5, 6, 7, 8 };

  public void writer() {
    int[] newData = new int[] { 1, 2, 3, 4, 5, 6, 7, 8 };
    for (int i = 0; i < newData.length; i++) {// 语句①（for循环语句）

      // 此处包含读内存的操作
      newData[i] = newData[i] - i;
    }
    data = newData;
    // 此处包含写内存的操作
    ready = true;// 语句②
  }

  public int reader() {
    int sum = 0;
    int[] snapshot;
    if (ready) {// 语句③（if语句）
      snapshot = data;
      for (int i = 0; i < snapshot.length; i++) {// 语句④（for循环语句）
        sum += snapshot[i];// 语句⑤
      }

    }
    return sum;
  }
}