package com.ey.service.wp.seebond.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import org.springframework.stereotype.Service;
import com.ey.dao.DaoSupport;
import com.ey.entity.Page;
import com.ey.service.wp.seebond.SeeBondManager;
import com.ey.util.AppUtil;
import com.ey.util.PageData;
import com.ey.util.UuidUtil;

/** 
 * 说明： 中证估值
 * 创建人：andychen
 * 创建时间：2017-11-23
 * @version
 */
@Service("seebondService")
public class SeeBondService implements SeeBondManager{

	@Resource(name = "daoSupport")
	private DaoSupport dao;
	
	/**新增
	 * @param pd
	 * @throws Exception
	 */
	@Override
    public void save(PageData pd)throws Exception{
		dao.save("SeeBondMapper.save", pd);
	}
	
	/**删除
	 * @param pd
	 * @throws Exception
	 */
	@Override
    public void delete(PageData pd)throws Exception{
		dao.delete("SeeBondMapper.delete", pd);
	}
	
	/**修改
	 * @param pd
	 * @throws Exception
	 */
	@Override
    public void edit(PageData pd)throws Exception{
		dao.update("SeeBondMapper.edit", pd);
	}
	
	/**列表
	 * @param page
	 * @throws Exception
	 */
	@Override
    @SuppressWarnings("unchecked")
	public List<PageData> list(Page page)throws Exception{
		return (List<PageData>)dao.findForList("SeeBondMapper.datalistPage", page);
	}
	
	/**列表(全部)
	 * @param pd
	 * @throws Exception
	 */
	@Override
    @SuppressWarnings("unchecked")
	public List<PageData> listAll(PageData pd)throws Exception{
		return (List<PageData>)dao.findForList("SeeBondMapper.listAll", pd);
	}
	
	/**通过id获取数据
	 * @param pd
	 * @throws Exception
	 */
	@Override
    public PageData findById(PageData pd)throws Exception{
		return (PageData)dao.findForObject("SeeBondMapper.findById", pd);
	}
	
	/**批量删除
	 * @param ArrayDATA_IDS
	 * @throws Exception
	 */
	@Override
    public void deleteAll(String[] ArrayDATA_IDS)throws Exception{
		dao.delete("SeeBondMapper.deleteAll", ArrayDATA_IDS);
	}
	
	@Override
	public void saveBatch(List<Map> maps) throws Exception {
		int idx = 1;
		List<PageData> pds = new ArrayList<PageData>();
		for (Map<String, Object> map : maps) {
			if (null != map.get("PERIOD")) {
				PageData pd = new PageData();
				pd.put("SEEBOND_ID", UuidUtil.get32UUID());
				pd.put("PERIOD", map.get("PERIOD"));
				pd.put("VALUE_DATE", map.get("VALUE_DATE"));
				pd.put("SHH_CODE", map.get("SHH_CODE"));
				pd.put("SHZ_CODE", map.get("SHZ_CODE"));
				pd.put("INTER_BANK_CODE", map.get("INTER_BANK_CODE"));
				pd.put("CALCULATION_PRICE1", map.get("CALCULATION_PRICE1"));
				pd.put("CLEAN_PRICE1", map.get("CLEAN_PRICE1"));
				pd.put("YIELD_TO_MATURITY1", map.get("YIELD_TO_MATURITY1"));
				pd.put("MODIFIED_DURATION1", map.get("MODIFIED_DURATION1"));
				pd.put("CONVEXITY1", map.get("CONVEXITY1"));
				pd.put("CALCULATION_PRICE2", map.get("CALCULATION_PRICE2"));
				pd.put("CLEAN_PRICE2", map.get("CLEAN_PRICE2"));
				pd.put("YIELD_TO_MATURITY2", map.get("YIELD_TO_MATURITY2"));
				pd.put("MODIFIED_DURATION2", map.get("MODIFIED_DURATION2"));
				pd.put("CONVEXITY2", map.get("CONVEXITY2"));
				pd.put("RECOMMENDATION", map.get("RECOMMENDATION"));
				pd.put("ESTIMATED_COUPON", map.get("ESTIMATED_COUPON"));
				pd.put("ACCRUED_INTEREST", map.get("ACCRUED_INTEREST"));
				pd.put("RESERVE", map.get("RESERVE"));
				pd.put("ACTIVE", null == map.get("ACTIVE") ? "Y" : map.get("ACTIVE"));
				pd.put("STATUS", null == map.get("STATUS") ? "INITIAL" : map.get("STATUS"));
				pds.add(pd);
				idx++;
			}
			if (idx % AppUtil.BATCH_INSERT_COUNT == 0) {
				// 批量插入
				dao.save("SeeBondMapper.saveBatch", pds);
				// 清空集合
				pds.clear();
			}
		}
		// 处理最后剩余数量
		if (pds.size() > 0) {
			// 批量插入
			dao.save("SeeBondMapper.saveBatch", pds);
		}
		
		// 批量更新
		dao.save("SeeBondMapper.updateBatch", null);
		
	}
	
}

