package com.ey.service.wp.stocklimitinfo.impl;

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
import com.ey.service.wp.stocklimitinfo.StockLimitInfoManager;

/** 
 * 说明： 股票流通受限信息
 * 创建人：andychen
 * 创建时间：2017-12-02
 * @version
 */
@Service("stocklimitinfoService")
public class StockLimitInfoService implements StockLimitInfoManager{

	@Resource(name = "daoSupport")
	private DaoSupport dao;
	
	/**新增
	 * @param pd
	 * @throws Exception
	 */
	public void save(PageData pd)throws Exception{
		dao.save("StockLimitInfoMapper.save", pd);
	}
	
	/**删除
	 * @param pd
	 * @throws Exception
	 */
	public void delete(PageData pd)throws Exception{
		dao.delete("StockLimitInfoMapper.delete", pd);
	}
	
	/**修改
	 * @param pd
	 * @throws Exception
	 */
	public void edit(PageData pd)throws Exception{
		dao.update("StockLimitInfoMapper.edit", pd);
	}
	
	/**列表
	 * @param page
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List<PageData> list(Page page)throws Exception{
		return (List<PageData>)dao.findForList("StockLimitInfoMapper.datalistPage", page);
	}
	
	/**列表(全部)
	 * @param pd
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List<PageData> listAll(PageData pd)throws Exception{
		return (List<PageData>)dao.findForList("StockLimitInfoMapper.listAll", pd);
	}
	
	/**通过id获取数据
	 * @param pd
	 * @throws Exception
	 */
	public PageData findById(PageData pd)throws Exception{
		return (PageData)dao.findForObject("StockLimitInfoMapper.findById", pd);
	}
	
	/**批量删除
	 * @param ArrayDATA_IDS
	 * @throws Exception
	 */
	public void deleteAll(String[] ArrayDATA_IDS)throws Exception{
		dao.delete("StockLimitInfoMapper.deleteAll", ArrayDATA_IDS);
	}
	
	@Override
	public void saveBatch(List<Map> maps) throws Exception {
		int idx = 1;
		List<PageData> pds = new ArrayList<PageData>();
		for (Map<String, Object> map : maps) {
			if (null != map.get("PERIOD")) {
				PageData pd = new PageData();
				pd.put("STOCKLIMITINFO_ID", UuidUtil.get32UUID());
				pd.put("PERIOD", map.get("PERIOD"));
				pd.put("FIRM_CODE", map.get("FIRM_CODE"));
				pd.put("FUND_ID", map.get("FUND_ID"));
				pd.put("ACCOUNT_NUM", map.get("ACCOUNT_NUM"));
				pd.put("STOCK_CODE", map.get("STOCK_CODE"));
				pd.put("STOCK_NAME", map.get("STOCK_NAME"));
				pd.put("MARKET", map.get("MARKET"));
				pd.put("SUB_TYPE", map.get("SUB_TYPE"));
				pd.put("TRX_STATUS", map.get("TRX_STATUS"));
				pd.put("RESTRICT_TYPE", map.get("RESTRICT_TYPE"));
				pd.put("SUBSCRIBE_DATE", map.get("SUBSCRIBE_DATE"));
				pd.put("SUBSCRIBE_PRICE", map.get("SUBSCRIBE_PRICE"));
				pd.put("LEFTING_DATE", map.get("LEFTING_DATE"));
				pd.put("CREATOR", map.get("CREATOR"));
				pd.put("REVIEWER", map.get("REVIEWER"));
				pd.put("ACTIVE", null == map.get("ACTIVE") ? "Y" : map.get("ACTIVE"));
				pds.add(pd);
				idx++;
			}
			if (idx % AppUtil.BATCH_INSERT_COUNT == 0) {
				// 批量插入
				dao.save("StockLimitInfoMapper.saveBatch", pds);
				// 清空集合
				pds.clear();
			}
		}
		// 处理最后剩余数量
		if (pds.size() > 0) {
			// 批量插入
			dao.save("StockLimitInfoMapper.saveBatch", pds);
		}
		
	}
	
}

