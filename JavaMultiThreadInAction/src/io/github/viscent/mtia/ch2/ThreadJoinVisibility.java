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

import io.github.viscent.mtia.util.Tools;

public class ThreadJoinVisibility {
  // 线程间的共享变量
  static int data = 0;

  public static void main(String[] args) {

    Thread thread = new Thread() {
      @Override
      public void run() {
        // 使当前线程休眠R毫秒（R的值为随机数）
        Tools.randomPause(50);

        // 更新data的值
        data = 1;
      }
    };

    thread.start();

    // 等待线程thread结束后，main线程才继续运行
    try {
      thread.join();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    // 读取并打印变量data的值
    System.out.println(data);

  }
}
