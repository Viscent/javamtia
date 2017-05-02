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

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.ReentrantLock;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

public class BuggyLckBasedPhilosopher extends AbstractPhilosopher {
  /**
   * 为确保每个Chopstick实例有且仅有一个显式锁（而不重复创建）与之对应,<br>
   * 这里的map必须采用static修饰!
   */
  protected final static ConcurrentMap<Chopstick, ReentrantLock> LOCK_MAP;
  static {
    LOCK_MAP = new ConcurrentHashMap<Chopstick, ReentrantLock>();
  }

  public BuggyLckBasedPhilosopher(int id, Chopstick left, Chopstick right) {
    super(id, left, right);
    // 每个筷子对应一个(唯一)锁实例
    LOCK_MAP.putIfAbsent(left, new ReentrantLock());
    LOCK_MAP.putIfAbsent(right, new ReentrantLock());
  }

  @Override
  public void eat() {
    // 先后拿起左手边和右手边的筷子
    if (pickUpChopstick(left) && pickUpChopstick(right)) {
      // 同时拿起两根筷子的时候才能够吃饭
      try{
        doEat();
      } finally {
        // 放下筷子
        putDownChopsticks(right, left);
      }
    }
  }

  @SuppressFBWarnings(value = "UL_UNRELEASED_LOCK",
      justification = "筷子对应的锁由应用自身保障总是能够被释放")
  protected boolean pickUpChopstick(Chopstick chopstick) {
    final ReentrantLock lock = LOCK_MAP.get(chopstick);
    lock.lock();
    try {
      Debug.info("%s is picking up %s on his %s...%n",
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

  private void putDownChopsticks(Chopstick chopstick1, Chopstick chopstick2) {
    try {
      putDownChopstick(chopstick1);
    } finally {
      putDownChopstick(chopstick2);
    }
  }

  protected void putDownChopstick(Chopstick chopstick) {
    final ReentrantLock lock = LOCK_MAP.get(chopstick);
    try {
      Debug.info("%s is putting down %s on his %s...%n",
          this, chopstick, chopstick == left ? "left" : "right");
      chopstick.putDown();
    } finally {
      lock.unlock();
    }
  }
}