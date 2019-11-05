package com.ey.service.wp.bondinfo.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import org.springframework.stereotype.Service;
import com.ey.dao.DaoSupport;
import com.ey.entity.Page;
import com.ey.util.AppUtil;
import com.ey.util.PageData;
import com.ey.util.UuidUtil;
import com.ey.service.wp.bondinfo.BondInfoManager;

/** 
 * 说明： 债券投资信息
 * 创建人：andychen
 * 创建时间：2017-12-03
 * @version
 */
@Service("bondinfoService")
public class BondInfoService implements BondInfoManager{

	@Resource(name = "daoSupport")
	private DaoSupport dao;
	
	/**新增
	 * @param pd
	 * @throws Exception
	 */
	public void save(PageData pd)throws Exception{
		dao.save("BondInfoMapper.save", pd);
	}
	
	/**删除
	 * @param pd
	 * @throws Exception
	 */
	public void delete(PageData pd)throws Exception{
		dao.delete("BondInfoMapper.delete", pd);
	}
	
	/**修改
	 * @param pd
	 * @throws Exception
	 */
	public void edit(PageData pd)throws Exception{
		dao.update("BondInfoMapper.edit", pd);
	}
	
	/**列表
	 * @param page
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List<PageData> list(Page page)throws Exception{
		return (List<PageData>)dao.findForList("BondInfoMapper.datalistPage", page);
	}
	
	/**列表(全部)
	 * @param pd
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List<PageData> listAll(PageData pd)throws Exception{
		return (List<PageData>)dao.findForList("BondInfoMapper.listAll", pd);
	}
	
	/**通过id获取数据
	 * @param pd
	 * @throws Exception
	 */
	public PageData findById(PageData pd)throws Exception{
		return (PageData)dao.findForObject("BondInfoMapper.findById", pd);
	}
	
	/**批量删除
	 * @param ArrayDATA_IDS
	 * @throws Exception
	 */
	public void deleteAll(String[] ArrayDATA_IDS)throws Exception{
		dao.delete("BondInfoMapper.deleteAll", ArrayDATA_IDS);
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
			if (null != map.get("PERIOD")) {
				PageData pd = new PageData();
				pd.put("BONDINFO_ID", UuidUtil.get32UUID());
				pd.put("PERIOD", map.get("PERIOD"));
				pd.put("BOND_CODE", map.get("BOND_CODE"));
				pd.put("BOND_NAME", map.get("BOND_NAME"));
				pd.put("FULL_NAME", map.get("FULL_NAME"));
				pd.put("MARKET", map.get("MARKET"));
				pd.put("BOND_TYPE", map.get("BOND_TYPE"));
				pd.put("SUSPENSION", map.get("SUSPENSION"));
				pd.put("SUSPENSION_INFO", map.get("SUSPENSION_INFO"));
				pd.put("PAR_VALUE_ISSUE", map.get("PAR_VALUE_ISSUE"));
				pd.put("PAR_VALUE_LAST", map.get("PAR_VALUE_LAST"));
				pd.put("COUPON_RATE", map.get("COUPON_RATE"));
				pd.put("ISSUE_PRICE", map.get("ISSUE_PRICE"));
				pd.put("DATE_FROM", map.get("DATE_FROM"));
				pd.put("DATE_TO", map.get("DATE_TO"));
				pd.put("DATE_PAY", map.get("DATE_PAY"));
				pd.put("INTEREST_MODE", map.get("INTEREST_MODE"));
				pd.put("PAYMENT_METHOD", map.get("PAYMENT_METHOD"));
				pd.put("PAYMENT_TIMES_YEAR", map.get("PAYMENT_TIMES_YEAR"));
				pd.put("INTEREST_PAY_METHOD", map.get("INTEREST_PAY_METHOD"));
				pd.put("INTEREST_RULE_TYPE", map.get("INTEREST_RULE_TYPE"));
				pd.put("PAY_DATE_YEAR", map.get("PAY_DATE_YEAR"));
				pd.put("PAY_DATE_LAST", map.get("PAY_DATE_LAST"));
				pd.put("PAY_DATE_NEXT", map.get("PAY_DATE_NEXT"));
				pd.put("BOND_RATING", map.get("BOND_RATING"));
				pd.put("BOND_RATING_ORG", map.get("BOND_RATING_ORG"));
				pd.put("BOND_RATING_DATE", map.get("BOND_RATING_DATE"));
				pd.put("ENTITY_RATING", map.get("ENTITY_RATING"));
				pd.put("ENTITY_RATING_ORG", map.get("ENTITY_RATING_ORG"));
				pd.put("ENTITY_RATING_DATE", map.get("ENTITY_RATING_DATE"));
				pd.put("TAX_FREE", map.get("TAX_FREE"));
				pd.put("TAX_RATE", map.get("TAX_RATE"));
				pd.put("SPECIAL_CLAUSE", map.get("SPECIAL_CLAUSE"));
				pd.put("EARLY_EXERCISE", map.get("EARLY_EXERCISE"));
				pd.put("YEAR_N", map.get("YEAR_N"));
				pd.put("INTEREST_RATE_OPTION", map.get("INTEREST_RATE_OPTION"));
				pd.put("SELL_BACK", map.get("SELL_BACK"));
				pd.put("REDEMPTION", map.get("REDEMPTION"));
				pds.add(pd);
				idx++;
			}
			if (idx % AppUtil.BATCH_INSERT_COUNT == 0) {
				// 批量插入
				dao.save("BondInfoMapper.saveBatch", pds);
				// 清空集合
				pds.clear();
			}
		}
		// 处理最后剩余数量
		if (pds.size() > 0) {
			// 批量插入
			dao.save("BondInfoMapper.saveBatch", pds);
		}
	}
	
}

