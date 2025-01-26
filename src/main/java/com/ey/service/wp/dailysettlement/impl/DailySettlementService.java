package com.ey.service.wp.dailysettlement.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.ey.dao.DaoSupport;
import com.ey.entity.Page;
import com.ey.service.wp.dailysettlement.DailySettlementManager;
import com.ey.util.AppUtil;
import com.ey.util.PageData;
import com.ey.util.UuidUtil;

/** 
 * 说明： 日交割汇总
 * 创建人：andychen
 * 创建时间：2017-09-24
 * @version
 */
@Service("dailysettlementService")
public class DailySettlementService implements DailySettlementManager{

	@Resource(name = "daoSupport")
	private DaoSupport dao;
	
	/**新增
	 * @param pd
	 * @throws Exception
	 */
	@Override
    public void save(PageData pd)throws Exception{
		dao.save("DailySettlementMapper.save", pd);
	}
	
	/**删除
	 * @param pd
	 * @throws Exception
	 */
	@Override
    public void delete(PageData pd)throws Exception{
		dao.delete("DailySettlementMapper.delete", pd);
	}
	
	/**修改
	 * @param pd
	 * @throws Exception
	 */
	@Override
    public void edit(PageData pd)throws Exception{
		dao.update("DailySettlementMapper.edit", pd);
	}
	
	/**列表
	 * @param page
	 * @throws Exception
	 */
	@Override
    @SuppressWarnings("unchecked")
	public List<PageData> list(Page page)throws Exception{
		return (List<PageData>)dao.findForList("DailySettlementMapper.datalistPage", page);
	}
	
	/**列表(全部)
	 * @param pd
	 * @throws Exception
	 */
	@Override
    @SuppressWarnings("unchecked")
	public List<PageData> listAll(PageData pd)throws Exception{
		return (List<PageData>)dao.findForList("DailySettlementMapper.listAll", pd);
	}
	
	/**通过id获取数据
	 * @param pd
	 * @throws Exception
	 */
	@Override
    public PageData findById(PageData pd)throws Exception{
		return (PageData)dao.findForObject("DailySettlementMapper.findById", pd);
	}
	
	/**批量删除
	 * @param ArrayDATA_IDS
	 * @throws Exception
	 */
	@Override
    public void deleteAll(String[] ArrayDATA_IDS)throws Exception{
		dao.delete("DailySettlementMapper.deleteAll", ArrayDATA_IDS);
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
				pd.put("DAILY_SETTLEMENT_ID", UuidUtil.get32UUID());
				pd.put("FUND_ID", map.get("FUND_ID"));
				pd.put("SEQ", idx);
				pd.put("PERIOD", map.get("PERIOD"));
				pd.put("TRX_DATE", map.get("TRX_DATE"));
				pd.put("TRX_TYPE", map.get("TRX_TYPE"));
				pd.put("DEALER", map.get("DEALER"));
				pd.put("TRX_QUANTITY", map.get("TRX_QUANTITY"));
				pd.put("TRX_AMOUNT", map.get("TRX_AMOUNT"));
				pd.put("TRX_FEE", map.get("TRX_FEE"));
				pd.put("BACKEND_FEE", map.get("BACKEND_FEE"));
				pd.put("CONFIRMED_AMOUNT", map.get("CONFIRMED_AMOUNT"));
				pd.put("TRX_FEE", map.get("TRX_FEE"));
				pd.put("ACTIVE", null == map.get("ACTIVE") ? "Y" : map.get("ACTIVE"));
				pd.put("STATUS", null == map.get("STATUS") ? "Y" : map.get("STATUS"));
				pds.add(pd);
				idx++;
			}
			if (idx % AppUtil.BATCH_INSERT_COUNT == 0) {
				// 批量插入
				dao.save("DailySettlementMapper.saveBatch", pds);
				// 清空集合
				pds.clear();
			}
		}
		// 处理最后剩余数量
		if (pds.size() > 0) {
			// 批量插入
			dao.save("DailySettlementMapper.saveBatch", pds);
		}
	}
	
}

