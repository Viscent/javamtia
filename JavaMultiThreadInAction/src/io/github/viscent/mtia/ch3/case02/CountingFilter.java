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
package io.github.viscent.mtia.ch3.case02;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

@WebFilter("/echo")
public class CountingFilter implements Filter {
  final Indicator indicator = Indicator.getInstance();

  public CountingFilter() {
    // 什么也不做
  }

  @Override
  public void destroy() {
    // 什么也不做
  }

  @Override
  public void doFilter(ServletRequest request, ServletResponse response,
      FilterChain chain) throws IOException, ServletException {
    indicator.newRequestReceived();
    StatusExposingResponse httpResponse = new StatusExposingResponse(
        (HttpServletResponse) response);

    chain.doFilter(request, httpResponse);

    int statusCode = httpResponse.getStatus();
    if (0 == statusCode || 2 == statusCode / 100) {
      indicator.newRequestProcessed();
    } else {
      indicator.requestProcessedFailed();
    }
  }

  public class StatusExposingResponse extends HttpServletResponseWrapper {
    private int httpStatus;

    public StatusExposingResponse(HttpServletResponse response) {
      super(response);
    }

    @Override
    public void sendError(int sc) throws IOException {
      httpStatus = sc;
      super.sendError(sc);
    }

    @Override
    public void sendError(int sc, String msg) throws IOException {
      httpStatus = sc;
      super.sendError(sc, msg);
    }

    @Override
    public void setStatus(int sc) {
      httpStatus = sc;
      super.setStatus(sc);
    }

    @Override
    public int getStatus() {
      return httpStatus;
    }
  }

  @Override
  public void init(FilterConfig fConfig) throws ServletException {
    // 什么也不做
  }

}
