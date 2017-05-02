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
package io.github.viscent.mtia.ch9;

import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.logging.Level;
import java.util.logging.Logger;



/**
 * 能够被重复执行的抽象异步任务。
 *
 * @author Viscent Huang
 */
public abstract class AsyncTask<V> implements Runnable,
    Callable<V> {
  final static Logger LOGGER = Logger.getAnonymousLogger();
  protected final Executor executor;

  public AsyncTask(Executor executor) {
    this.executor = executor;
  }

  public AsyncTask() {
    this(new Executor() {
      @Override
      public void execute(Runnable command) {
        command.run();
      }
    });
  }

  @Override
  public void run() {
    Exception exp = null;
    V r = null;
    try {
      r = call();
    } catch (Exception e) {
      exp = e;
    }

    final V result = r;
    if (null == exp) {
      executor.execute(new Runnable() {
        @Override
        public void run() {
          onResult(result);
        }
      });
    } else {
      final Exception exceptionCaught = exp;
      executor.execute(new Runnable() {
        @Override
        public void run() {
          onError(exceptionCaught);
        }
      });
    }
  }// run结束

  /**
   * 留给子类实现任务执行结果的处理逻辑。
   *
   * @param result
   *          任务执行结果
   */
  protected abstract void onResult(V result);

  /**
   * 子类可覆盖该方法来对任务执行过程中抛出的异常进行处理。
   *
   * @param e
   *          任务执行过程中抛出的异常
   */
  protected void onError(Exception e) {
    LOGGER.log(Level.SEVERE, "AsyncTask[" + this + "] failed.", e);
  }
}