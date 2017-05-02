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
package io.github.viscent.mtia.ch4.case02;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class DelayItem {
	private long timeStamp;
	private AtomicInteger sampleCount = new AtomicInteger(0);
	private AtomicLong totalDelay = new AtomicLong(0);

	public DelayItem(long timeStamp) {
		this.timeStamp = timeStamp;
	}

	public AtomicInteger getSampleCount() {
		return sampleCount;
	}

	public void setSampleCount(AtomicInteger sampleCount) {
		this.sampleCount = sampleCount;
	}

	public AtomicLong getTotalDelay() {
		return totalDelay;
	}

	public void setTotalDelay(AtomicLong totalDelay) {
		this.totalDelay = totalDelay;
	}

	public long getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(long timeStamp) {
		this.timeStamp = timeStamp;
	}
}
