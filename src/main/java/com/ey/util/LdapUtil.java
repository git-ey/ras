package com.ey.util;

import org.springframework.ldap.core.DistinguishedName;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.ldap.filter.AndFilter;
import org.springframework.ldap.filter.EqualsFilter;

public class LdapUtil {
	/**
	 * 初始化LdapTemplate
	 * 
	 * @return
	 */
	public static LdapTemplate getLdapTemplate() {
		LdapTemplate template = null;
		try {
			LdapContextSource contextSource = new LdapContextSource();

			String url = "ldap://pacrim.ey.net";
			String base = "OU=Shanghai,OU=China,OU=CN,DC=pacrim,DC=ey,DC=net";
			String userDn = "P.WAMPROD.1";
			String password = "v18fT3Kq1qYzG5";

			contextSource.setUrl(url);
			contextSource.setBase(base);
			contextSource.setUserDn(userDn);
			contextSource.setPassword(password);
			contextSource.setPooled(false);
			contextSource.afterPropertiesSet(); // important
			contextSource.setReferral("throw");
			template = new LdapTemplate(contextSource);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return template;
	}
	
	public static boolean authenticate(String userName, String password) {
		LdapTemplate ldapTemplate = LdapUtil.getLdapTemplate();
        AndFilter filter = new AndFilter();
        filter.and(new EqualsFilter("objectclass", "user")).and(
                new EqualsFilter("CN", userName));
        return ldapTemplate.authenticate(DistinguishedName.EMPTY_PATH, filter
                .toString(), password);
    }

	public static void main(String[] args) {
		
		boolean b = LdapUtil.authenticate("Vincent-S.Shen","@jvqak5fpzj201911");
		
		System.out.println(b);
		
		/*
		 * try { LdapTemplate template = LdapUtil.getLdapTemplate();
		 * 
		 * List<String> names = template.search( query()
		 * .searchScope(SearchScope.SUBTREE) .where("objectclass") .is("*") .and("cn")
		 * .is("vincent-s.shen"), new AbstractContextMapper<String>() {
		 * 
		 * @Override protected String doMapFromContext(DirContextOperations ctx) {
		 * return ctx.getNameInNamespace(); } });
		 * 
		 * } catch (Exception e) { e.printStackTrace(); }
		 */

	}

}
