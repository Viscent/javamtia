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
package io.github.viscent.mtia.ch1;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/echo")
public class EchoServlet extends HttpServlet {
  private static final long serialVersionUID = 4787580353870831328L;

  @Override
  protected void
      doGet(HttpServletRequest request, HttpServletResponse response)
          throws ServletException, IOException {
    // 获取当前线程
    Thread currentThread = Thread.currentThread();
    // 获取当前线程的线程名称
    String currentThreadName = currentThread.getName();
    response.setContentType("text/plain");
    try (PrintWriter pwr = response.getWriter()) {
      // 输出处理当前请求的线程的名称
      pwr.printf("This request was handled by thread:%s%n", currentThreadName);
      pwr.flush();
    }
  }
}