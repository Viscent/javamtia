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

import java.util.HashMap;
import java.util.Map;

/**
 * 配置实体.
 * 
 * @author Viscent Huang
 */
//非线程安全
public class Configuration {
  /**
   * 配置名称
   */
  private final String name;
  /**
   * 配置当前版本号
   */
  private volatile int version;
  /**
   * 存储配置项的Map. 每个配置项是一个“属性名”->“属性值”的关联。
   */
  private volatile Map<String, String> configItemMap;

  public Configuration(String name, int version) {
    this.name = name;
    this.version = version;
    configItemMap = new HashMap<String, String>();
  }

  public String getName() {
    return name;
  }

  public void setProperty(String key, String value) {
    configItemMap.put(key, value);
  }

  public String getProperty(String key) {
    return configItemMap.get(key);
  }

  public void update(Map<String, String> properties) {
    configItemMap = properties;
  }

  public int getVersion() {
    return version;
  }

  public void setVersion(int version) {
    this.version = version;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + (name == null ? 0 : name.hashCode());
    result = prime * result + version;
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    Configuration other = (Configuration) obj;
    if (name == null) {
      if (other.name != null)
        return false;
    } else if (!name.equals(other.name))
      return false;
    if (version != other.version)
      return false;
    return true;
  }
}