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
package io.github.viscent.mtia.ch5.case04;

import io.github.viscent.mtia.ch5.TerminatableTaskRunner;
import io.github.viscent.mtia.util.Debug;
import io.github.viscent.mtia.util.Tools;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Timer;
import java.util.TimerTask;

public class MusicFinder extends SimpleFileVisitor<Path> implements Runnable {
  final PrintWriter pwr;
  final TerminatableTaskRunner taskRunner;
  final String rootDirToSearch;
  final long timeout;
  volatile boolean canceled = false;

  public MusicFinder(TerminatableTaskRunner taskRunner, String rootDirToSearch,
      String outputFileName, long timeout) throws Exception {
    this.taskRunner = taskRunner;
    this.rootDirToSearch = rootDirToSearch;
    this.pwr = new PrintWriter(new FileWriter(outputFileName), true);
    this.timeout = timeout;
  }

  public MusicFinder(TerminatableTaskRunner taskRunner, String rootDirToSearch,
      String outputFileName) throws Exception {
    this(taskRunner, rootDirToSearch, outputFileName, 0L);
  }

  public void searchMusicFiles() throws InterruptedException {
    final MusicFinder task = this;
    taskRunner.submit(task);
    if (timeout <= 0) {
      return;
    }
    // 在指定时间后取消提交给taskRunner的任务task
    Timer timer = new Timer(true);
    timer.schedule(new TimerTask() {
      @Override
      public void run() {
        task.cancel();
      }
    }, timeout);
  }

  public void cancel() {
    Debug.info("Canceling searching mp3...");
    taskRunner.cancelTask();
  }

  @Override
  public void run() {
    Path start = FileSystems.getDefault().getPath(rootDirToSearch);
    try {
      Files.walkFileTree(start, this);
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      Tools.silentClose(pwr);
    }
    Debug.info("Search %s.", canceled ? "canceled" : "done");
  }

  @Override
  public FileVisitResult visitFile(Path filePath, BasicFileAttributes attrs)
      throws IOException {
    File file = filePath.toFile();
    if (file.getName().toLowerCase().endsWith(".mp3")) {
      final String fileName = file.getCanonicalPath();
      Debug.info("adding mp3:%s", fileName);
      // 将文件名添加到播放列表文件中
      pwr.println(fileName);
    }
    return FileVisitResult.CONTINUE;
  }

  @Override
  public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes
      attrs)
      throws IOException {
    // 检测中断标志
    if (Thread.currentThread().isInterrupted()) {
      Debug.info("interrupt detected");
      canceled = true;
      // 停止文件查找
      return FileVisitResult.TERMINATE;
    }
    return FileVisitResult.CONTINUE;
  }
}
