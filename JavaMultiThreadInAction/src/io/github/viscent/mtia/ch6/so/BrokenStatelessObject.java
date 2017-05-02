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
package io.github.viscent.mtia.ch6.so;

import java.util.HashMap;
import java.util.Map;

public class BrokenStatelessObject {
  public String doSomething(String s) {
    UnsafeSingleton us = UnsafeSingleton.INSTANCE;
    int i = us.doSomething(s);
    UnsafeStatefullObject sfo = new UnsafeStatefullObject();
    String str = sfo.doSomething(s, i);
    return str;
  }

  public String doSomething1(String s) {
    UnsafeSingleton us = UnsafeSingleton.INSTANCE;
    UnsafeStatefullObject sfo = new UnsafeStatefullObject();
    String str;
    synchronized (this) {
      str = sfo.doSomething(s, us.doSomething(s));
    }
    return str;
  }
}

class UnsafeStatefullObject {
  static Map<String, String> cache = new HashMap<String, String>();

  public String doSomething(String s, int len) {
    String result = cache.get(s);
    if (null == result) {
      result = md5sum(result, len);
      cache.put(s, result);
    }
    return result;
  }

  public String md5sum(String s, int len) {
    // 生成md5摘要
    // 省略其他代码
    return s;
  }
}

enum UnsafeSingleton {
  INSTANCE;

  public int state1;

  public int doSomething(String s) {
    // 省略其他代码

    // 访问state1
    return 0;
  }
}
