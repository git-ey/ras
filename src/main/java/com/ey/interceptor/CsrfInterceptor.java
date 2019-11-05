package com.ey.interceptor;

import java.util.HashSet;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.ey.service.system.config.impl.SystemConfig;

/**
 * @description 拦截csrf攻击
 * @date 2017年1月18日上午11:47:46
 * @version
 */
public class CsrfInterceptor extends HandlerInterceptorAdapter {
	
	@Resource(name = "systemConfig")
	private SystemConfig systemConfig;
	
	private static final String URL_PROTO_HTTP = "http://";
	private static final String URL_PROTO_HTTPS = "https://";
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		String referer = request.getHeader("Referer");
		String port = "-1";
		if (StringUtils.isNotBlank(referer)) {
			if (referer.startsWith(URL_PROTO_HTTP)) {
				referer = referer.replace(URL_PROTO_HTTP, "");
			} else if (referer.startsWith(URL_PROTO_HTTPS)) {
				referer = referer.replace(URL_PROTO_HTTPS, "");
			}
			int i = referer.indexOf("/");
			if (i > 0) {
				referer = referer.substring(0, i);
				if (referer.indexOf(":") > 0) {
					port = referer.split(":")[1];
					referer = referer.substring(0, referer.indexOf(":"));
				}
			}
			if (!getPlatDomains().contains(referer)) { // 不同域请求 视为非法
				if (!systemConfig.getServerPort().equals(port)) {
					throw new Exception("非法请求，请求源不正确");
				}
			}
		} else if(request.getServletPath().endsWith(".do")) {
			throw new Exception("非法请求，请求源不合法");
		}
		return true;
	}

	private Set<String> getPlatDomains() {
		Set<String> domains = new HashSet<String>();
		// 代理地址
		String serverAddr = systemConfig.getServerAddr();
		String[] serverAddrs = serverAddr.split(",");
		for (String addr : serverAddrs) {
			domains.add(addr);
		}
		// 多网域暂时未考虑
		return domains;
	}

}
