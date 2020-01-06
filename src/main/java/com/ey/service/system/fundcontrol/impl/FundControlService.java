package com.ey.service.system.fundcontrol.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.ey.dao.DaoSupport;
import com.ey.entity.Page;
import com.ey.service.system.fundcontrol.FundControlManager;
import com.ey.util.AppUtil;
import com.ey.util.PageData;
import com.ey.util.UuidUtil;

/** 
 * 说明： 报告章节控制表
 * 创建人：andychen
 * 创建时间：2017-12-31
 * @version
 */
@Service("fundcontrolService")
public class FundControlService implements FundControlManager{

	@Resource(name = "daoSupport")
	private DaoSupport dao;
	
	/**新增
	 * @param pd
	 * @throws Exception
	 */
	public void save(PageData pd)throws Exception{
		dao.save("FundControlMapper.save", pd);
	}
	
	/**删除
	 * @param pd
	 * @throws Exception
	 */
	public void delete(PageData pd)throws Exception{
		dao.delete("FundControlMapper.delete", pd);
	}
	
	/**修改
	 * @param pd
	 * @throws Exception
	 */
	public void edit(PageData pd)throws Exception{
		dao.update("FundControlMapper.edit", pd);
	}
	
	/**列表
	 * @param page
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List<PageData> list(Page page)throws Exception{
		return (List<PageData>)dao.findForList("FundControlMapper.datalistPage", page);
	}
	
	/**列表(全部)
	 * @param pd
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List<PageData> listAll(PageData pd)throws Exception{
		return (List<PageData>)dao.findForList("FundControlMapper.listAll", pd);
	}
	
	/**通过id获取数据
	 * @param pd
	 * @throws Exception
	 */
	public PageData findById(PageData pd)throws Exception{
		return (PageData)dao.findForObject("FundControlMapper.findById", pd);
	}
	
	/**批量删除
	 * @param ArrayDATA_IDS
	 * @throws Exception
	 */
	public void deleteAll(String[] ArrayDATA_IDS)throws Exception{
		dao.delete("FundControlMapper.deleteAll", ArrayDATA_IDS);
	}
	
	@Override
	public void saveBatch(List<Map> maps) throws Exception {
		int idx = 1;
		List<PageData> pds = new ArrayList<PageData>();
		for (Map<String, Object> map : maps) {
			if (null != map.get("FUND_ID")) {
				PageData pd = new PageData();
				pd.put("FUNDCONTROL_ID", UuidUtil.get32UUID());
				pd.put("FUND_ID", map.get("FUND_ID"));
				pd.put("STOCK_ALL", map.get("STOCK_ALL"));
				pd.put("STOCK_BS", map.get("STOCK_BS"));
				pd.put("STOCK_R", map.get("STOCK_R"));
				pd.put("STOCK_P", map.get("STOCK_P"));
				pd.put("BOND_ALL", map.get("BOND_ALL"));
				pd.put("BOND_BS", map.get("BOND_BS"));
				pd.put("BOND_R", map.get("BOND_R"));
				pd.put("BOND_P", map.get("BOND_P"));
				pd.put("GOLD_ALL", map.get("GOLD_ALL"));
				pd.put("GOLD_BS", map.get("GOLD_BS"));
				pd.put("GOLD_R", map.get("GOLD_R"));
				pd.put("GOLD_P", map.get("GOLD_P"));
				pd.put("RISK_LOC", map.get("RISK_LOC"));
				pd.put("RISK_S_INT", map.get("RISK_S_INT"));
				pd.put("RISK_S_PRICE", map.get("RISK_S_PRICE"));
				pd.put("RISK_E_PRICE", map.get("RISK_E_PRICE"));
				pd.put("ACTIVE", null == map.get("ACTIVE") ? "Y" : map.get("ACTIVE"));
				pds.add(pd);
				idx++;
			}
			if (idx % AppUtil.BATCH_INSERT_COUNT == 0) {
				// 批量插入
				dao.save("FundControlMapper.saveBatch", pds);
				// 清空集合
				pds.clear();
			}
		}
		// 处理最后剩余数量
		if (pds.size() > 0) {
			// 批量插入
			dao.save("FundControlMapper.saveBatch", pds);
		}
	}
	
}

