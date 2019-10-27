package com.ey.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

public class FilterSecurity implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }
    @Override
    public void doFilter(ServletRequest request, ServletResponse response,FilterChain chain) throws IOException, ServletException {
            HttpServletResponse res = (HttpServletResponse) response;  
            //res.setDateHeader("Expries", 0); 
            //res.setHeader("Cache-Control", "no-cache");
            //res.setHeader("Content-Security-Policy","frame-src 'self';");
    }
    @Override
    public void destroy() {
    }
}