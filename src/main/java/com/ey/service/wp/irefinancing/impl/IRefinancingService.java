package com.ey.service.wp.irefinancing.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.ey.dao.DaoSupport;
import com.ey.entity.Page;
import com.ey.service.wp.irefinancing.IRefinancingManager;
import com.ey.util.AppUtil;
import com.ey.util.PageData;
import com.ey.util.UuidUtil;

/** 
 * 说明： 转融通
 * 创建人：Irene
 * 创建时间：2021-08-13
 * @version
 */
@Service("irefinancingService")
public class IRefinancingService implements IRefinancingManager{

	@Resource(name = "daoSupport")
	private DaoSupport dao;
	
	/**新增
	 * @param pd
	 * @throws Exception
	 */
	public void save(PageData pd)throws Exception{
		dao.save("IRefinancingMapper.save", pd);
	}
	
	/**删除
	 * @param pd
	 * @throws Exception
	 */
	public void delete(PageData pd)throws Exception{
		dao.delete("IRefinancingMapper.delete", pd);
	}
	
	/**修改
	 * @param pd
	 * @throws Exception
	 */
	public void edit(PageData pd)throws Exception{
		dao.update("IRefinancingMapper.edit", pd);
	}
	
	/**列表
	 * @param page
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List<PageData> list(Page page)throws Exception{
		return (List<PageData>)dao.findForList("IRefinancingMapper.datalistPage", page);
	}
	
	/**列表(全部)
	 * @param pd
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List<PageData> listAll(PageData pd)throws Exception{
		return (List<PageData>)dao.findForList("IRefinancingMapper.listAll", pd);
	}
	
	/**通过id获取数据
	 * @param pd
	 * @throws Exception
	 */
	public PageData findById(PageData pd)throws Exception{
		return (PageData)dao.findForObject("IRefinancingMapper.findById", pd);
	}
	
	/**批量删除
	 * @param ArrayDATA_IDS
	 * @throws Exception
	 */
	public void deleteAll(String[] ArrayDATA_IDS)throws Exception{
		dao.delete("IRefinancingMapper.deleteAll", ArrayDATA_IDS);
	}
	
	@Override
	public void saveBatch(List<Map> maps) throws Exception {
		int idx = 1;
		List<PageData> pds = new ArrayList<PageData>();
		for (Map<String, Object> map : maps) {
			if (null != map.get("PERIOD")) {
				PageData pd = new PageData();
				pd.put("IREFINANCING_ID", UuidUtil.get32UUID());
				pd.put("FIRM_CODE", map.get("FIRM_CODE"));
				pd.put("FUND_ID", map.get("FUND_ID"));
				pd.put("PERIOD", map.get("PERIOD"));
				pd.put("CONTRACT_NUM", map.get("CONTRACT_NUM"));
				pd.put("BOND_CODE", map.get("BOND_CODE"));
				pd.put("BOND_NAME", map.get("BOND_NAME"));
				pd.put("TRX_DATE", map.get("TRX_DATE"));
				pd.put("DUE_DATE", map.get("DUE_DATE"));
				pd.put("UNIT_PRICE", map.get("UNIT_PRICE"));
				pd.put("QUANTITY", map.get("QUANTITY"));
				pd.put("VAL_VALUE", map.get("VAL_VALUE"));
				pd.put("MARKET", map.get("MARKET"));
				pds.add(pd);
				idx++;
			}
			if (idx % AppUtil.BATCH_INSERT_COUNT == 0) {
				// 批量插入
				dao.save("IRefinancingMapper.saveBatch", pds);
				// 清空集合
				pds.clear();
			}
		}
		// 处理最后剩余数量
		if (pds.size() > 0) {
			// 批量插入
			dao.save("IRefinancingMapper.saveBatch", pds);
		}
		
	}
	
}

