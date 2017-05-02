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
package io.github.viscent.mtia.ch3.rwlock;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ReadWriteLockDowngrade {
  private final ReadWriteLock rwLock = new ReentrantReadWriteLock();
  private final Lock readLock = rwLock.readLock();
  private final Lock writeLock = rwLock.writeLock();

  public void operationWithLockDowngrade() {
    boolean readLockAcquired = false;
    writeLock.lock(); // 申请写锁
    try {
      // 对共享数据进行更新
      // ...
      // 当前线程在持有写锁的情况下申请读锁readLock
      readLock.lock();
      readLockAcquired = true;
    } finally {
      writeLock.unlock();// 释放写锁
    }

    if (readLockAcquired) {
      try {
        // 读取共享数据并据此执行其他操作
        // ...

      } finally {
        readLock.unlock();// 释放读锁
      }
    } else {
      // ...
    }
  }
}
