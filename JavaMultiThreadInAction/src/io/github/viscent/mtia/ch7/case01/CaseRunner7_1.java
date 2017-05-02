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
package io.github.viscent.mtia.ch7.case01;

import io.github.viscent.mtia.util.Debug;
import io.github.viscent.mtia.util.Tools;

import java.util.HashMap;
import java.util.Map;

/**
 * 本程序可能导致死锁
 * 
 * @author Viscent Huang
 */
public class CaseRunner7_1 {

 final static ConfigurationHelper configHelper = ConfigurationHelper.INSTANCE.init();

  public static void main(String[] args) throws InterruptedException {
    // 模拟业务线程读取配置实体
    Thread trxThread = new Thread(new Runnable() {

      @Override
      public void run() {
        Configuration cfg = configHelper.getConfig("serverInfo");
        String url = cfg.getProperty("url");
        process(url);
      }

      private void process(String url) {
        Debug.info("processing %s", url);
        // ...
      }

    });

    // 模拟系统管理线程更新配置数据
    Thread updateThread = new Thread(new Runnable() {

      @Override
      public void run() {
        // 模拟实际操作所需的时间
        Tools.randomPause(40);

        Map<String, String> props = new HashMap<String, String>();
        props.put("property1", "value1");
        props.put("property2", "value2");
        props.put("property3", "value3");
        ConfigurationManager.INSTANCE.update("anotherConfig", 6, props);
      }

    });

    // 启动并等待指定的线程终止
    Tools.startAndWaitTerminated(trxThread, updateThread);
  }
}