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
package io.github.viscent.mtia.ch7.diningphilosophers;

import io.github.viscent.mtia.util.Debug;

import java.util.concurrent.locks.ReentrantLock;

public class RecoverablePhilosopher extends BuggyLckBasedPhilosopher {

  public RecoverablePhilosopher(int id, Chopstick left, Chopstick right) {
    super(id, left, right);
  }

  @Override
  protected boolean pickUpChopstick(Chopstick chopstick) {
    final ReentrantLock lock = LOCK_MAP.get(chopstick);
    try {
      lock.lockInterruptibly();
    } catch (InterruptedException e) {
      // 使当前线程释放其已持有的锁
      Debug.info("%s detected interrupt.", Thread.currentThread().getName());
      Chopstick theOtherChopstick = chopstick == left ? right : left;
      theOtherChopstick.putDown();
      LOCK_MAP.get(theOtherChopstick).unlock();
      return false;
    }
    try {
      Debug.info(
          "%s is picking up %s on his %s...%n",
          this, chopstick, chopstick == left ? "left" : "right");

      chopstick.pickUp();
    } catch (Exception e) {
      // 不大可能走到这里
      e.printStackTrace();
      lock.unlock();
      return false;
    }
    return true;
  }
}
