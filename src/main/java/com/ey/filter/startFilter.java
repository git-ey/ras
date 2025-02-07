package com.ey.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import com.ey.controller.base.BaseController;
import com.ey.util.DbTools;

/**
 * 启动tomcat时运行此类
 */
public class startFilter extends BaseController implements Filter {

	/**
	 * 初始化
	 */
	@Override
	public void init(FilterConfig fc) throws ServletException {
		this.reductionDbBackupQuartzState();
	}

	/**
	 * web容器重启时，所有定时备份状态关闭
	 */
	public void reductionDbBackupQuartzState() {
		try {
			DbTools.executeUpdateFH("update DB_TIMINGBACKUP set STATUS = '2'");
		} catch (Exception e) {
		}
	}

	@Override
	public void destroy() {
	}

	@Override
    public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		//必须
		//HttpServletRequest servletRequest = (HttpServletRequest)request;
		//HttpServletResponse servletResponse = (HttpServletResponse)response;
		//实际设置
		//servletResponse.setHeader("x-frame-options","SAMEORIGIN");
		//调用下一个过滤器
		//chain.doFilter(request, response);
	}

}
