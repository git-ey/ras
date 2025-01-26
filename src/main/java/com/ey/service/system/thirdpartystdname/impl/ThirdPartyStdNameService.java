package com.ey.service.system.thirdpartystdname.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.ey.dao.DaoSupport;
import com.ey.entity.Page;
import com.ey.service.system.thirdpartystdname.ThirdPartyStdNameManager;
import com.ey.util.AppUtil;
import com.ey.util.PageData;
import com.ey.util.UuidUtil;

/** 
 * 说明： 第三方名称
 * 创建人：andychen
 * 创建时间：2017-12-23
 * @version
 */
@Service("thirdpartystdnameService")
public class ThirdPartyStdNameService implements ThirdPartyStdNameManager{

	@Resource(name = "daoSupport")
	private DaoSupport dao;
	
	/**新增
	 * @param pd
	 * @throws Exception
	 */
	public void save(PageData pd)throws Exception{
		dao.save("ThirdPartyStdNameMapper.save", pd);
	}
	
	/**删除
	 * @param pd
	 * @throws Exception
	 */
	public void delete(PageData pd)throws Exception{
		dao.delete("ThirdPartyStdNameMapper.delete", pd);
	}
	
	/**修改
	 * @param pd
	 * @throws Exception
	 */
	public void edit(PageData pd)throws Exception{
		dao.update("ThirdPartyStdNameMapper.edit", pd);
	}
	
	/**列表
	 * @param page
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List<PageData> list(Page page)throws Exception{
		return (List<PageData>)dao.findForList("ThirdPartyStdNameMapper.datalistPage", page);
	}
	
	/**列表(全部)
	 * @param pd
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List<PageData> listAll(PageData pd)throws Exception{
		return (List<PageData>)dao.findForList("ThirdPartyStdNameMapper.listAll", pd);
	}
	
	/**通过id获取数据
	 * @param pd
	 * @throws Exception
	 */
	public PageData findById(PageData pd)throws Exception{
		return (PageData)dao.findForObject("ThirdPartyStdNameMapper.findById", pd);
	}
	
	/**批量删除
	 * @param ArrayDATA_IDS
	 * @throws Exception
	 */
	public void deleteAll(String[] ArrayDATA_IDS)throws Exception{
		dao.delete("ThirdPartyStdNameMapper.deleteAll", ArrayDATA_IDS);
	}
	
	/**批量新增
	 * @param pds
	 * @throws Exception
	 */
	@Override
    public void saveBatch(List<Map> maps) throws Exception {
		int idx = 1;
		List<PageData> pds = new ArrayList<PageData>();
		for (Map<String, Object> map : maps) {
			PageData pd = new PageData();
			pd.put("THIRDPARTYSTDNAME_ID", UuidUtil.get32UUID());
			pd.put("TYPE", map.get("TYPE"));
			pd.put("FULL_NAME", map.get("FULL_NAME"));
			pd.put("SHORT_NAME", map.get("SHORT_NAME"));
			pd.put("ACTIVE", null == map.get("ACTIVE") ? "Y" : map.get("ACTIVE"));
			pd.put("STATUS", null == map.get("STATUS") ? "Y" : map.get("STATUS"));
			pds.add(pd);
			if (idx % AppUtil.BATCH_INSERT_COUNT == 0) {
				// 批量插入
				dao.save("ThirdPartyStdNameMapper.saveBatch", pds);
				// 清空集合
				pds.clear();
			}
			idx++;
		}
		// 处理最后剩余数量
		if (pds.size() > 0) {
			// 批量插入
			dao.save("ThirdPartyStdNameMapper.saveBatch", pds);
		}
	}
	
}

