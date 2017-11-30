package com.ey.service.system.chinabond.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.ey.dao.DaoSupport;
import com.ey.entity.Page;
import com.ey.service.system.chinabond.BondChinaManager;
import com.ey.util.AppUtil;
import com.ey.util.PageData;
import com.ey.util.UuidUtil;

/** 
 * 说明： 中债估值
 * 创建人：andychen
 * 创建时间：2017-11-23
 * @version
 */
@Service("chinabondService")
public class BondChinaService implements BondChinaManager{

	@Resource(name = "daoSupport")
	private DaoSupport dao;
	
	/**新增
	 * @param pd
	 * @throws Exception
	 */
	@Override
    public void save(PageData pd)throws Exception{
		dao.save("ChinaBondMapper.save", pd);
	}
	
	/**删除
	 * @param pd
	 * @throws Exception
	 */
	@Override
    public void delete(PageData pd)throws Exception{
		dao.delete("ChinaBondMapper.delete", pd);
	}
	
	/**修改
	 * @param pd
	 * @throws Exception
	 */
	@Override
    public void edit(PageData pd)throws Exception{
		dao.update("ChinaBondMapper.edit", pd);
	}
	
	/**列表
	 * @param page
	 * @throws Exception
	 */
	@Override
    @SuppressWarnings("unchecked")
	public List<PageData> list(Page page)throws Exception{
		return (List<PageData>)dao.findForList("ChinaBondMapper.datalistPage", page);
	}
	
	/**列表(全部)
	 * @param pd
	 * @throws Exception
	 */
	@Override
    @SuppressWarnings("unchecked")
	public List<PageData> listAll(PageData pd)throws Exception{
		return (List<PageData>)dao.findForList("ChinaBondMapper.listAll", pd);
	}
	
	/**通过id获取数据
	 * @param pd
	 * @throws Exception
	 */
	@Override
    public PageData findById(PageData pd)throws Exception{
		return (PageData)dao.findForObject("ChinaBondMapper.findById", pd);
	}
	
	/**批量删除
	 * @param ArrayDATA_IDS
	 * @throws Exception
	 */
	@Override
    public void deleteAll(String[] ArrayDATA_IDS)throws Exception{
		dao.delete("ChinaBondMapper.deleteAll", ArrayDATA_IDS);
	}
	
	@Override
	public void saveBatch(List<Map> maps) throws Exception {
		int idx = 1;
		List<PageData> pds = new ArrayList<PageData>();
		for (Map<String, Object> map : maps) {
			if (null != map.get("BOND_CODE")) {
				PageData pd = new PageData();
				pd.put("CHINABOND_ID", UuidUtil.get32UUID());
				pd.put("BOND_CODE", map.get("BOND_CODE"));
				pd.put("BOND_NAME", map.get("BOND_NAME"));
				pd.put("VALUE_DATE", map.get("VALUE_DATE"));
				pd.put("VALUATION_NET_PRICE", map.get("VALUATION_NET_PRICE"));
				pd.put("VALUATION_RETURN", map.get("VALUATION_RETURN"));
				pd.put("DURATION", map.get("DURATION"));
				pd.put("CONVEXITY", map.get("CONVEXITY"));
				pd.put("RELIABILITY", map.get("RELIABILITY"));
				pd.put("VALUATION_PRICE_END", map.get("VALUATION_PRICE_END"));
				pd.put("INTEREST_END", map.get("INTEREST_END"));
				pd.put("ACTIVE", null == map.get("ACTIVE") ? "Y" : map.get("ACTIVE"));
				pds.add(pd);
				idx++;
			}
			if (idx % AppUtil.BATCH_INSERT_COUNT == 0) {
				// 批量插入
				dao.save("ChinaBondMapper.saveBatch", pds);
				// 清空集合
				pds.clear();
			}
		}
		// 处理最后剩余数量
		if (pds.size() > 0) {
			// 批量插入
			dao.save("ChinaBondMapper.saveBatch", pds);
		}
	}
	
}

