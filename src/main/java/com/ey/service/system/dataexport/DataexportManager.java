package com.ey.service.system.dataexport;

import java.util.List;

import com.ey.entity.Page;
import com.ey.util.PageData;

/** 
 * 说明： 导出工作台接口
 * 创建人：andychen
 * 创建时间：2017-08-22
 * @version
 */
public interface DataexportManager{

	/**列表
	 * @param page
	 * @throws Exception
	 */
	public List<PageData> list(Page page)throws Exception;
	
	/**
	 * 获取期间
	 * @param pd
	 * @return
	 */
	public List<PageData> listPeriod(PageData pd) throws Exception;

	
}

