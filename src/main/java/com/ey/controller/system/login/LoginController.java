package com.ey.controller.system.login;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.mgt.eis.SessionDAO;
import org.apache.shiro.subject.Subject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.ey.controller.base.BaseController;
import com.ey.entity.system.Menu;
import com.ey.entity.system.Role;
import com.ey.entity.system.User;
import com.ey.service.hr.datajur.DatajurManager;
import com.ey.service.system.button.ButtonManager;
import com.ey.service.system.buttonrights.ButtonrightsManager;
import com.ey.service.system.config.impl.SystemConfig;
import com.ey.service.system.ldap.LdapService;
import com.ey.service.system.loger.LogerManager;
import com.ey.service.system.loger.LoginManager;
import com.ey.service.system.menu.MenuManager;
import com.ey.service.system.role.RoleManager;
import com.ey.service.system.user.UserManager;
import com.ey.util.AppUtil;
import com.ey.util.Const;
import com.ey.util.DateUtil;
import com.ey.util.Jurisdiction;
import com.ey.util.PageData;
import com.ey.util.RightsHelper;
import com.ey.util.Tools;

/**
 * 登录总入口
 */
@Controller
public class LoginController extends BaseController {

	@Resource(name = "userService")
	private UserManager userService;
	@Resource(name = "menuService")
	private MenuManager menuService;
	@Resource(name = "roleService")
	private RoleManager roleService;
	@Resource(name = "buttonrightsService")
	private ButtonrightsManager buttonrightsService;
	@Resource(name = "buttonService")
	private ButtonManager buttonService;
	@Resource(name = "datajurService")
	private DatajurManager datajurService;
	@Resource(name = "logService")
	private LogerManager logManager;
	@Resource(name = "ldapService")
	private LdapService ldapService;
	@Resource(name = "systemConfig")
	private SystemConfig systemConfig;
	@Resource(name = "loginService")
	private LoginManager loginManager;
	@Resource(name = "sessionDAO")
	private SessionDAO sessionDAO;

	/**
	 * 访问登录页
	 * 
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/login_toLogin")
	public ModelAndView toLogin() throws Exception {
		ModelAndView mv = this.getModelAndView();
		PageData pd = new PageData();
		pd = this.getPageData();
		pd = this.setLoginPd(pd); // 设置登录页面的配置参数
		mv.setViewName("system/index/login");
		mv.addObject("pd", pd);
		return mv;
	}

	/**
	 * 请求登录，验证用户
	 * 
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/login_login", produces = "application/json;charset=UTF-8")
	@ResponseBody
	public Object login(HttpServletResponse response) throws Exception {
		// 处理缓存
		// setNoCache(response);
		Map<String, String> map = new HashMap<String, String>();
		PageData pd = new PageData();
		pd = this.getPageData();
		String errInfo = "";
		String KEYDATA[] = pd.getString("KEYDATA").split(",");
		if (null != KEYDATA && KEYDATA.length == 2) {
			String USERNAME = KEYDATA[0]; // 登录过来的用户名
			String PASSWORD = KEYDATA[1]; // 登录过来的密码
			pd.put("USERNAME", USERNAME);
			String passwd = new SimpleHash("SHA-1", USERNAME, PASSWORD).toString(); // 密码加密
			if (StringUtils.isBlank(systemConfig.getLdapFlag()) || systemConfig.getLdapFlag().equals("N")) {
				pd.put("PASSWORD", passwd);
				pd = userService.getUserByNameAndPwd(pd); // 根据用户名和密码去读取用户信息
				// 登录过程
				errInfo = this.processLogin(pd, USERNAME, PASSWORD);
				// LDAP处理方式
			} else {
				boolean b;
				try {
					b = ldapService.authenticate(USERNAME, PASSWORD);
				} catch (Exception e) {
					b = false;
					errInfo = "AD域认证异常，请联系管理员";
				}
				if (b) {
					pd = userService.findByUsername(pd);
					if (pd == null) {
						pd = this.insertUser(USERNAME, passwd);
					} else {
						this.updateUser(USERNAME, passwd);
					}
					// 登录过程
					errInfo = this.processLogin(pd, USERNAME, PASSWORD);
				} else {
					// pd = userService.findByUsername(pd);
					// if (pd != null) {
						//  // 登录过程
					    //  errInfo = this.processLogin(null, USERNAME, PASSWORD);
					//若出现连接LDAP服务器异常，或出现密码频繁报错（实际没错）的情况
					pd.put("PASSWORD", passwd);
					pd = userService.getUserByNameAndPwd(pd);// 根据用户名和密码去读取用户信息
					// 登录过程
					errInfo = this.processLogin(pd, USERNAME, PASSWORD);
					if (pd != null) {
						// 登录过程
						errInfo = this.processLogin(null, USERNAME, PASSWORD);
					}
				}
			}

		} else {
			errInfo = "error"; // 缺少参数
		}
		map.put("result", errInfo);
		return AppUtil.returnObject(new PageData(), map);
	}

	/**
	 * 访问系统首页
	 * 
	 * @param changeMenu：切换菜单参数
	 * @return
	 */
	@RequestMapping(value = "/main/{changeMenu}")
	public ModelAndView login_index(@PathVariable("changeMenu") String changeMenu) {
		ModelAndView mv = this.getModelAndView();
		PageData pd = new PageData();
		pd = this.getPageData();
		try {
			Session session = Jurisdiction.getSession();
			User user = (User) session.getAttribute(Const.SESSION_USER); // 读取session中的用户信息(单独用户信息)
			if (user != null) {
				User userr = (User) session.getAttribute(Const.SESSION_USERROL); // 读取session中的用户信息(含角色信息)
				if (null == userr) {
					user = userService.getUserAndRoleById(user.getUSER_ID()); // 通过用户ID读取用户信息和角色信息
					session.setAttribute(Const.SESSION_USERROL, user); // 存入session
				} else {
					user = userr;
				}
				String userId = user.getUSER_ID();
				String USERNAME = user.getUSERNAME();
				Role role = user.getRole(); // 获取用户角色
				String roleRights = role != null ? role.getRIGHTS() : ""; // 角色权限(菜单权限)
				session.setAttribute(USERNAME + Const.SESSION_ROLE_RIGHTS, roleRights); // 将角色权限存入session
				session.setAttribute(Const.SESSION_USERID, userId); // 放入用户ID到session
				session.setAttribute(Const.SESSION_USERNAME, USERNAME); // 放入用户名到session
				this.setAttributeToAllDEPARTMENT_ID(session, USERNAME); // 把用户的组织机构权限放到session里面
				List<Menu> allmenuList = new ArrayList<Menu>();
				allmenuList = this.getAttributeMenu(session, USERNAME, roleRights); // 菜单缓存
				List<Menu> menuList = new ArrayList<Menu>();
				menuList = this.changeMenuF(allmenuList, session, USERNAME, changeMenu); // 切换菜单
				if (null == session.getAttribute(USERNAME + Const.SESSION_QX)) {
					session.setAttribute(USERNAME + Const.SESSION_QX, this.getUQX(USERNAME));// 按钮权限放到session中
				}
				this.getRemortIP(USERNAME); // 更新登录IP
				mv.setViewName("system/index/main");
				mv.addObject("user", user);
				mv.addObject("menuList", menuList);
			} else {
				mv.setViewName("system/index/login");// session失效后跳转登录页面
			}
		} catch (Exception e) {
			mv.setViewName("system/index/login");
			logger.error(e.getMessage(), e);
		}
		pd.put("SYSNAME", Tools.readTxtFile(Const.SYSNAME)); // 读取系统名称
		pd.put("SKIN", Tools.readTxtFile(Const.SKIN)); // 读取默认皮肤
		mv.addObject("pd", pd);
		return mv;
	}

	/**
	 * 菜单缓存
	 * 
	 * @param session
	 * @param USERNAME
	 * @param roleRights
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List<Menu> getAttributeMenu(Session session, String USERNAME, String roleRights) throws Exception {
		List<Menu> allmenuList = new ArrayList<Menu>();
		if (null == session.getAttribute(USERNAME + Const.SESSION_ALLMENULIST)) {
			allmenuList = menuService.listAllMenuQx("0"); // 获取所有菜单
			if (Tools.notEmpty(roleRights)) {
				allmenuList = this.readMenu(allmenuList, roleRights); // 根据角色权限获取本权限的菜单列表
			}
			session.setAttribute(USERNAME + Const.SESSION_ALLMENULIST, allmenuList);// 菜单权限放入session中
		} else {
			allmenuList = (List<Menu>) session.getAttribute(USERNAME + Const.SESSION_ALLMENULIST);
		}
		return allmenuList;
	}

	/**
	 * 根据角色权限获取本权限的菜单列表(递归处理)
	 * 
	 * @param menuList：传入的总菜单
	 * @param roleRights：加密的权限字符串
	 * @return
	 */
	public List<Menu> readMenu(List<Menu> menuList, String roleRights) {
		for (int i = 0; i < menuList.size(); i++) {
			menuList.get(i).setHasMenu(RightsHelper.testRights(roleRights, menuList.get(i).getMENU_ID()));
			if (menuList.get(i).isHasMenu()) { // 判断是否有此菜单权限
				this.readMenu(menuList.get(i).getSubMenu(), roleRights);// 是：继续排查其子菜单
			}
		}
		return menuList;
	}

	/**
	 * 切换菜单处理
	 * 
	 * @param allmenuList
	 * @param session
	 * @param USERNAME
	 * @param changeMenu
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Menu> changeMenuF(List<Menu> allmenuList, Session session, String USERNAME, String changeMenu) {
		List<Menu> menuList = new ArrayList<Menu>();
		if (null == session.getAttribute(USERNAME + Const.SESSION_MENULIST) || ("yes".equals(changeMenu))) {
			List<Menu> menuList1 = new ArrayList<Menu>();
			List<Menu> menuList2 = new ArrayList<Menu>();
			for (int i = 0; i < allmenuList.size(); i++) {// 拆分菜单
				Menu menu = allmenuList.get(i);
				if ("1".equals(menu.getMENU_TYPE())) {
					menuList1.add(menu);
				} else {
					menuList2.add(menu);
				}
			}
			session.removeAttribute(USERNAME + Const.SESSION_MENULIST);
			if ("2".equals(session.getAttribute("changeMenu"))) {
				session.setAttribute(USERNAME + Const.SESSION_MENULIST, menuList1);
				session.removeAttribute("changeMenu");
				session.setAttribute("changeMenu", "1");
				menuList = menuList1;
			} else {
				session.setAttribute(USERNAME + Const.SESSION_MENULIST, menuList2);
				session.removeAttribute("changeMenu");
				session.setAttribute("changeMenu", "2");
				menuList = menuList2;
			}
		} else {
			menuList = (List<Menu>) session.getAttribute(USERNAME + Const.SESSION_MENULIST);
		}
		return menuList;
	}

	/**
	 * 把用户的组织机构权限放到session里面
	 * 
	 * @param session
	 * @param USERNAME
	 * @return
	 * @throws Exception
	 */
	public void setAttributeToAllDEPARTMENT_ID(Session session, String USERNAME) throws Exception {
		String DEPARTMENT_IDS = "0", DEPARTMENT_ID = "0";
		if (!"admin".equals(USERNAME)) {
			PageData pd = datajurService.getDEPARTMENT_IDS(USERNAME);
			DEPARTMENT_IDS = null == pd ? "无权" : pd.getString("DEPARTMENT_IDS");
			DEPARTMENT_ID = null == pd ? "无权" : pd.getString("DEPARTMENT_ID");
		}
		session.setAttribute(Const.DEPARTMENT_IDS, DEPARTMENT_IDS); // 把用户的组织机构权限集合放到session里面
		session.setAttribute(Const.DEPARTMENT_ID, DEPARTMENT_ID); // 把用户的最高组织机构权限放到session里面
	}

	/**
	 * 进入tab标签
	 * 
	 * @return
	 */
	@RequestMapping(value = "/tab")
	public String tab() {
		return "system/index/tab";
	}

	/**
	 * 进入首页后的默认页面
	 * 
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/login_default")
	public ModelAndView defaultPage() throws Exception {
		ModelAndView mv = this.getModelAndView();
		PageData pd = new PageData();
		mv.addObject("pd", pd);
		mv.setViewName("system/index/default");
		return mv;
	}

	/**
	 * 用户注销
	 * 
	 * @param session
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/logout")
	public ModelAndView logout() throws Exception {
		String USERNAME = Jurisdiction.getUsername(); // 当前登录的用户名
		logBefore(logger, USERNAME + "退出系统");
		logManager.save(USERNAME, "退出");
		ModelAndView mv = this.getModelAndView();
		PageData pd = new PageData();
		this.removeSession(USERNAME);// 请缓存
		// shiro销毁登录
		Subject subject = SecurityUtils.getSubject();
		subject.logout();
		pd = this.getPageData();
		pd.put("msg", pd.getString("msg"));
		pd = this.setLoginPd(pd); // 设置登录页面的配置参数
		mv.setViewName("system/index/login");
		mv.addObject("pd", pd);
		return mv;
	}

	/**
	 * 清理session
	 */
	public void removeSession(String USERNAME) {
		Session session = Jurisdiction.getSession(); // 以下清除session缓存
		session.removeAttribute(Const.SESSION_USER);
		session.removeAttribute(USERNAME + Const.SESSION_ROLE_RIGHTS);
		session.removeAttribute(USERNAME + Const.SESSION_ALLMENULIST);
		session.removeAttribute(USERNAME + Const.SESSION_MENULIST);
		session.removeAttribute(USERNAME + Const.SESSION_QX);
		session.removeAttribute(Const.SESSION_USERPDS);
		session.removeAttribute(Const.SESSION_USERNAME);
		session.removeAttribute(Const.SESSION_USERROL);
		session.removeAttribute("changeMenu");
		session.removeAttribute("DEPARTMENT_IDS");
		session.removeAttribute("DEPARTMENT_ID");
	}

	/**
	 * 设置登录页面的配置参数
	 * 
	 * @param pd
	 * @return
	 */
	public PageData setLoginPd(PageData pd) {
		pd.put("SYSNAME", Tools.readTxtFile(Const.SYSNAME)); // 读取系统名称
		return pd;
	}

	/**
	 * 获取用户权限
	 * 
	 * @param session
	 * @return
	 */
	public Map<String, String> getUQX(String USERNAME) {
		PageData pd = new PageData();
		Map<String, String> map = new HashMap<String, String>();
		try {
			pd.put(Const.SESSION_USERNAME, USERNAME);
			pd.put("ROLE_ID", userService.findByUsername(pd).get("ROLE_ID").toString());// 获取角色ID
			pd = roleService.findObjectById(pd); // 获取角色信息
			map.put("adds", pd.getString("ADD_QX")); // 增
			map.put("dels", pd.getString("DEL_QX")); // 删
			map.put("edits", pd.getString("EDIT_QX")); // 改
			map.put("chas", pd.getString("CHA_QX")); // 查
			List<PageData> buttonQXnamelist = new ArrayList<PageData>();
			if ("admin".equals(USERNAME)) {
				buttonQXnamelist = buttonService.listAll(pd); // admin用户拥有所有按钮权限
			} else {
				buttonQXnamelist = buttonrightsService.listAllBrAndQxname(pd); // 此角色拥有的按钮权限标识列表
			}
			for (int i = 0; i < buttonQXnamelist.size(); i++) {
				map.put(buttonQXnamelist.get(i).getString("QX_NAME"), "1"); // 按钮权限
			}
		} catch (Exception e) {
			logger.error(e.toString(), e);
		}
		return map;
	}

	/**
	 * 更新登录用户的IP
	 * 
	 * @param USERNAME
	 * @throws Exception
	 */
	public void getRemortIP(String USERNAME) throws Exception {
		PageData pd = new PageData();
		HttpServletRequest request = this.getRequest();
		String ip = "";
		if (request.getHeader("x-forwarded-for") == null) {
			ip = request.getRemoteAddr();
		} else {
			ip = request.getHeader("x-forwarded-for");
		}
		pd.put("USERNAME", USERNAME);
		pd.put("IP", ip);
		userService.saveIP(pd);
	}

	// 登录执行
	private String processLogin(PageData pd, String USERNAME, String PASSWORD) throws Exception {
		Session session = Jurisdiction.getSession();
		String errInfo = "";
		if (pd == null) {
			// 记录错误次数
			saveLoginErrorTimes(USERNAME);
			// 错误次数控制
			errInfo = checkLoginErrorTimes(USERNAME, false);
			if (StringUtils.isNotBlank(errInfo)) {
				return errInfo;
			}
			logBefore(logger, USERNAME + "登录系统密码或用户名错误");
			logManager.save(USERNAME, "登录系统密码或用户名错误");
		} else {
			// 错误次数控制
			errInfo = checkLoginErrorTimes(USERNAME, true);
			if (StringUtils.isNotBlank(errInfo)) {
				return errInfo;
			}
			this.removeSession(USERNAME);// 清缓存
			loginManager.delete(USERNAME); // 清记录
			pd.put("LAST_LOGIN", DateUtil.getTime().toString());
			userService.updateLastLogin(pd);
			User user = new User();
			user.setUSER_ID(pd.getString("USER_ID"));
			user.setUSERNAME(pd.getString("USERNAME"));
			user.setPASSWORD(pd.getString("PASSWORD"));
			user.setNAME(pd.getString("NAME"));
			user.setRIGHTS(pd.getString("RIGHTS"));
			user.setROLE_ID(pd.getString("ROLE_ID"));
			user.setLAST_LOGIN(pd.getString("LAST_LOGIN"));
			user.setIP(pd.getString("IP"));
			user.setSTATUS(pd.getString("STATUS"));
			session.setAttribute(Const.SESSION_USER, user); // 把用户信息放session中
			session.removeAttribute(Const.SESSION_SECURITY_CODE); // 清除登录验证码的session
			// 单一登录控制
			checkSingleSingOn(USERNAME);
			// shiro加入身份验证
			Subject subject = SecurityUtils.getSubject();
			UsernamePasswordToken token = new UsernamePasswordToken(USERNAME, PASSWORD);
			try {
				subject.login(token);
			} catch (AuthenticationException e) {
				errInfo = "身份验证失败！";
			}
		}
		if (Tools.isEmpty(errInfo)) {
			errInfo = "success"; // 验证成功
			logBefore(logger, USERNAME + "登录系统");
			logManager.save(USERNAME, "登录系统");
		}
		return errInfo;
	}

	private String checkLoginErrorTimes(String USERNAME, boolean loginSucess) {
		try {
			PageData pd = loginManager.findById(USERNAME);
			if (null == pd) {
				return "";
			}
			long pwdTimes = systemConfig.getPwdTimes();
			long lockTimes = systemConfig.getLockTimes();
			Long times = Long.parseLong(String.valueOf(pd.get("TIMES")));
			Date loginDate = Tools.str2Date(pd.getString("LOGIN_DATE"));
			long sec = timeDifference(new Date(), loginDate, lockTimes);
			if (loginSucess) {
				if (sec > 0 && (times >= pwdTimes)) {
					return "账户已锁定，请 " + sec + " 分钟后再试!";
				} else {
					loginManager.delete(USERNAME);
				}
			} else {
				if (times < pwdTimes) {
					return "还有" + (pwdTimes - times) + "次机会，超过5次会被锁定1小时";
				} else {
					if (sec > 0) {
						return "账户已锁定，请 " + sec + " 分钟后再试!";
					}
				}
			}
		} catch (Exception e) {
		}
		return "";
	}

	private long timeDifference(Date date1, Date date2, long lockTimes) {
		long times = date1.getTime() - date2.getTime();
		return (lockTimes - times) / 1000 / 60;
	}

	/**
	 * 设置登录错误密码
	 * 
	 * @param USERNAME
	 */
	private void saveLoginErrorTimes(String USERNAME) {
		try {
			PageData pd = loginManager.findById(USERNAME);
			if (null == pd) {
				loginManager.save(USERNAME, Tools.date2Str(new Date()));
			} else {
				Long times = Long.parseLong(String.valueOf(pd.get("TIMES")));
				if (times < 5) {
					loginManager.update(USERNAME, Tools.date2Str(new Date()));
				}
				Date loginDate = Tools.str2Date(pd.getString("LOGIN_DATE"));
				long lockTimes = systemConfig.getLockTimes();
				long sec = timeDifference(new Date(), loginDate, lockTimes);
				if (sec < 0) {
					loginManager.delete(USERNAME);
					loginManager.save(USERNAME, Tools.date2Str(new Date()));
				}
			}
		} catch (Exception e) {
		}
	}

	/**
	 * 用户单一登录
	 * 
	 * @param user
	 *            用户实体
	 */
	private void checkSingleSingOn(String USERNAME) {
		// 获取当前已登录的用户session列表
		Collection<Session> sessions = sessionDAO.getActiveSessions();
		for (Session session : sessions) {
			// 清除该用户以前登录时保存的session
			if (USERNAME.equals(String.valueOf(session.getAttribute("USERNAME")))) {
				// 清除认证缓存
				sessionDAO.delete(session);
			}
		}
	}

	/**
	 * 新增用户
	 * 
	 * @param uSERNAME
	 * @param pASSWORD
	 * @return
	 */
	private PageData insertUser(String USERNAME, String PASSWORD) throws Exception {
		PageData pd = new PageData();
		pd.put("USER_ID", this.get32UUID()); // ID 主键
		pd.put("USERNAME", USERNAME);
		pd.put("NAME", USERNAME);
		pd.put("ROLE_ID", "3"); // 角色ID 3 为注册用户
		pd.put("NUMBER", ""); // 编号
		pd.put("PHONE", ""); // 手机号
		pd.put("BZ", "注册用户"); // 备注
		pd.put("LAST_LOGIN", ""); // 最后登录时间
		pd.put("IP", ""); // IP
		pd.put("STATUS", "0"); // 状态
		pd.put("SKIN", "default");
		pd.put("RIGHTS", "");
		pd.put("PASSWORD", new SimpleHash("SHA-1", USERNAME, PASSWORD).toString()); // 密码加密
		if (null == userService.findByUsername(pd)) { // 判断用户名是否存在
			userService.saveU(pd); // 执行保存
			logManager.save(pd.getString("USERNAME"), "新注册");
		}
		return pd;
	}

	/**
	 * 更新用户
	 * 
	 * @param uSERNAME
	 * @param pASSWORD
	 * @return
	 * @throws Exception
	 */
	private PageData updateUser(String USERNAME, String PASSWORD) throws Exception {
		PageData pd = new PageData();
		pd.put("USERNAME", USERNAME);
		pd.put("PASSWORD", PASSWORD);
		userService.updateUser(pd);
		return pd;
	}

}
