package com.ey.service.system.acctmapping.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.ey.dao.DaoSupport;
import com.ey.entity.Page;
import com.ey.service.system.acctmapping.AcctmappingManager;
import com.ey.util.AppUtil;
import com.ey.util.PageData;
import com.ey.util.UuidUtil;

/**
 * 说明： 科目映射 创建人：andychen 创建时间：2017-08-09
 * 
 * @version
 */
@Service("acctmappingService")
public class AcctmappingService implements AcctmappingManager {

	@Resource(name = "daoSupport")
	private DaoSupport dao;

	/**
	 * 新增
	 * 
	 * @param pd
	 * @throws Exception
	 */
	@Override
    public void save(PageData pd) throws Exception {
		dao.save("AcctmappingMapper.save", pd);
	}

	/**
	 * 批量新增
	 * 
	 * @param pds
	 * @throws Exception
	 */
	@Override
    public void saveBatch(List<Map> maps) throws Exception {
		int idx = 1;
		List<PageData> pds = new ArrayList<PageData>();
		for (Map<String, Object> map : maps) {
			if (null != map.get("FUND_ID")) {
				PageData pd = new PageData();
				pd.put("ACCTMAPPING_ID", UuidUtil.get32UUID());
				pd.put("FUND_ID", map.get("FUND_ID"));
				pd.put("ACCOUNT_NUM", map.get("ACCOUNT_NUM"));
				pd.put("ACCOUNT_DESCRIPTION", map.get("ACCOUNT_DESCRIPTION"));
				pd.put("LEVEL", map.get("LEVEL"));
				pd.put("CURRENCY", map.get("CURRENCY"));
				pd.put("TYPE", map.get("TYPE"));
				pd.put("ENTERABLE", map.get("ENTERABLE"));
				pd.put("EY_ACCOUNT_NUM", map.get("EY_ACCOUNT_NUM"));
				pd.put("ATTR1", map.get("ATTR1"));
				pd.put("ATTR2", map.get("ATTR2"));
				pd.put("ATTR3", map.get("ATTR3"));
				pd.put("ATTR4", map.get("ATTR4"));
				pd.put("ATTR5", map.get("ATTR5"));
				pd.put("ATTR6", map.get("ATTR6"));
				pd.put("ACTIVE", map.get("ACTIVE"));
				pd.put("STATUS", "");
				pds.add(pd);
				idx++;
			}
			if (idx % AppUtil.BATCH_INSERT_COUNT == 0) {
				// 批量插入
				dao.save("AcctmappingMapper.saveBatch", pds);
				// 清空集合
				pds.clear();
			}
		}
		// 处理最后剩余数量
		if (pds.size() > 0) {
			// 批量插入
			dao.save("AcctmappingMapper.saveBatch", pds);
		}
		// 批量更新
		dao.update("AcctmappingMapper.updateBatch", null);
		// 批量删除
		dao.update("AcctmappingMapper.deleteBatch", null);
	}

	/**
	 * 删除
	 * 
	 * @param pd
	 * @throws Exception
	 */
	@Override
    public void delete(PageData pd) throws Exception {
		dao.delete("AcctmappingMapper.delete", pd);
	}

	/**
	 * 修改
	 * 
	 * @param pd
	 * @throws Exception
	 */
	@Override
    public void edit(PageData pd) throws Exception {
		dao.update("AcctmappingMapper.edit", pd);
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
		return (List<PageData>) dao.findForList("AcctmappingMapper.datalistPage", page);
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
		return (List<PageData>) dao.findForList("AcctmappingMapper.listAll", pd);
	}

	/**
	 * 通过id获取数据
	 * 
	 * @param pd
	 * @throws Exception
	 */
	@Override
    public PageData findById(PageData pd) throws Exception {
		return (PageData) dao.findForObject("AcctmappingMapper.findById", pd);
	}

	/**
	 * 批量删除
	 * 
	 * @param ArrayDATA_IDS
	 * @throws Exception
	 */
	@Override
    public void deleteAll(String[] ArrayDATA_IDS) throws Exception {
		dao.delete("AcctmappingMapper.deleteAll", ArrayDATA_IDS);
	}

}
