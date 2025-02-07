package com.ey.service.system.valueationindex.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.ey.dao.DaoSupport;
import com.ey.entity.Page;
import com.ey.service.system.valueationindex.ValueationIndexManager;
import com.ey.util.AppUtil;
import com.ey.util.PageData;
import com.ey.util.UuidUtil;

/** 
 * 说明： 估值方法索引
 * 创建人：andychen
 * 创建时间：2017-12-03
 * @version
 */
@Service("valueationindexService")
public class ValueationIndexService implements ValueationIndexManager{

	@Resource(name = "daoSupport")
	private DaoSupport dao;
	
	/**新增
	 * @param pd
	 * @throws Exception
	 */
	public void save(PageData pd)throws Exception{
		dao.save("ValueationIndexMapper.save", pd);
	}
	
	/**删除
	 * @param pd
	 * @throws Exception
	 */
	public void delete(PageData pd)throws Exception{
		dao.delete("ValueationIndexMapper.delete", pd);
	}
	
	/**修改
	 * @param pd
	 * @throws Exception
	 */
	public void edit(PageData pd)throws Exception{
		dao.update("ValueationIndexMapper.edit", pd);
	}
	
	/**列表
	 * @param page
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List<PageData> list(Page page)throws Exception{
		return (List<PageData>)dao.findForList("ValueationIndexMapper.datalistPage", page);
	}
	
	/**列表(全部)
	 * @param pd
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List<PageData> listAll(PageData pd)throws Exception{
		return (List<PageData>)dao.findForList("ValueationIndexMapper.listAll", pd);
	}
	
	/**通过id获取数据
	 * @param pd
	 * @throws Exception
	 */
	public PageData findById(PageData pd)throws Exception{
		return (PageData)dao.findForObject("ValueationIndexMapper.findById", pd);
	}
	
	/**批量删除
	 * @param ArrayDATA_IDS
	 * @throws Exception
	 */
	public void deleteAll(String[] ArrayDATA_IDS)throws Exception{
		dao.delete("ValueationIndexMapper.deleteAll", ArrayDATA_IDS);
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
			if (null != map.get("TYPE")) {
				PageData pd = new PageData();
				pd.put("VALUEATIONINDEX_ID", UuidUtil.get32UUID());
				pd.put("FIRM_CODE", map.get("FIRM_CODE"));
				pd.put("MMF", map.get("MMF"));
				pd.put("TYPE", map.get("TYPE"));
				pd.put("MARKET", map.get("MARKET"));
				pd.put("SUB_TYPE", map.get("SUB_TYPE"));
				pd.put("TRX_STATUS", map.get("TRX_STATUS"));
				pd.put("INTEREST_MODE", map.get("INTEREST_MODE"));
				pd.put("VAL_TYPE_INDEX", map.get("VAL_TYPE_INDEX"));
				pd.put("VAL_TYPE_CODE", map.get("VAL_TYPE_CODE"));
				pd.put("VAL_TYPE_DEXS", map.get("VAL_TYPE_DEXS"));
				pd.put("VAL_BASE_SOURCE", map.get("VAL_BASE_SOURCE"));
				pd.put("VAL_COLUMN", map.get("VAL_COLUMN"));
				pd.put("THREE_LEVEL", map.get("THREE_LEVEL"));
				pd.put("firm_code", map.get("firm_code"));
				pds.add(pd);
				idx++;
			}
			if (idx % AppUtil.BATCH_INSERT_COUNT == 0) {
				// 批量插入
				dao.save("ValueationIndexMapper.saveBatch", pds);
				// 清空集合
				pds.clear();
			}
		}
		// 处理最后剩余数量
		if (pds.size() > 0) {
			// 批量插入
			dao.save("ValueationIndexMapper.saveBatch", pds);
		}
	}
	
}

