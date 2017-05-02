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

/**
 * 加权轮询负载均衡算法实现类
 *
 * @author Viscent Huang
 */
public class WeightedRoundRobinLoadBalancer extends AbstractLoadBalancer {
  // 私有构造器
  private WeightedRoundRobinLoadBalancer(Candidate candidate) {
    super(candidate);
  }

  // 通过该静态方法创建该类的实例
  public static LoadBalancer newInstance(Candidate candidate)
      throws Exception {
    WeightedRoundRobinLoadBalancer lb =
        new WeightedRoundRobinLoadBalancer(candidate);
    lb.init();
    return lb;

  }

  // 在该方法中实现相应的负载均衡算法
  @Override
  public Endpoint nextEndpoint() {
    Endpoint selectedEndpoint = null;
    int subWeight = 0;
    int dynamicTotoalWeight;
    final double rawRnd = super.random.nextDouble();
    int rand;

    // 读取volatile变量candidate
    final Candidate candiate = super.candidate;
    dynamicTotoalWeight = candiate.totalWeight;
    for (Endpoint endpoint : candiate) {
      // 选取节点以及计算总权重时跳过非在线节点
      if (!endpoint.isOnline()) {
        dynamicTotoalWeight -= endpoint.weight;
        continue;
      }
      rand = (int) (rawRnd * dynamicTotoalWeight);
      subWeight += endpoint.weight;
      if (rand <= subWeight) {
        selectedEndpoint = endpoint;
        break;
      }
    }
    return selectedEndpoint;
  }
}
