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
package io.github.viscent.mtia.ch3;

public class HTTPRangeRequest {
  private final Range range;
  private String url;

  public HTTPRangeRequest(String url, int lowerBound, int upperBound) {
    this.url = url;
    this.range = new Range(lowerBound, upperBound);
  }

  public static class Range {
    private long lowerBound;
    private long upperBound;

    public Range(long lowerBound, long upperBound) {
      this.lowerBound = lowerBound;
      this.upperBound = upperBound;
    }

    public long getLowerBound() {
      return lowerBound;
    }

    public void setLowerBound(long lowerBound) {
      this.lowerBound = lowerBound;
    }

    public long getUpperBound() {
      return upperBound;
    }

    public void setUpperBound(long upperBound) {
      this.upperBound = upperBound;
    }

  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public Range getRange() {
    return range;
  }
}