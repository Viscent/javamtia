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
package io.github.viscent.mtia.ch6;

import io.github.viscent.mtia.util.ReadOnlyIterator;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public final class BigImmutableObject implements
    Iterable<Map.Entry<String, BigObject>> {
  private final HashMap<String, BigObject> registry;

  public BigImmutableObject(HashMap<String, BigObject> registry) {
    this.registry = registry;
  }

  public BigImmutableObject(BigImmutableObject prototype, String key,
      BigObject newValue) {
    this(createRegistry(prototype, key, newValue));
  }

  @SuppressWarnings("unchecked")
  private static HashMap<String, BigObject> createRegistry(
      BigImmutableObject prototype, String key,
      BigObject newValue) {
    // 从现有对象中复制（浅复制）字段
    HashMap<String, BigObject> newRegistry =
        (HashMap<String, BigObject>) prototype.registry.clone();

    // 仅更新需要更新的部分
    newRegistry.put(key, newValue);
    return newRegistry;
  }

  @Override
  public Iterator<Entry<String, BigObject>> iterator() {
    // 对entrySet进行防御性复制
    final Set<Entry<String, BigObject>> readOnlyEntries = Collections
        .unmodifiableSet(registry.entrySet());

    // 返回一个只读的Iterator实例
    return ReadOnlyIterator.with(
        readOnlyEntries.iterator());
  }

  public BigObject getObject(String key) {
    return registry.get(key);
  }

  public BigImmutableObject update(String key,
      BigObject newValue) {
    return new BigImmutableObject(this, key, newValue);
  }
}

class BigObject {
  byte[] data = new byte[4 * 1024 * 1024];
  private int id;
  private final static AtomicInteger ID_Gen = new AtomicInteger(0);

  public BigObject() {
    id = ID_Gen.incrementAndGet();
  }

  @Override
  public String toString() {
    return "BigObject [id=" + id + "]";
  }
  // 省略其他代码
}