package com.ey.service.system.dataexport.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.ey.dao.DaoSupport;
import com.ey.entity.Page;
import com.ey.service.system.dataexport.DataexportManager;
import com.ey.util.PageData;

/** 
 * 说明： 导出工作台
 * 创建人：andychen
 * 创建时间：2017-08-22
 * @version
 */
@Service("dataexportService")
public class DataexportService implements DataexportManager{

	@Resource(name = "daoSupport")
	private DaoSupport dao;
	
	/**列表
	 * @param page
	 * @throws Exception
	 */
	@Override
    @SuppressWarnings("unchecked")
	public List<PageData> list(Page page)throws Exception{
		return (List<PageData>)dao.findForList("DataexportMapper.datalistPage", page);
	}
	
	/**
	 * 获取期间
	 * @param pd
	 * @return
	 */
    @Override
    @SuppressWarnings("unchecked")
    public List<PageData> listPeriod(PageData pd) throws Exception {
		return (List<PageData>)dao.findForList("DataexportMapper.listPeriod", pd);
	}
	
}

