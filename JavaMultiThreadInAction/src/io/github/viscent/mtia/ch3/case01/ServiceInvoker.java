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

import io.github.viscent.mtia.util.Debug;

public class ServiceInvoker {
  // 保存当前类的唯一实例
  private static final ServiceInvoker INSTANCE = new ServiceInvoker();
  // 负载均衡器实例，使用volatile变量保障可见性
  private volatile LoadBalancer loadBalancer;

  // 私有构造器
  private ServiceInvoker() {
    // 什么也不做
  }

  /**
   * 获取当前类的唯一实例
   */
  public static ServiceInvoker getInstance() {
    return INSTANCE;
  }

  /**
   * 根据指定的负载均衡器派发请求到特定的下游部件。
   *
   * @param request
   *          待派发的请求
   */
  public void dispatchRequest(Request request) {
    // 这里读取volatile变量loadBalancer
    Endpoint endpoint = getLoadBalancer().nextEndpoint();

    if (null == endpoint) {
      // 省略其他代码

      return;
    }

    // 将请求发给下游部件
    dispatchToDownstream(request, endpoint);

  }

  // 真正将指定的请求派发给下游部件
  private void dispatchToDownstream(Request request, Endpoint endpoint) {
    Debug.info("Dispatch request to " + endpoint + ":" + request);
    // 省略其他代码
  }

  public LoadBalancer getLoadBalancer() {
    // 读取负载均衡器实例
    return loadBalancer;
  }

  public void setLoadBalancer(LoadBalancer loadBalancer) {
    // 设置或者更新负载均衡器实例
    this.loadBalancer = loadBalancer;
  }
}
