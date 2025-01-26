package com.ey.service.system.loger.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.ey.dao.DaoSupport;
import com.ey.service.system.loger.LoginManager;
import com.ey.util.PageData;

/** 
 * 说明： 操作日志记录
 */
@Service("loginService")
public class LoginService implements LoginManager{

	@Resource(name = "daoSupport")
	private DaoSupport dao;
	
	/**新增
	 * @param pd
	 * @throws Exception
	 */
	@Override
    public void save(String USERNAME,String loginDate)throws Exception{
		PageData pd = new PageData();
		pd.put("USERNAME", USERNAME);			//用户名
		pd.put("TIMES", 1L);				    //次数
		pd.put("LOGIN_DATE", loginDate);		//时间
		dao.save("LoginMapper.save", pd);
	}
	
	/**删除
	 * @param pd
	 * @throws Exception
	 */
	@Override
    public void delete(String USERNAME)throws Exception{
		PageData pd = new PageData();
		pd.put("USERNAME", USERNAME);			//用户名
		dao.delete("LoginMapper.delete", pd);
	}
	
	@Override
	public void update(String USERNAME,String LOGIN_DATE) throws Exception {
		PageData pd = new PageData();
		pd.put("USERNAME", USERNAME);			//用户名
		pd.put("LOGIN_DATE", LOGIN_DATE);		//时间
		dao.delete("LoginMapper.update", pd);
	}
	
	/**通过id获取数据
	 * @param pd
	 * @throws Exception
	 */
	@Override
    public PageData findById(String USERNAME)throws Exception{
		PageData pd = new PageData();
		pd.put("USERNAME", USERNAME);			//用户名
		return (PageData)dao.findForObject("LoginMapper.findById", pd);
	}
	
}

