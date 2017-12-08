package com.ey.service.system.fundrelatedparty.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.ey.dao.DaoSupport;
import com.ey.entity.Page;
import com.ey.service.system.fundrelatedparty.FundRelatedPartyManager;
import com.ey.util.AppUtil;
import com.ey.util.PageData;
import com.ey.util.UuidUtil;

/** 
 * 说明： 基金关联方信息
 * 创建人：andychen
 * 创建时间：2017-12-02
 * @version
 */
@Service("fundrelatedpartyService")
public class FundRelatedPartyService implements FundRelatedPartyManager{

	@Resource(name = "daoSupport")
	private DaoSupport dao;
	
	/**新增
	 * @param pd
	 * @throws Exception
	 */
	public void save(PageData pd)throws Exception{
		dao.save("FundRelatedPartyMapper.save", pd);
	}
	
	/**删除
	 * @param pd
	 * @throws Exception
	 */
	public void delete(PageData pd)throws Exception{
		dao.delete("FundRelatedPartyMapper.delete", pd);
	}
	
	/**修改
	 * @param pd
	 * @throws Exception
	 */
	public void edit(PageData pd)throws Exception{
		dao.update("FundRelatedPartyMapper.edit", pd);
	}
	
	/**列表
	 * @param page
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List<PageData> list(Page page)throws Exception{
		return (List<PageData>)dao.findForList("FundRelatedPartyMapper.datalistPage", page);
	}
	
	/**列表(全部)
	 * @param pd
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List<PageData> listAll(PageData pd)throws Exception{
		return (List<PageData>)dao.findForList("FundRelatedPartyMapper.listAll", pd);
	}
	
	/**通过id获取数据
	 * @param pd
	 * @throws Exception
	 */
	public PageData findById(PageData pd)throws Exception{
		return (PageData)dao.findForObject("FundRelatedPartyMapper.findById", pd);
	}
	
	/**批量删除
	 * @param ArrayDATA_IDS
	 * @throws Exception
	 */
	public void deleteAll(String[] ArrayDATA_IDS)throws Exception{
		dao.delete("FundRelatedPartyMapper.deleteAll", ArrayDATA_IDS);
	}
	
	@Override
	public void saveBatch(List<Map> maps) throws Exception {
		int idx = 1;
		List<PageData> pds = new ArrayList<PageData>();
		for (Map<String, Object> map : maps) {
			if (null != map.get("FUND_ID")) {
				PageData pd = new PageData();
				pd.put("FUNDRELATEDPARTY_ID", UuidUtil.get32UUID());
				pd.put("FUND_ID", map.get("FUND_ID"));
				pd.put("PARTY_ID", map.get("PARTY_ID"));
				pd.put("PARTY_FULL_NAME", map.get("PARTY_FULL_NAME"));
				pd.put("PARTY_SHORT_NAME_1", map.get("PARTY_SHORT_NAME_1"));
				pd.put("PARTY_SHORT_NAME_2", map.get("PARTY_SHORT_NAME_2"));
				pd.put("PARTY_SHORT_NAME_3", map.get("PARTY_SHORT_NAME_3"));
				pd.put("RELATIONSHIP", map.get("RELATIONSHIP"));
				pd.put("STOCK_CODE", map.get("STOCK_CODE"));
				pd.put("BOND_CODE", map.get("BOND_CODE"));
				pd.put("FUND_CODE", map.get("FUND_CODE"));
				pd.put("ACTIVE", null == map.get("ACTIVE") ? "Y" : map.get("ACTIVE"));
				pd.put("STATUS", null == map.get("STATUS") ? "INITIAL" : map.get("STATUS"));
				pds.add(pd);
				idx++;
			}
			if (idx % AppUtil.BATCH_INSERT_COUNT == 0) {
				// 批量插入
				dao.save("FundRelatedPartyMapper.saveBatch", pds);
				// 清空集合
				pds.clear();
			}
		}
		// 处理最后剩余数量
		if (pds.size() > 0) {
			// 批量插入
			dao.save("FundRelatedPartyMapper.saveBatch", pds);
		}
	}
	
}

