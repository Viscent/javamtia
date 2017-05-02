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

import io.github.viscent.mtia.ch3.case01.Endpoint;

import java.util.Comparator;

public class DefaultEndpointComparator implements Comparator<Endpoint> {
  @Override
  public int compare(Endpoint server1, Endpoint server2) {
    int result = 0;
    boolean isOnline1 = server1.isOnline();
    boolean isOnline2 = server2.isOnline();
    // 优先按照服务器是否在线排序
    if (isOnline1 == isOnline2) {
      // 被比较的两台服务器都在线（或不在线）的情况下进一步比较服务器权重
      result = compareWeight(server1.weight, server2.weight);
    } else {
      // 在线的服务器排序靠前
      if (isOnline1) {
        result = -1;
      }
    }
    return result;
  }

  private int compareWeight(int weight1, int weight2) {
    if (weight1 == weight2) {
      return 0;
    } else if (weight1 < weight2) {
      // 按权重降序排列
      return 1;
    } else {
      return -1;
    }
  }
}