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
package io.github.viscent.mtia.ch3;

import io.github.viscent.mtia.util.Debug;
import io.github.viscent.mtia.util.Tools;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLongFieldUpdater;

public class CASBasedCounter {
  private volatile long count;
  /**
   * 这里使用AtomicLongFieldUpdater只是为了便于讲解和运行该实例，
   * 实际上更多的情况是我们不使用AtomicLongFieldUpdater，而是使用
   * java.util.concurrent.atomic包下的其他更为直接的类。
   */
  private final AtomicLongFieldUpdater<CASBasedCounter> fieldUpdater;

  public CASBasedCounter() throws SecurityException, NoSuchFieldException {
    fieldUpdater = AtomicLongFieldUpdater.newUpdater(CASBasedCounter.class,
        "count");
  }

  public long vaule() {
    return count;
  }

  public void increment() {
    long oldValue;
    long newValue;
    do {
      oldValue = count;// 读取共享变量当前值
      newValue = oldValue + 1;// 计算共享变量的新值
    } while (/* 调用CAS来更新共享变量的值 */!compareAndSwap(oldValue, newValue));
  }

  /*
   * 该方法是个实例方法，且共享变量count是当前类的实例变量，因此这里我们没有必要在方法参数中声明一个表示共享变量的参数
   */
  private boolean compareAndSwap(long oldValue, long newValue) {
    boolean isOK = fieldUpdater.compareAndSet(this, oldValue, newValue);
    return isOK;
  }

  public static void main(String[] args) throws Exception {
    final CASBasedCounter counter = new CASBasedCounter();
    Thread t;
    Set<Thread> threads = new HashSet<Thread>();
    for (int i = 0; i < 20; i++) {
      t = new Thread(new Runnable() {
        @Override
        public void run() {
          Tools.randomPause(50);
          counter.increment();
        }
      });
      threads.add(t);
    }

    for (int i = 0; i < 8; i++) {
      t = new Thread(new Runnable() {
        @Override
        public void run() {
          Tools.randomPause(50);
          Debug.info(String.valueOf(counter.vaule()));
        }
      });
      threads.add(t);
    }

    // 启动并等待指定的线程结束
    Tools.startAndWaitTerminated(threads);
    Debug.info("final count:" + String.valueOf(counter.vaule()));
  }
}