package com.ey.service.system.user.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ldap.core.DistinguishedName;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.ldap.filter.AndFilter;
import org.springframework.ldap.filter.EqualsFilter;
import org.springframework.stereotype.Service;

@Service("ldapService")
public class LdapService {

	@Autowired
	private LdapContextSource ldapContextSource;

	public boolean authenticate(String userName, String password) {
		
		LdapTemplate ldapTemplate = new LdapTemplate(ldapContextSource);
		
		AndFilter filter = new AndFilter();
		filter.and(new EqualsFilter("objectClass", "user")).and(new EqualsFilter("CN", userName));
		return ldapTemplate.authenticate(DistinguishedName.EMPTY_PATH, filter.toString(), password);
	}

}
