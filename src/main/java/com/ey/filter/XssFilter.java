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

import com.ey.util.MHttpServletRequest;
public class XssFilter implements Filter{  
	@Override
    public void init(FilterConfig filterConfig) throws ServletException {
 
    }
 
    @Override
    public void doFilter(ServletRequest req, ServletResponse res,
            FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest)req;
        request = new MHttpServletRequest(request);
        HttpServletResponse response = (HttpServletResponse)res;
        response.setHeader("X-XSS-Protection", "0");
        chain.doFilter(request, response);
    }
 
    @Override
    public void destroy() {
 
    }
}
