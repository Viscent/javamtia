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

import io.github.viscent.mtia.util.Debug;
import io.github.viscent.mtia.util.Tools;

import java.util.HashMap;
import java.util.Map;

public class FinalExample {
	private final Map<String, String> taskConfig;
  private final String defaultTimeout = "2000";
	private int retriedTimes = -1;

	private static FinalExample instance;

	public FinalExample(String url) {
    taskConfig = new HashMap<String, String>();
    taskConfig.put("url", url);
    taskConfig.put("timeout", String.valueOf(defaultTimeout));
	}

	public FinalExample(String url, int timeout) {
		taskConfig = new HashMap<String, String>();
		taskConfig.put("url", url);
		taskConfig.put("timeout", String.valueOf(timeout));
	}

	public static void init(String url, int timeout) {
		instance = new FinalExample(url, timeout);
	}

	public static void checkConfig() {
		if (null != instance) {
			Map<String, String> conf = instance.taskConfig;
			Debug.info("url:" + conf.get("url"));
			Debug.info("defaultTimeout:" + instance.defaultTimeout.equals("2000"));
			Debug.info("retriedTimes:" + instance.retriedTimes);
		}
	}

	public static void main(String[] args) {
		Thread subThread = new Thread() {
			@Override
			public void run() {
				Tools.randomPause(50);
				FinalExample.checkConfig();
			}
		};
		subThread.start();
		FinalExample.init("https://github.com/Viscent", 1200);
	}

}
