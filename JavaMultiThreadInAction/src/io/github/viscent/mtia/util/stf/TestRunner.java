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
package io.github.viscent.mtia.util.stf;

import io.github.viscent.mtia.util.Tools;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.SortedMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@ConcurrencyTest
public class TestRunner {
  private static final Semaphore FLOW_CONTROL = new Semaphore(Runtime
      .getRuntime().availableProcessors());

  private static final ExecutorService EXECUTOR_SERVICE = Executors
      .newCachedThreadPool(new ThreadFactory() {

        @Override
        public Thread newThread(Runnable r) {
          Thread t = new Thread(r);
          t.setPriority(Thread.MAX_PRIORITY);
          t.setDaemon(false);
          return t;
        }
      });

  private volatile boolean stop = false;
  private final AtomicInteger runs = new AtomicInteger(0);
  private final int iterations;
  private final int thinkTime;
  private final Method publishMethod;
  private final Method observerMethod;
  private volatile Method setupMethod = null;
  private final Object testCase;
  private final SortedMap<Integer, ExpectInfo> expectMap;

  public TestRunner(Method publishMethod, Method observerMethod,
      Method setupMethod, Object testCase) {
    this.publishMethod = publishMethod;
    this.observerMethod = observerMethod;
    this.setupMethod = setupMethod;
    this.testCase = testCase;
    this.expectMap = parseExpects(getExpects(observerMethod));
    ConcurrencyTest testCaseAnn = testCase.getClass().getAnnotation(
        ConcurrencyTest.class);
    iterations = testCaseAnn.iterations();
    thinkTime = testCaseAnn.thinkTime();

  }

  private static class ExpectInfo {
    public final String description;
    private final AtomicInteger counter;

    public ExpectInfo(String description) {
      this(description, 0);
    }

    public ExpectInfo(String description, int hitCount) {
      this.description = description;
      this.counter = new AtomicInteger(hitCount);
    }

    public int hit() {
      return counter.incrementAndGet();
    }

    public int count() {
      return counter.get();
    }

  }

  public static void runTest(Class<?> testCaseClazz)
      throws InstantiationException, IllegalAccessException {

    Object test = testCaseClazz.newInstance();
    Method publishMethod = null;
    Method observerMethod = null;
    Method setupMethod = null;
    for (Method method : testCaseClazz.getMethods()) {
      if (method.getAnnotation(Actor.class) != null) {
        publishMethod = method;
      }
      if (method.getAnnotation(Observer.class) != null) {
        observerMethod = method;
      }
      if (method.getAnnotation(Setup.class) != null) {
        setupMethod = method;
      }
    }

    TestRunner runner = new TestRunner(publishMethod, observerMethod,
        setupMethod, test);
    runner.doTest();

  }

  private static Expect[] getExpects(final Method observerMethod) {
    Observer observerAnn = observerMethod.getAnnotation(Observer.class);
    Expect[] expects = observerAnn.value();
    return expects;
  }

  private static SortedMap<Integer, ExpectInfo> parseExpects(
      final Expect[] expects) {
    SortedMap<Integer, ExpectInfo> map = new ConcurrentSkipListMap<Integer, ExpectInfo>();
    for (Expect expect : expects) {
      map.put(Integer.valueOf(expect.expected()), new ExpectInfo(expect.desc()));
    }
    return map;
  }

  protected void doTest() {

    Runnable publishTask = new Runnable() {

      @Override
      public void run() {
        try {
          publishMethod.invoke(testCase, new Object[] {});
        } catch (IllegalAccessException e) {
          e.printStackTrace();
        } catch (IllegalArgumentException e) {
          e.printStackTrace();
        } catch (InvocationTargetException e) {
          e.printStackTrace();
        } finally {
          FLOW_CONTROL.release(1);
        }

      }

    };

    Runnable observerTask = new Runnable() {

      @SuppressWarnings("unchecked")
      @Override
      public void run() {
        try {
          int result = -1;
          try {
            result = Integer.valueOf(observerMethod.invoke(testCase,
                new Object[] {}).toString());
            ExpectInfo expectInfo = expectMap.get(Integer.valueOf(result));
            if (null != expectInfo) {
              expectInfo.hit();
            } else {
              expectInfo = new ExpectInfo("unexpected", 1);
              ((ConcurrentMap<Integer, ExpectInfo>) expectMap).putIfAbsent(
                  result, expectInfo);
            }
          } catch (IllegalAccessException e) {
            e.printStackTrace();
          } catch (IllegalArgumentException e) {
            e.printStackTrace();
          } catch (InvocationTargetException e) {
            e.printStackTrace();
          }

        } finally {
          FLOW_CONTROL.release(1);
        }
      }

    };

    CountDownLatch latch;
    while (!stop) {

      latch = createLatch();
      if (null != setupMethod) {
        try {
          setupMethod.invoke(testCase, new Object[] {});
        } catch (Exception e) {
          break;
        }
      }

      schedule(observerTask, latch);
      schedule(publishTask, latch);

      if (runs.incrementAndGet() >= iterations) {
        break;
      }
      if (thinkTime > 0) {
        Tools.randomPause(thinkTime);
      }

      try {
        latch.await();
      } catch (InterruptedException e) {
        ;
      }
    }

    EXECUTOR_SERVICE.shutdown();
    try {
      EXECUTOR_SERVICE.awaitTermination(2000, TimeUnit.MINUTES);
    } catch (InterruptedException e) {
      ;
    }

    report();
  }

  private static class DummyLatch extends CountDownLatch {

    public DummyLatch(int count) {
      super(count);
    }

    @Override
    public void await() throws InterruptedException {
      ;
    }

    @Override
    public boolean await(long timeout, TimeUnit unit)
        throws InterruptedException {
      return true;
    }

    @Override
    public void countDown() {
      ;
    }

    @Override
    public long getCount() {
      return 0;
    }
  }

  private CountDownLatch createLatch() {
    CountDownLatch latch;
    if (null != setupMethod) {
      latch = new CountDownLatch(2);
    } else {
      latch = new DummyLatch(2);
    }
    return latch;
  }

  protected void report() {
    ExpectInfo ei;
    StringBuilder sbd = new StringBuilder();
    sbd.append("\n\r<<Simple Concurrency Test Framework report>>:");
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss E");
    sbd.append("\n\r===========================" + sdf.format(new Date())
        + "=================================");
    for (Map.Entry<Integer, ExpectInfo> entry : expectMap.entrySet()) {
      ei = entry.getValue();
      sbd.append("\n\rexpected:" + entry.getKey() + "		occurrences:"
          + ei.count() + "		==>" + ei.description);
    }
    sbd.append("\n\r=====================================END=============================================");
    System.out.println(sbd);
  }

  protected void schedule(final Runnable task, final CountDownLatch latch) {
    try {
      FLOW_CONTROL.acquire(1);
    } catch (InterruptedException e) {
      latch.countDown();
      return;
    }
    EXECUTOR_SERVICE.submit(new Runnable() {

      @Override
      public void run() {

        try {
          task.run();
        } finally {
          FLOW_CONTROL.release(1);
          latch.countDown();
        }

      }

    });
  }

}
