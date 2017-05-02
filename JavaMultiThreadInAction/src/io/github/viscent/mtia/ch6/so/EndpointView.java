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
import io.github.viscent.mtia.util.Debug;

import java.util.Arrays;
import java.util.Comparator;

public class EndpointView {
  static final Comparator<Endpoint> DEFAULT_COMPARATOR;
  static {
    DEFAULT_COMPARATOR = new DefaultEndpointComparator();
  }

  // 省略其他代码

  public Endpoint[] retrieveServerList(Comparator<Endpoint> comparator) {
    Endpoint[] serverList = doRetrieveServerList();
    Arrays.sort(serverList, comparator);
    return serverList;
  }

  public Endpoint[] retrieveServerList() {
    return retrieveServerList(DEFAULT_COMPARATOR);
  }

  private Endpoint[] doRetrieveServerList() {
    // 模拟实际代码
    Endpoint[] serverList = new Endpoint[] {
        new Endpoint("192.168.1.100", 8080, 5),
        new Endpoint("192.168.1.101", 8081, 3),
        new Endpoint("192.168.1.102", 8082, 2),
        new Endpoint("192.168.1.103", 8080, 4) };
    serverList[0].setOnline(false);
    serverList[3].setOnline(false);
    return serverList;
  }

  public static void main(String[] args) {
    EndpointView endpointView = new EndpointView();
    Endpoint[] serverList = endpointView.retrieveServerList();
    Debug.info(Arrays.toString(serverList));
  }
}