package com.ey.service.system.stock.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.ey.dao.DaoSupport;
import com.ey.entity.Page;
import com.ey.service.system.stock.StockManager;
import com.ey.util.AppUtil;
import com.ey.util.PageData;
import com.ey.util.UuidUtil;

/** 
 * 说明： 股票信息
 * 创建人：andychen
 * 创建时间：2017-11-02
 * @version
 */
@Service("stockService")
public class StockService implements StockManager{

	@Resource(name = "daoSupport")
	private DaoSupport dao;
	
	/**新增
	 * @param pd
	 * @throws Exception
	 */
	public void save(PageData pd)throws Exception{
		dao.save("StockMapper.save", pd);
	}
	
	/**删除
	 * @param pd
	 * @throws Exception
	 */
	public void delete(PageData pd)throws Exception{
		dao.delete("StockMapper.delete", pd);
	}
	
	/**修改
	 * @param pd
	 * @throws Exception
	 */
	public void edit(PageData pd)throws Exception{
		dao.update("StockMapper.edit", pd);
	}
	
	/**列表
	 * @param page
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List<PageData> list(Page page)throws Exception{
		return (List<PageData>)dao.findForList("StockMapper.datalistPage", page);
	}
	
	/**列表(全部)
	 * @param pd
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List<PageData> listAll(PageData pd)throws Exception{
		return (List<PageData>)dao.findForList("StockMapper.listAll", pd);
	}
	
	/**通过id获取数据
	 * @param pd
	 * @throws Exception
	 */
	public PageData findById(PageData pd)throws Exception{
		return (PageData)dao.findForObject("StockMapper.findById", pd);
	}
	
	/**批量删除
	 * @param ArrayDATA_IDS
	 * @throws Exception
	 */
	public void deleteAll(String[] ArrayDATA_IDS)throws Exception{
		dao.delete("StockMapper.deleteAll", ArrayDATA_IDS);
	}
	
	/**
	 * 批量新增
	 * 
	 * @param pds
	 * @throws Exception
	 */
	public void saveBatch(List<Map> maps) throws Exception {
		int idx = 1;
		List<PageData> pds = new ArrayList<PageData>();
		for (Map<String, Object> map : maps) {
			if (StringUtils.isNotBlank(map.get("CODE").toString())) {
				PageData pd = new PageData();
				pd.put("STOCK_ID", UuidUtil.get32UUID());
				pd.put("PERIOD", map.get("PERIOD"));
				pd.put("CODE", map.get("CODE"));
				pd.put("NAME", map.get("NAME"));
				pd.put("INDUSTRY", map.get("INDUSTRY"));
				pd.put("VALUATION_DATE", map.get("VALUATION_DATE"));
				pd.put("RECENT_TRX_DATE", map.get("RECENT_TRX_DATE"));
				pd.put("UNIT_PRICE", map.get("UNIT_PRICE"));
				pd.put("SUSPENSION", map.get("SUSPENSION"));
				pd.put("SUSPENSION_DATE", map.get("SUSPENSION_DATE"));
				pd.put("CLOSING_PRICE", map.get("CLOSING_PRICE"));
				pd.put("SUSPENSION_INFO", map.get("SUSPENSION_INFO"));
				pd.put("RESUMPTION_DATE", map.get("RESUMPTION_DATE"));
				pd.put("RESUMPTION_PRICE", map.get("RESUMPTION_PRICE"));
				pd.put("NEW_FLAG", map.get("NEW_FLAG"));
				pd.put("AFLOAT_DATE", map.get("AFLOAT_DATE"));
				pd.put("ACTIVE", map.get("ACTIVE") == null ? "Y" : map.get("ACTIVE"));
				pd.put("STATUS", map.get("STATUS"));
				pds.add(pd);
				idx++;
			}
			if (idx % AppUtil.BATCH_INSERT_COUNT == 0) {
				// 批量插入
				dao.save("StockMapper.saveBatch", pds);
				// 清空集合
				pds.clear();
			}
		}
		// 处理最后剩余数量
		if (pds.size() > 0) {
			// 批量插入
			dao.save("StockMapper.saveBatch", pds);
		}
		// 批量更新
		dao.update("StockMapper.updateBatch", null);
	}
	
}

