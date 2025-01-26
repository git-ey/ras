package com.ey.service.system.ldap;

import java.util.Hashtable;

import javax.naming.Context;

import org.springframework.ldap.core.support.LdapContextSource;

public class SSLLdapContextSource extends LdapContextSource {
	
	@Override
	public Hashtable<String, Object> getAnonymousEnv(){
        Hashtable<String, Object> anonymousEnv = super.getAnonymousEnv();
        anonymousEnv.put("java.naming.security.protocol", "ssl");
        anonymousEnv.put("java.naming.ldap.factory.socket", LTSSSLSocketFactory.class.getName());
        anonymousEnv.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        return anonymousEnv;
    }

}
