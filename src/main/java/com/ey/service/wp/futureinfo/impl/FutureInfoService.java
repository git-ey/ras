package com.ey.service.wp.futureinfo.impl;

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
import com.ey.service.wp.futureinfo.FutureInfoManager;

/** 
 * 说明： 期货投资信息
 * 创建人：andychen
 * 创建时间：2017-12-03
 * @version
 */
@Service("futureinfoService")
public class FutureInfoService implements FutureInfoManager{

	@Resource(name = "daoSupport")
	private DaoSupport dao;
	
	/**新增
	 * @param pd
	 * @throws Exception
	 */
	public void save(PageData pd)throws Exception{
		dao.save("FutureInfoMapper.save", pd);
	}
	
	/**删除
	 * @param pd
	 * @throws Exception
	 */
	public void delete(PageData pd)throws Exception{
		dao.delete("FutureInfoMapper.delete", pd);
	}
	
	/**修改
	 * @param pd
	 * @throws Exception
	 */
	public void edit(PageData pd)throws Exception{
		dao.update("FutureInfoMapper.edit", pd);
	}
	
	/**列表
	 * @param page
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List<PageData> list(Page page)throws Exception{
		return (List<PageData>)dao.findForList("FutureInfoMapper.datalistPage", page);
	}
	
	/**列表(全部)
	 * @param pd
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List<PageData> listAll(PageData pd)throws Exception{
		return (List<PageData>)dao.findForList("FutureInfoMapper.listAll", pd);
	}
	
	/**通过id获取数据
	 * @param pd
	 * @throws Exception
	 */
	public PageData findById(PageData pd)throws Exception{
		return (PageData)dao.findForObject("FutureInfoMapper.findById", pd);
	}
	
	/**批量删除
	 * @param ArrayDATA_IDS
	 * @throws Exception
	 */
	public void deleteAll(String[] ArrayDATA_IDS)throws Exception{
		dao.delete("FutureInfoMapper.deleteAll", ArrayDATA_IDS);
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
			if (null != map.get("PERIOD")) {
				PageData pd = new PageData();
				pd.put("FUTUREINFO_ID", UuidUtil.get32UUID());
				pd.put("PERIOD", map.get("PERIOD"));
				pd.put("FUTURE_CODE", map.get("FUTURE_CODE"));
				pd.put("FUTURE_NAME", map.get("FUTURE_NAME"));
				pd.put("MARKET", map.get("MARKET"));
				pd.put("SUB_TYPE", map.get("SUB_TYPE"));
				pd.put("TYPE", map.get("TYPE"));
				pd.put("OBJECT", map.get("OBJECT"));
				pd.put("MULTIPLIER", map.get("MULTIPLIER"));
				pd.put("UNIT_SETTL_PRICE", map.get("UNIT_SETTL_PRICE"));
				pds.add(pd);
				idx++;
			}
			if (idx % AppUtil.BATCH_INSERT_COUNT == 0) {
				// 批量插入
				dao.save("FutureInfoMapper.saveBatch", pds);
				// 清空集合
				pds.clear();
			}
		}
		// 处理最后剩余数量
		if (pds.size() > 0) {
			// 批量插入
			dao.save("FutureInfoMapper.saveBatch", pds);
		}
	}
	
}

