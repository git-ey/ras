package com.ey.service.system.beta.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.ey.dao.DaoSupport;
import com.ey.entity.Page;
import com.ey.service.system.beta.BetaManager;
import com.ey.util.AppUtil;
import com.ey.util.PageData;
import com.ey.util.UuidUtil;

/** 
 * 说明： BETA系数导入
 * 创建人：andychen
 * 创建时间：2017-11-23
 * @version
 */
@Service("betaService")
public class BetaService implements BetaManager{

	@Resource(name = "daoSupport")
	private DaoSupport dao;
	
	/**新增
	 * @param pd
	 * @throws Exception
	 */
	@Override
    public void save(PageData pd)throws Exception{
		dao.save("BetaMapper.save", pd);
	}
	
	/**删除
	 * @param pd
	 * @throws Exception
	 */
	@Override
    public void delete(PageData pd)throws Exception{
		dao.delete("BetaMapper.delete", pd);
	}
	
	/**修改
	 * @param pd
	 * @throws Exception
	 */
	@Override
    public void edit(PageData pd)throws Exception{
		dao.update("BetaMapper.edit", pd);
	}
	
	/**列表
	 * @param page
	 * @throws Exception
	 */
	@Override
    @SuppressWarnings("unchecked")
	public List<PageData> list(Page page)throws Exception{
		return (List<PageData>)dao.findForList("BetaMapper.datalistPage", page);
	}
	
	/**列表(全部)
	 * @param pd
	 * @throws Exception
	 */
	@Override
    @SuppressWarnings("unchecked")
	public List<PageData> listAll(PageData pd)throws Exception{
		return (List<PageData>)dao.findForList("BetaMapper.listAll", pd);
	}
	
	/**通过id获取数据
	 * @param pd
	 * @throws Exception
	 */
	@Override
    public PageData findById(PageData pd)throws Exception{
		return (PageData)dao.findForObject("BetaMapper.findById", pd);
	}
	
	/**批量删除
	 * @param ArrayDATA_IDS
	 * @throws Exception
	 */
	@Override
    public void deleteAll(String[] ArrayDATA_IDS)throws Exception{
		dao.delete("BetaMapper.deleteAll", ArrayDATA_IDS);
	}
	
	@Override
	public void saveBatch(List<Map> maps) throws Exception {
		int idx = 1;
		List<PageData> pds = new ArrayList<PageData>();
		for (Map<String, Object> map : maps) {
			if (null != map.get("STOCK_CODE")) {
				PageData pd = new PageData();
				pd.put("BETA_ID", UuidUtil.get32UUID());
				pd.put("PERIOD", map.get("PERIOD"));
				pd.put("SOURCE", map.get("SOURCE"));
				pd.put("STOCK_CODE", map.get("STOCK_CODE"));
				pd.put("BETA", map.get("BETA"));
				pd.put("ACTIVE", null == map.get("ACTIVE") ? "Y" : map.get("ACTIVE"));
				pds.add(pd);
				idx++;
			}
			if (idx % AppUtil.BATCH_INSERT_COUNT == 0) {
				// 批量插入
				dao.save("BetaMapper.saveBatch", pds);
				// 清空集合
				pds.clear();
			}
		}
		// 处理最后剩余数量
		if (pds.size() > 0) {
			// 批量插入
			dao.save("BetaMapper.saveBatch", pds);
		}
	}
	
}

