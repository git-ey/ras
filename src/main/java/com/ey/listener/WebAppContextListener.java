package com.ey.listener;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.springframework.web.context.support.WebApplicationContextUtils;

import com.ey.util.Const;
/**
 * 
* 类名称：WebAppContextListener 应用上下文监听类
 */

public class WebAppContextListener implements ServletContextListener {

	@Override
    public void contextDestroyed(ServletContextEvent event) {
		// TODO Auto-generated method stub
	}

	@Override
    public void contextInitialized(ServletContextEvent event) {
		// TODO Auto-generated method stub
		Const.WEB_APP_CONTEXT = WebApplicationContextUtils.getWebApplicationContext(event.getServletContext());
		//System.out.println("========获取Spring WebApplicationContext");
		System.setProperty("com.sun.jndi.ldap.object.disableEndpointIdentification",  "true");
	}

}
