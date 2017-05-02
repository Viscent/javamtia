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

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 配置助手.
 *该类可能导致死锁！
 * @author Viscent Huang
 */
public enum ConfigurationHelper implements ConfigEventListener {
  INSTANCE;

  final ConfigurationManager configManager;
  final ConcurrentMap<String, Configuration> cachedConfig;

  private ConfigurationHelper() {
    configManager = ConfigurationManager.INSTANCE;
    cachedConfig = new ConcurrentHashMap<String, Configuration>();
  }

  public Configuration getConfig(String name) {
    Configuration cfg;
    cfg = getCachedConfig(name);
    if (null == cfg) {
      synchronized (this) {
        cfg = getCachedConfig(name);
        if (null == cfg) {
          cfg = configManager.load(name);
          cachedConfig.put(name, cfg);
        }
      }
    }
    return cfg;
  }

  public Configuration getCachedConfig(String name) {
    return cachedConfig.get(name);
  }

  public ConfigurationHelper init() {
    configManager.registerListener(this);
    return this;
  }

  @Override
  public void onConfigLoaded(Configuration cfg) {
    cachedConfig.putIfAbsent(cfg.getName(), cfg);
  }

  @Override
  public void onConfigUpdated(String name, int newVersion,
      Map<String, String> properties) {
    Configuration cachedConfig = getCachedConfig(name);
    // 更新内容和版本这两个操作必须是原子操作
    synchronized (this) {
      if (null != cachedConfig) {
        cachedConfig.update(properties);
        cachedConfig.setVersion(newVersion);
      }
    }
  }
}