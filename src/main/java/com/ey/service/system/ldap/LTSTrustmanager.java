package com.ey.service.system.ldap;

import java.security.cert.CertificateException;  
import java.security.cert.X509Certificate;  
  
import javax.net.ssl.X509TrustManager;  
  
public class LTSTrustmanager implements X509TrustManager {  
  
    @Override  
    public void checkClientTrusted(X509Certificate[] arg0, String arg1)  
            throws CertificateException {  
          
    }  
  
    @Override  
    public void checkServerTrusted(X509Certificate[] arg0, String arg1)  
            throws CertificateException {  
          
    }  
  
    @Override  
    public X509Certificate[] getAcceptedIssuers() {  
        return new java.security.cert.X509Certificate[0];  
    }  
  
}  
