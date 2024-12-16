package com.ey.service.system.stocklimit.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.ey.dao.DaoSupport;
import com.ey.entity.Page;
import com.ey.service.system.stocklimit.StockLimitManager;
import com.ey.util.AppUtil;
import com.ey.util.PageData;
import com.ey.util.UuidUtil;

/** 
 * 说明：EY_STOCK_LIMIT股票流通受限清单导入
 * 创建人：linnea
 * 创建时间：2019-12-27
 * @version
 */
@Service("stocklimitService")
public class StockLimitService implements StockLimitManager{

	@Resource(name = "daoSupport")
	private DaoSupport dao;
	
	/**新增
	 * @param pd
	 * @throws Exception
	 */
	@Override
    public void save(PageData pd)throws Exception{
		dao.save("StockLimitMapper.save", pd);
	}
	
	/**删除
	 * @param pd
	 * @throws Exception
	 */
	@Override
    public void delete(PageData pd)throws Exception{
		dao.delete("StockLimitMapper.delete", pd);
	}
	
	/**修改
	 * @param pd
	 * @throws Exception
	 */
	@Override
    public void edit(PageData pd)throws Exception{
		dao.update("StockLimitMapper.edit", pd);
	}
	
	/**列表
	 * @param page
	 * @throws Exception
	 */
	@Override
    @SuppressWarnings("unchecked")
	public List<PageData> list(Page page)throws Exception{
		return (List<PageData>)dao.findForList("StockLimitMapper.datalistPage", page);
	}
	
	/**列表(全部)
	 * @param pd
	 * @throws Exception
	 */
	@Override
    @SuppressWarnings("unchecked")
	public List<PageData> listAll(PageData pd)throws Exception{
		return (List<PageData>)dao.findForList("StockLimitMapper.listAll", pd);
	}
	
	/**通过id获取数据
	 * @param pd
	 * @throws Exception
	 */
	@Override
    public PageData findById(PageData pd)throws Exception{
		return (PageData)dao.findForObject("StockLimitMapper.findById", pd);
	}
	
	/**批量删除
	 * @param ArrayDATA_IDS
	 * @throws Exception
	 */
	@Override
    public void deleteAll(String[] ArrayDATA_IDS)throws Exception{
		dao.delete("StockLimitMapper.deleteAll", ArrayDATA_IDS);
	}
	
	@Override
	public void saveBatch(List<Map> maps) throws Exception {
		int idx = 1;
		List<PageData> pds = new ArrayList<PageData>();
		for (Map<String, Object> map : maps) {
			if (null != map.get("FIRM_CODE")) {
				PageData pd = new PageData();
				pd.put("STOCKLIMIT_ID", UuidUtil.get32UUID());
				pd.put("FIRM_CODE", map.get("FIRM_CODE"));
				pd.put("FUND_ID", map.get("FUND_ID"));
				pd.put("PERIOD", map.get("PERIOD"));
				pd.put("ACCOUNT_NUM", map.get("ACCOUNT_NUM"));
				pd.put("STOCK_CODE", map.get("STOCK_CODE"));
				pd.put("STOCK_NAME", map.get("STOCK_NAME"));
				pd.put("MARKET", map.get("MARKET"));
				pd.put("SUB_TYPE", map.get("SUB_TYPE"));
				pd.put("TRX_STATUS", map.get("TRX_STATUS"));
				pd.put("QUANTITY", map.get("QUANTITY"));
				pd.put("SUBSCRIBE_PRICE", map.get("SUBSCRIBE_PRICE"));
				pd.put("VAL_PRICE", map.get("VAL_PRICE"));
				pd.put("RESTRICT_TYPE", map.get("RESTRICT_TYPE"));
				pd.put("TOTAL_COST", map.get("TOTAL_COST"));
				pd.put("MKT_VALUE", map.get("MKT_VALUE"));
				pd.put("SUBSCRIBE_DATE", map.get("SUBSCRIBE_DATE"));
				pd.put("LIFTING_DATE", map.get("LIFTING_DATE"));
				pd.put("PUB_LIFTING_DATE", map.get("PUB_LIFTING_DATE"));
				pd.put("SUSPENSION_DATE", map.get("SUSPENSION_DATE"));
				pd.put("SUSPENSION_INFO", map.get("SUSPENSION_INFO"));
				pd.put("RESUMPTION_DATE", map.get("RESUMPTION_DATE"));
				pd.put("RESMPATION_OPEN_PRICE", map.get("RESMPATION_OPEN_PRICE"));
				pd.put("LIMIT_LEN", map.get("LIMIT_LEN"));// 20220630irene新增
				pd.put("DESCRIPTION", map.get("DESCRIPTION"));
				pd.put("CREATOR", map.get("CREATOR"));
				pd.put("REVIEWER", map.get("REVIEWER"));
				pd.put("ACTIVE", null == map.get("ACTIVE") ? "Y" : map.get("ACTIVE"));
				pds.add(pd);
				idx++;
			}
			if (idx % AppUtil.BATCH_INSERT_COUNT == 0) {
				// 批量插入
				dao.save("StockLimitMapper.saveBatch", pds);
				// 清空集合
				pds.clear();
			}
		}
		// 处理最后剩余数量
		if (pds.size() > 0) {
			// 批量插入
			dao.save("StockLimitMapper.saveBatch", pds);
		}
	}
	
}

