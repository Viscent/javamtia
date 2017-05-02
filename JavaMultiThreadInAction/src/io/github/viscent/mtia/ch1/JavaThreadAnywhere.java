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
package io.github.viscent.mtia.ch1;

public class JavaThreadAnywhere {

  public static void main(String[] args) {
    // 获取当前线程
    Thread currentThread = Thread.currentThread();

    // 获取当前线程的线程名称
    String currentThreadName = currentThread.getName();

    System.out.printf("The main method was executed by thread:%s",
        currentThreadName);
    Helper helper = new Helper("Java Thread AnyWhere");
    helper.run();
  }

  static class Helper implements Runnable {
    private final String message;

    public Helper(String message) {
      this.message = message;
    }

    private void doSomething(String message) {
      // 获取当前线程
      Thread currentThread = Thread.currentThread();

      // 获取当前线程的线程名称
      String currentThreadName = currentThread.getName();

      System.out.printf("The doSomething method was executed by thread:%s",
          currentThreadName);
      System.out.println("Do something with " + message);
    }

    @Override
    public void run() {
      doSomething(message);
    }
  }
}