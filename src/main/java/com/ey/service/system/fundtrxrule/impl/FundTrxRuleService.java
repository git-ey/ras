package com.ey.service.system.fundtrxrule.impl;

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
import com.ey.service.system.fundtrxrule.FundTrxRuleManager;

/** 
 * 说明： 基金申赎款划款规则
 * 创建人：andychen
 * 创建时间：2017-12-02
 * @version
 */
@Service("fundtrxruleService")
public class FundTrxRuleService implements FundTrxRuleManager{

	@Resource(name = "daoSupport")
	private DaoSupport dao;
	
	/**新增
	 * @param pd
	 * @throws Exception
	 */
	public void save(PageData pd)throws Exception{
		dao.save("FundTrxRuleMapper.save", pd);
	}
	
	/**删除
	 * @param pd
	 * @throws Exception
	 */
	public void delete(PageData pd)throws Exception{
		dao.delete("FundTrxRuleMapper.delete", pd);
	}
	
	/**修改
	 * @param pd
	 * @throws Exception
	 */
	public void edit(PageData pd)throws Exception{
		dao.update("FundTrxRuleMapper.edit", pd);
	}
	
	/**列表
	 * @param page
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List<PageData> list(Page page)throws Exception{
		return (List<PageData>)dao.findForList("FundTrxRuleMapper.datalistPage", page);
	}
	
	/**列表(全部)
	 * @param pd
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List<PageData> listAll(PageData pd)throws Exception{
		return (List<PageData>)dao.findForList("FundTrxRuleMapper.listAll", pd);
	}
	
	/**通过id获取数据
	 * @param pd
	 * @throws Exception
	 */
	public PageData findById(PageData pd)throws Exception{
		return (PageData)dao.findForObject("FundTrxRuleMapper.findById", pd);
	}
	
	/**批量删除
	 * @param ArrayDATA_IDS
	 * @throws Exception
	 */
	public void deleteAll(String[] ArrayDATA_IDS)throws Exception{
		dao.delete("FundTrxRuleMapper.deleteAll", ArrayDATA_IDS);
	}
	
	@Override
	public void saveBatch(List<Map> maps) throws Exception {
		int idx = 1;
		List<PageData> pds = new ArrayList<PageData>();
		for (Map<String, Object> map : maps) {
			if (null != map.get("FUND_ID")) {
				PageData pd = new PageData();
				pd.put("FUNDTRXRULE_ID", UuidUtil.get32UUID());
				pd.put("FUND_ID", map.get("FUND_ID"));
				pd.put("PERIOD", map.get("PERIOD"));
				pd.put("TYPE", map.get("TYPE"));
				pd.put("ATTR1", map.get("ATTR1"));
				pd.put("ATTR2", map.get("ATTR2"));
				pd.put("ATTR3", map.get("ATTR3"));
				pd.put("ATTR4", map.get("ATTR4"));
				pd.put("ATTR5", map.get("ATTR5"));
				pd.put("ATTR6", map.get("ATTR6"));
				pd.put("ATTR7", map.get("ATTR7"));
				pd.put("ATTR8", map.get("ATTR8"));
				pd.put("ATTR9", map.get("ATTR9"));
				pd.put("ACTIVE", null == map.get("ACTIVE") ? "Y" : map.get("ACTIVE"));
				pd.put("STATUS", null == map.get("STATUS") ? "INITIAL" : map.get("STATUS"));
				pds.add(pd);
				idx++;
			}
			if (idx % AppUtil.BATCH_INSERT_COUNT == 0) {
				// 批量插入
				dao.save("FundTrxRuleMapper.saveBatch", pds);
				// 清空集合
				pds.clear();
			}
		}
		// 处理最后剩余数量
		if (pds.size() > 0) {
			// 批量插入
			dao.save("FundTrxRuleMapper.saveBatch", pds);
		}
	}
	
}

