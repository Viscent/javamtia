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
package io.github.viscent.mtia.ch8;

import io.github.viscent.mtia.util.Debug;
import io.github.viscent.mtia.util.Tools;

import java.util.Scanner;

public class ThreadPauseDemo {
  final static PauseControl pc = new PauseControl();

  public static void main(String[] args) {
    final Runnable action = new Runnable() {
      @Override
      public void run() {
        Debug.info("Master,I'm working...");
        Tools.randomPause(300);
      }
    };
    Thread slave = new Thread() {
      @Override
      public void run() {
        try {
          for (;;) {
            pc.pauseIfNeccessary(action);
          }
        } catch (InterruptedException e) {
          // 什么也不做
        }
      }
    };
    slave.setDaemon(true);
    slave.start();
    askOnBehaveOfSlave();
  }

  static void askOnBehaveOfSlave() {
    String answer;
    int minPause = 2000;
    try (Scanner sc = new Scanner(System.in)) {
      for (;;) {
        Tools.randomPause(8000, minPause);
        pc.requestPause();
        Debug.info("Master,may I take a rest now?%n");
        Debug.info("%n(1) OK,you may take a rest%n"
            + "(2) No, Keep working!%nPress any other key to quit:%n");
        answer = sc.next();
        if ("1".equals(answer)) {
          pc.requestPause();
          Debug.info("Thank you,my master!");
          minPause = 8000;
        } else if ("2".equals(answer)) {
          Debug.info("Yes,my master!");
          pc.proceed();
          minPause = 2000;
        } else {
          break;
        }
      }// for结束
    }// try结束
    Debug.info("Game over!");
  }
}