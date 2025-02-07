package com.ey.service.system.loger.impl;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.ey.dao.DaoSupport;
import com.ey.entity.Page;
import com.ey.service.system.loger.LogerManager;
import com.ey.service.system.user.UserManager;
import com.ey.util.PageData;
import com.ey.util.Tools;
import com.ey.util.UuidUtil;

/**
 * 说明： 操作日志记录
 */
@Service("logService")
public class LogerService implements LogerManager {

	@Resource(name = "daoSupport")
	private DaoSupport dao;
	@Resource(name = "userService")
	private UserManager userService;

	/**
	 * 新增
	 * 
	 * @param pd
	 * @throws Exception
	 */
	@Override
	public void save(String USERNAME, String CONTENT) throws Exception {
		PageData pd = new PageData();
		pd.put("USERNAME", USERNAME); // 用户名
		pd.put("CONTENT", CONTENT); // 事件
		pd.put("LOG_ID", UuidUtil.get32UUID()); // 主键
		pd.put("CZTIME", Tools.date2Str(new Date())); // 操作时间
		PageData result = userService.findByUsername(pd);
		if (result != null) {
			pd.put("IP", result.getString("IP"));
		}
		dao.save("LogMapper.save", pd);
	}

	/**
	 * 删除
	 * 
	 * @param pd
	 * @throws Exception
	 */
	@Override
	public void delete(PageData pd) throws Exception {
		dao.delete("LogMapper.delete", pd);
	}

	/**
	 * 列表
	 * 
	 * @param page
	 * @throws Exception
	 */
	@Override
	@SuppressWarnings("unchecked")
	public List<PageData> list(Page page) throws Exception {
		return (List<PageData>) dao.findForList("LogMapper.datalistPage", page);
	}

	/**
	 * 列表(全部)
	 * 
	 * @param pd
	 * @throws Exception
	 */
	@Override
	@SuppressWarnings("unchecked")
	public List<PageData> listAll(PageData pd) throws Exception {
		return (List<PageData>) dao.findForList("LogMapper.listAll", pd);
	}

	/**
	 * 通过id获取数据
	 * 
	 * @param pd
	 * @throws Exception
	 */
	@Override
	public PageData findById(PageData pd) throws Exception {
		return (PageData) dao.findForObject("LogMapper.findById", pd);
	}

	/**
	 * 批量删除
	 * 
	 * @param ArrayDATA_IDS
	 * @throws Exception
	 */
	@Override
	public void deleteAll(String[] ArrayDATA_IDS) throws Exception {
		dao.delete("LogMapper.deleteAll", ArrayDATA_IDS);
	}

}
