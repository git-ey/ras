package com.ey.filter;
import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CSPFilter implements Filter {
    public void init(FilterConfig config) throws ServletException {
        // 初始化Filter
    }
    // public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
    //         throws IOException, ServletException {
    //     // 设置CSP策略头部
    //     response.setHeader("Content-Security-Policy", "your-csp-policy-here");

    //     // 继续处理请求
    //     chain.doFilter(request, response);
    // }
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain)
			throws IOException, ServletException {
		HttpServletRequest CSPFRequest = (HttpServletRequest) request;
		HttpServletResponse CSPFResponse = (HttpServletResponse) response;
		//CSPFResponse.setHeader("Content-Security-Policy", "default-src 'self' 'sha256-5E11pZlXVBfFvwPwKagMMUwFQzS0rBkIO2Z+1vkYL6o='; frame-ancestors 'self'; script-src 'self' 'unsafe-inline' 'unsafe-eval';");
		//CSPFResponse.setHeader("Content-Security-Policy", "frame-ancestors 'self'; script-src 'self' 'unsafe-inline' 'unsafe-eval'");
        CSPFResponse.setHeader("Content-Security-Policy", "frame-ancestors 'self'");
        filterChain.doFilter(CSPFRequest, CSPFResponse);
	}

    public void destroy() {
        // 销毁Filter
    }
}