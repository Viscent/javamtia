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
package io.github.viscent.mtia.ch4.case01;

import io.github.viscent.mtia.util.Debug;

import java.io.Closeable;
import java.io.IOException;
import java.nio.ByteBuffer;

public class DownloadBuffer implements Closeable {
  /**
   * 当前Buffer中缓冲的数据相对于整个存储文件的位置偏移
   */
  private long globalOffset;
  private long upperBound;
  private int offset = 0;
  public final ByteBuffer byteBuf;
  private final Storage storage;

  public DownloadBuffer(long globalOffset, long upperBound,
      final Storage storage) {
    this.globalOffset = globalOffset;
    this.upperBound = upperBound;
    this.byteBuf = ByteBuffer.allocate(1024 * 1024);
    this.storage = storage;
  }

  public void write(ByteBuffer buf) throws IOException {
    int length = buf.position();
    final int capacity = byteBuf.capacity();
    // 当前缓冲区已满，或者剩余容量不够容纳新数据
    if (offset + length > capacity || length == capacity) {
      // 将缓冲区中的数据写入文件
      flush();
    }
    byteBuf.position(offset);
    buf.flip();
    byteBuf.put(buf);
    offset += length;
  }

  public void flush() throws IOException {
    int length;
    byteBuf.flip();
    length = storage.store(globalOffset, byteBuf);
    byteBuf.clear();
    globalOffset += length;
    offset = 0;
  }

  @Override
  public void close() throws IOException {
    Debug.info("globalOffset:%s,upperBound:%s", globalOffset, upperBound);
    if (globalOffset < upperBound) {
      flush();
    }
  }
}