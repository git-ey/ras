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
public class startFilter extends BaseController implements Filter{
	
	/**
	 * 初始化
	 */
	public void init(FilterConfig fc) throws ServletException {
		this.reductionDbBackupQuartzState();
	}
	
	/**
	 * web容器重启时，所有定时备份状态关闭
	 */
	public void reductionDbBackupQuartzState(){
		try {
			DbTools.executeUpdateFH("update DB_TIMINGBACKUP set STATUS = '2'");
		} catch (Exception e) {
		} 
	}
	
	public void destroy() {
	}
	
	public void doFilter(ServletRequest arg0, ServletResponse arg1,
			FilterChain arg2) throws IOException, ServletException {
	}
	
}
