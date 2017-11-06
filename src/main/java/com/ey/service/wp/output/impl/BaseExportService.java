package com.ey.service.wp.output.impl;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.ey.dao.DaoSupport;
import com.ey.service.wp.output.BaseExportManager;

/**
 * @name CExportService
 * @description 底稿输出服务--C
 * @author Dai Zong	2017年8月26日
 */
@Service("baseExportService")
public abstract class BaseExportService implements BaseExportManager{

    /**
     * dao
     */
	@Resource(name = "daoSupport")
	protected DaoSupport dao;
	
	/**
	 * 构建基本查询Map
	 * @author Dai Zong 2017年9月12日
	 * 
	 * @param fundId
	 * @param periodStr
	 * @return
	 */
	protected Map<String,Object> createBaseQueryMap(String fundId, String periodStr){
	    Map<String, Object> res = new HashMap<String,Object>();
	    res.put("fundId", fundId);
	    res.put("period", periodStr);
	    return res;
	}

}