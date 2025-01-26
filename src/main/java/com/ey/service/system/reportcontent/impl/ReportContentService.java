package com.ey.service.system.reportcontent.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.ey.dao.DaoSupport;
import com.ey.entity.Page;
import com.ey.service.system.reportcontent.ReportContentManager;
import com.ey.util.AppUtil;
import com.ey.util.PageData;
import com.ey.util.UuidUtil;

/** 
 * 说明： 报告文本信息表
 * 创建人：andychen
 * 创建时间：2017-12-31
 * @version
 */
@Service("reportcontentService")
public class ReportContentService implements ReportContentManager{

	@Resource(name = "daoSupport")
	private DaoSupport dao;
	
	/**新增
	 * @param pd
	 * @throws Exception
	 */
	public void save(PageData pd)throws Exception{
		dao.save("ReportContentMapper.save", pd);
	}
	
	/**删除
	 * @param pd
	 * @throws Exception
	 */
	public void delete(PageData pd)throws Exception{
		dao.delete("ReportContentMapper.delete", pd);
	}
	
	/**修改
	 * @param pd
	 * @throws Exception
	 */
	public void edit(PageData pd)throws Exception{
		dao.update("ReportContentMapper.edit", pd);
	}
	
	/**列表
	 * @param page
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List<PageData> list(Page page)throws Exception{
		return (List<PageData>)dao.findForList("ReportContentMapper.datalistPage", page);
	}
	
	/**列表(全部)
	 * @param pd
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List<PageData> listAll(PageData pd)throws Exception{
		return (List<PageData>)dao.findForList("ReportContentMapper.listAll", pd);
	}
	
	/**通过id获取数据
	 * @param pd
	 * @throws Exception
	 */
	public PageData findById(PageData pd)throws Exception{
		return (PageData)dao.findForObject("ReportContentMapper.findById", pd);
	}
	
	/**批量删除
	 * @param ArrayDATA_IDS
	 * @throws Exception
	 */
	public void deleteAll(String[] ArrayDATA_IDS)throws Exception{
		dao.delete("ReportContentMapper.deleteAll", ArrayDATA_IDS);
	}
	
	@Override
	public void saveBatch(List<Map> maps) throws Exception {
		int idx = 1;
		List<PageData> pds = new ArrayList<PageData>();
		for (Map<String, Object> map : maps) {
			if (null != map.get("FUND_ID")) {
				PageData pd = new PageData();
				pd.put("REPORTCONTENT_ID", UuidUtil.get32UUID());
				pd.put("FUND_ID", map.get("FUND_ID"));
				pd.put("FS_ATTR1", map.get("FS_ATTR1"));
				pd.put("FS_ATTR2", map.get("FS_ATTR2"));
				pd.put("FS_ATTR3", map.get("FS_ATTR3"));
				pd.put("COMISSION_NOTE", map.get("COMISSION_NOTE"));
				pd.put("MF_ATTR1", map.get("MF_ATTR1"));
				pd.put("MF_ATTR2", map.get("MF_ATTR2"));
				pd.put("MF_ATTR3", map.get("MF_ATTR3"));
				pd.put("MF_ATTR4", map.get("MF_ATTR4"));
				pd.put("MF_ATTR5", map.get("MF_ATTR5"));
				pd.put("CF_ATTR1", map.get("CF_ATTR1"));
				pd.put("CF_ATTR2", map.get("CF_ATTR2"));
				pd.put("CF_ATTR3", map.get("CF_ATTR3"));
				pd.put("CF_ATTR4", map.get("CF_ATTR4"));
				pd.put("CF_ATTR5", map.get("CF_ATTR5"));
				pd.put("SF_ATTR1", map.get("SF_ATTR1"));
				pd.put("SF_ATTR2", map.get("SF_ATTR2"));
				pd.put("SF_ATTR3", map.get("SF_ATTR3"));
				pd.put("SF_ATTR4", map.get("SF_ATTR4"));
				pd.put("SF_ATTR5", map.get("SF_ATTR5"));
				pd.put("LR", map.get("LR")); // yury,20201112,新增投资的长短期信用评级
				pd.put("EPA", map.get("EPA")); // yury,20201112,新增投资的长短期信用评级
				pd.put("PLR", map.get("PLR")); // yury,20201112,新增投资的长短期信用评级
				pd.put("MR", map.get("MR"));
				pd.put("IR", map.get("IR"));
				pd.put("IR_EXP", map.get("IR_EXP"));
				pd.put("IR_ATTR1", map.get("IR_ATTR1"));
				pd.put("ER", map.get("ER"));
				pd.put("IR_UP", map.get("IR_UP"));
				pd.put("IR_DOWN", map.get("IR_DOWN"));
				pd.put("PR_UP", map.get("PR_UP"));
				pd.put("PR_DOWN", map.get("PR_DOWN"));
				pd.put("PR_ATTR1", map.get("PR_ATTR1"));
				pd.put("PR_ATTR2", map.get("PR_ATTR2"));
				pd.put("PR_ATTR3", map.get("PR_ATTR3"));
				pd.put("FV_ATTR1", map.get("FV_ATTR1"));
				pd.put("FV_ATTR2", map.get("FV_ATTR2"));
				pd.put("FI", map.get("FI"));
				pd.put("CR", map.get("CR"));
				pd.put("CR_LOSS", map.get("CR_LOSS"));
				pd.put("ADDITAN_NOTE", map.get("ADDITAN_NOTE"));
				pd.put("THREE_LEVEL_CHANGE_NOTE", map.get("THREE_LEVEL_CHANGE_NOTE"));
				pd.put("RM", map.get("RM"));
				pd.put("ACTIVE", null == map.get("ACTIVE") ? "Y" : map.get("ACTIVE"));
				pds.add(pd);
				idx++;
			}
			if (idx % AppUtil.BATCH_INSERT_COUNT == 0) {
				// 批量插入
				dao.save("ReportContentMapper.saveBatch", pds);
				// 清空集合
				pds.clear();
			}
		}
		// 处理最后剩余数量
		if (pds.size() > 0) {
			// 批量插入
			dao.save("ReportContentMapper.saveBatch", pds);
		}
	}
	
}

