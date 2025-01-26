package com.ey.service.system.config.impl;

/**
 * 
 * @author 系统配置类
 *
 */
public class SystemConfig {

	/**
	 * LDAP是否开启
	 */
	private String ldapFlag;
	/**
	 * 密码锁定次数
	 */
	private long pwdTimes = 5L;
	/**
	 * 锁定时间（毫秒）
	 */
	private long lockTimes = (60 * 60 * 1000);

    private String serverPort;
    
    private String serverAddr;
    
	public String getLdapFlag() {
		return ldapFlag;
	}

	public void setLdapFlag(String ldapFlag) {
		this.ldapFlag = ldapFlag;
	}

	public long getPwdTimes() {
		return pwdTimes;
	}

	public void setPwdTimes(long pwdTimes) {
		this.pwdTimes = pwdTimes;
	}

	public long getLockTimes() {
		return lockTimes;
	}

	public void setLockTimes(long lockTimes) {
		this.lockTimes = lockTimes;
	}

	public String getServerPort() {
		return serverPort;
	}

	public void setServerPort(String serverPort) {
		this.serverPort = serverPort;
	}

	public String getServerAddr() {
		return serverAddr;
	}

	public void setServerAddr(String serverAddr) {
		this.serverAddr = serverAddr;
	}

}
