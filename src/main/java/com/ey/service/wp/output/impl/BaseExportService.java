package com.ey.service.wp.output.impl;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.annotation.Resource;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import com.ey.dao.DaoSupport;
import com.ey.service.wp.output.BaseExportManager;

/**
 * @name CExportService
 * @description 底稿输出基础服务
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
	    Map<String, Object> res = new HashMap<>();
	    res.put("fundId", fundId);
	    res.put("period", periodStr);
	    return res;
	}
	
	/**
	 * 根据基金ID获取基金信息
	 * @author Dai Zong 2017年11月8日
	 * 
	 * @param fundId 基金ID
	 * @return 基金信息
	 * @throws Exception 基金ID无效
	 */
	protected Map<String,String> selectFundInfo(String fundId) throws Exception{
	    Map<String, Object> query = new HashMap<>();
	    query.put("fundId", fundId);
	    @SuppressWarnings("unchecked")
        List<Map<String,Object>> resMapList = (List<Map<String,Object>>)this.dao.findForList("FundMapper.selectFundInfo", query);
        if(CollectionUtils.isEmpty(resMapList) || resMapList.size() != 1) {
            throw new Exception("基金ID " + fundId + " 无效");
        }
        Map<String, String> res = new HashMap<>();
        resMapList.get(0).forEach((k,v) -> res.put(k, String.valueOf(v)));
        return res;
    }
	
	private static final String MOTHER_LEVEL = "母基金";
	private static final String A_SMALL_LAVEL = "0";
	
	/**
	 * <p>基金level比较器</p>
	 * <p>优化了母基金的排序</p>
	 */
	protected static final Comparator<String> LEVEL_COMPARATOR = (level1, level2) -> {
        String a;
        String b;
        if(Objects.equals(MOTHER_LEVEL, level1)) {
            a = A_SMALL_LAVEL;
        }else {
            a = level1;
        }
        if(Objects.equals(MOTHER_LEVEL, level2)) {
            b = A_SMALL_LAVEL;
        }else {
            b = level2;
        }
        return a.compareTo(b);
    };

}