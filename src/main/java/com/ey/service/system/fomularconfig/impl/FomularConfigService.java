package com.ey.service.system.fomularconfig.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.ey.dao.DaoSupport;
import com.ey.entity.Page;
import com.ey.service.system.fomularconfig.FomularConfigManager;
import com.ey.util.AppUtil;
import com.ey.util.PageData;
import com.ey.util.UuidUtil;

/** 
 * 说明：sys_fomular_config斜率法公式表导入
 * 创建人：linnea
 * 创建时间：2019-12-27
 * @version
 */
@Service("fomularconfigService")
public class FomularConfigService implements FomularConfigManager{

	@Resource(name = "daoSupport")
	private DaoSupport dao;
	
	/**新增
	 * @param pd
	 * @throws Exception
	 */
	@Override
    public void save(PageData pd)throws Exception{
		dao.save("FomularConfigMapper.save", pd);
	}
	
	/**删除
	 * @param pd
	 * @throws Exception
	 */
	@Override
    public void delete(PageData pd)throws Exception{
		dao.delete("FomularConfigMapper.delete", pd);
	}
	
	/**修改
	 * @param pd
	 * @throws Exception
	 */
	@Override
    public void edit(PageData pd)throws Exception{
		dao.update("FomularConfigMapper.edit", pd);
	}
	
	/**列表
	 * @param page
	 * @throws Exception
	 */
	@Override
    @SuppressWarnings("unchecked")
	public List<PageData> list(Page page)throws Exception{
		return (List<PageData>)dao.findForList("FomularConfigMapper.datalistPage", page);
	}
	
	/**列表(全部)
	 * @param pd
	 * @throws Exception
	 */
	@Override
    @SuppressWarnings("unchecked")
	public List<PageData> listAll(PageData pd)throws Exception{
		return (List<PageData>)dao.findForList("FomularConfigMapper.listAll", pd);
	}
	
	/**通过id获取数据
	 * @param pd
	 * @throws Exception
	 */
	@Override
    public PageData findById(PageData pd)throws Exception{
		return (PageData)dao.findForObject("FomularConfigMapper.findById", pd);
	}
	
	/**批量删除
	 * @param ArrayDATA_IDS
	 * @throws Exception
	 */
	@Override
    public void deleteAll(String[] ArrayDATA_IDS)throws Exception{
		dao.delete("FomularConfigMapper.deleteAll", ArrayDATA_IDS);
	}
	
	@Override
	public void saveBatch(List<Map> maps) throws Exception {
		int idx = 1;
		List<PageData> pds = new ArrayList<PageData>();
		for (Map<String, Object> map : maps) {
			if (null != map.get("TEST_TYPE")) {
				PageData pd = new PageData();
				pd.put("FOMULARCONFIG_ID", UuidUtil.get32UUID());
				pd.put("TEST_TYPE",map.get("TEST_TYPE"));
				pd.put("FORMULA_TYPE",map.get("FORMULA_TYPE"));
				pd.put("SEQ",map.get("SEQ"));
				pd.put("FUND_ID",map.get("FUND_ID"));
				pd.put("WEIGHT",map.get("WEIGHT"));
				pd.put("BASE_TYPE",map.get("BASE_TYPE"));
				pd.put("DATA_TYPE",map.get("DATA_TYPE"));
				pd.put("INDEX_CODE",map.get("INDEX_CODE"));
				pd.put("VALUE",map.get("VALUE"));
				pd.put("DATA_FROM",map.get("DATA_FROM"));
				pd.put("ACTIVE",map.get("ACTIVE"));
				pd.put("STATUS",map.get("STATUS"));
				pd.put("DESCRIPTION",map.get("DESCRIPTION"));
				pd.put("IMPORT_FILE_ID",map.get("IMPORT_FILE_ID"));
				pd.put("ACTIVE", null == map.get("ACTIVE") ? "Y" : map.get("ACTIVE"));
				pds.add(pd);
				idx++;
			}
			if (idx % AppUtil.BATCH_INSERT_COUNT == 0) {
				// 批量插入
				dao.save("FomularConfigMapper.saveBatch", pds);
				// 清空集合
				pds.clear();
			}
		}
		// 处理最后剩余数量
		if (pds.size() > 0) {
			// 批量插入
			dao.save("FomularConfigMapper.saveBatch", pds);
		}
	}
	
}

