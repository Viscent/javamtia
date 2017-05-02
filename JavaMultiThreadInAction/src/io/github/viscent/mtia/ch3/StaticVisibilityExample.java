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

import java.util.HashMap;
import java.util.Map;

public class StaticVisibilityExample {
  private static Map<String, String> taskConfig;
  static {
    Debug.info("The class being initialized...");
    taskConfig = new HashMap<String, String>();// 语句①
    taskConfig.put("url", "https://github.com/Viscent");// 语句②
    taskConfig.put("timeout", "1000");// 语句③
  }

  public static void changeConfig(String url, int timeout) {
    taskConfig = new HashMap<String, String>();// 语句④
    taskConfig.put("url", url);// 语句⑤
    taskConfig.put("timeout", String.valueOf(timeout));// 语句⑥
  }

  public static void init() {
    // 该线程至少能够看到语句①～语句③的操作结果，而能否看到语句④～语句⑥的操作结果是没有保障的。
    Thread t = new Thread() {
      @Override
      public void run() {
        String url = taskConfig.get("url");
        String timeout = taskConfig.get("timeout");
        doTask(url, Integer.valueOf(timeout));
      }
    };
    t.start();
  }

  private static void doTask(String url, int timeout) {
    // 省略其他代码

    // 模拟实际操作的耗时
    Tools.randomPause(500);
  }
}