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
package io.github.viscent.mtia.ch5.case01;

import io.github.viscent.mtia.util.Debug;
import io.github.viscent.mtia.util.Tools;

import java.util.Random;

/**
 * 告警代理
 *
 * @author Viscent Huang
 */
public class AlarmAgent {
  // 保存该类的唯一实例
  private final static AlarmAgent INSTANCE = new AlarmAgent();
  // 是否连接上告警服务器
  private boolean connectedToServer = false;
  // 心跳线程，用于检测告警代理与告警服务器的网络连接是否正常
  private final HeartbeartThread heartbeatThread = new HeartbeartThread();

  private AlarmAgent() {
    // 什么也不做
  }

  public static AlarmAgent getInstance() {
    return INSTANCE;
  }

  public void init() {
    connectToServer();
    heartbeatThread.setDaemon(true);
    heartbeatThread.start();
  }

  private void connectToServer() {
    // 创建并启动网络连接线程，在该线程中与告警服务器建立连接
    new Thread() {
      @Override
      public void run() {
        doConnect();
      }
    }.start();
  }

  private void doConnect() {
    // 模拟实际操作耗时
    Tools.randomPause(100);
    synchronized (this) {
      connectedToServer = true;
      // 连接已经建立完毕，通知以唤醒告警发送线程
      notify();
    }
  }

  public void sendAlarm(String message) throws InterruptedException {
    synchronized (this) {
      // 使当前线程等待直到告警代理与告警服务器的连接建立完毕或者恢复
      while (!connectedToServer) {
        Debug.info("Alarm agent was not connected to server.");
        wait();
      }
      // 真正将告警消息上报到告警服务器
      doSendAlarm(message);
    }
  }

  private void doSendAlarm(String message) {
    // ...
    Debug.info("Alarm sent:%s", message);
  }

  // 心跳线程
  class HeartbeartThread extends Thread {
    @Override
    public void run() {
      try {
        // 留一定的时间给网络连接线程与告警服务器建立连接
        Thread.sleep(1000);
        while (true) {
          if (checkConnection()) {
            connectedToServer = true;
          } else {
            connectedToServer = false;
            Debug.info("Alarm agent was disconnected from server.");

            // 检测到连接中断，重新建立连接
            connectToServer();
          }
          Thread.sleep(2000);
        }
      } catch (InterruptedException e) {
        // 什么也不做;
      }
    }

    // 检测与告警服务器的网络连接情况
    private boolean checkConnection() {
      boolean isConnected = true;
      final Random random = new Random();

      // 模拟随机性的网络断链
      int rand = random.nextInt(1000);
      if (rand <= 500) {
        isConnected = false;
      }
      return isConnected;
    }
  }
}
