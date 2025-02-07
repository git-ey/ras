package com.ey.service.system.userfund.impl;

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
import com.ey.service.system.userfund.UserFundManager;

/** 
 * 说明： 用户基金权限
 * 创建人：andychen
 * 创建时间：2019-11-18
 * @version
 */
@Service("userfundService")
public class UserFundService implements UserFundManager{

	@Resource(name = "daoSupport")
	private DaoSupport dao;
	
	/**新增
	 * @param pd
	 * @throws Exception
	 */
	public void save(PageData pd)throws Exception{
		dao.save("UserFundMapper.save", pd);
	}
	
	/**删除
	 * @param pd
	 * @throws Exception
	 */
	public void delete(PageData pd)throws Exception{
		dao.delete("UserFundMapper.delete", pd);
	}
	
	/**修改
	 * @param pd
	 * @throws Exception
	 */
	public void edit(PageData pd)throws Exception{
		dao.update("UserFundMapper.edit", pd);
	}
	
	/**列表
	 * @param page
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List<PageData> list(Page page)throws Exception{
		return (List<PageData>)dao.findForList("UserFundMapper.datalistPage", page);
	}
	
	/**列表(全部)
	 * @param pd
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List<PageData> listAll(PageData pd)throws Exception{
		return (List<PageData>)dao.findForList("UserFundMapper.listAll", pd);
	}
	
	/**通过id获取数据
	 * @param pd
	 * @throws Exception
	 */
	public PageData findById(PageData pd)throws Exception{
		return (PageData)dao.findForObject("UserFundMapper.findById", pd);
	}
	
	/**批量删除
	 * @param ArrayDATA_IDS
	 * @throws Exception
	 */
	public void deleteAll(String[] ArrayDATA_IDS)throws Exception{
		dao.delete("UserFundMapper.deleteAll", ArrayDATA_IDS);
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
			pd.put("USERFUND_ID", UuidUtil.get32UUID());
			pd.put("USERNAME", map.get("USERNAME"));
			pd.put("OWNFUND_ID", map.get("OWNFUND_ID"));
			pds.add(pd);
			if (idx % AppUtil.BATCH_INSERT_COUNT == 0) {
				// 批量插入
				dao.save("UserFundMapper.saveBatch", pds);
				// 清空集合
				pds.clear();
			}
			idx++;
		}
		// 处理最后剩余数量
		if (pds.size() > 0) {
			// 批量插入
			dao.save("UserFundMapper.saveBatch", pds);
		}
	}
	
}

