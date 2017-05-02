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
package io.github.viscent.mtia.ch3.case01;

import java.util.HashSet;
import java.util.Set;

public class SystemBooter {

  public static void main(String[] args) throws Exception {
    SystemBooter sysBooter = new SystemBooter();
    ServiceInvoker rd = ServiceInvoker.getInstance();

    LoadBalancer lb = sysBooter.createLoadBalancer();

    // 在main线程中设置负载均衡器实例
    rd.setLoadBalancer(lb);

  }

  // 根据系统配置创建负载均衡器实例
  private LoadBalancer createLoadBalancer() throws Exception {
    LoadBalancer lb;
    Candidate candidate = new Candidate(loadEndpoints());
    lb = WeightedRoundRobinLoadBalancer.newInstance(candidate);
    return lb;
  }

  private Set<Endpoint> loadEndpoints() {
    Set<Endpoint> endpoints = new HashSet<Endpoint>();

    // 模拟从数据库加载以下信息
    endpoints.add(new Endpoint("192.168.101.100", 8080, 3));
    endpoints.add(new Endpoint("192.168.101.101", 8080, 2));
    endpoints.add(new Endpoint("192.168.101.102", 8080, 5));
    endpoints.add(new Endpoint("192.168.101.103", 8080, 7));
    return endpoints;
  }

}
