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

import io.github.viscent.mtia.util.stf.Actor;
import io.github.viscent.mtia.util.stf.ConcurrencyTest;
import io.github.viscent.mtia.util.stf.Expect;
import io.github.viscent.mtia.util.stf.Observer;
import io.github.viscent.mtia.util.stf.TestRunner;

@ConcurrencyTest(iterations = 200000)
public class VolatileOrderingDemo {
	private int dataA = 0;
	private long dataB = 0L;
	private String dataC = null;
	private volatile boolean ready = false;

	@Actor
	public void writer() {
		dataA = 1;
		dataB = 10000L;
		dataC = "Content...";
		ready = true;
	}

	@Observer({
		@Expect(desc = "Normal", expected = 1),
	  @Expect(desc = "Impossible", expected = 2),
	  @Expect(desc = "ready not true", expected = 3) 
	})
	public int reader() {
		int result = 0;
		boolean allISOK;
		if (ready) {
			allISOK = (1 == dataA) && (10000L == dataB) && "Content...".equals(dataC);
			result = allISOK ? 1 : 2;
		} else {
			result = 3;
		}
		return result;
	}

	public static void main(String[] args) throws InstantiationException,
	    IllegalAccessException {

		// 调用测试工具运行测试代码
		TestRunner.runTest(VolatileOrderingDemo.class);
	}

}
