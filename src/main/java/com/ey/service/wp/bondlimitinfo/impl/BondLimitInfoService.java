package com.ey.service.wp.bondlimitinfo.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.ey.dao.DaoSupport;
import com.ey.entity.Page;
import com.ey.service.wp.bondlimitinfo.BondLimitInfoManager;
import com.ey.util.AppUtil;
import com.ey.util.PageData;
import com.ey.util.UuidUtil;

/** 
 * 说明： 债券流通受限信息
 * 创建人：andychen
 * 创建时间：2017-12-03
 * @version
 */
@Service("bondlimitinfoService")
public class BondLimitInfoService implements BondLimitInfoManager{

	@Resource(name = "daoSupport")
	private DaoSupport dao;
	
	/**新增
	 * @param pd
	 * @throws Exception
	 */
	public void save(PageData pd)throws Exception{
		dao.save("BondLimitInfoMapper.save", pd);
	}
	
	/**删除
	 * @param pd
	 * @throws Exception
	 */
	public void delete(PageData pd)throws Exception{
		dao.delete("BondLimitInfoMapper.delete", pd);
	}
	
	/**修改
	 * @param pd
	 * @throws Exception
	 */
	public void edit(PageData pd)throws Exception{
		dao.update("BondLimitInfoMapper.edit", pd);
	}
	
	/**列表
	 * @param page
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List<PageData> list(Page page)throws Exception{
		return (List<PageData>)dao.findForList("BondLimitInfoMapper.datalistPage", page);
	}
	
	/**列表(全部)
	 * @param pd
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List<PageData> listAll(PageData pd)throws Exception{
		return (List<PageData>)dao.findForList("BondLimitInfoMapper.listAll", pd);
	}
	
	/**通过id获取数据
	 * @param pd
	 * @throws Exception
	 */
	public PageData findById(PageData pd)throws Exception{
		return (PageData)dao.findForObject("BondLimitInfoMapper.findById", pd);
	}
	
	/**批量删除
	 * @param ArrayDATA_IDS
	 * @throws Exception
	 */
	public void deleteAll(String[] ArrayDATA_IDS)throws Exception{
		dao.delete("BondLimitInfoMapper.deleteAll", ArrayDATA_IDS);
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
				pd.put("BONDLIMITINFO_ID", UuidUtil.get32UUID());
				pd.put("PERIOD", map.get("PERIOD"));
				pd.put("FIRM_CODE", map.get("FIRM_CODE"));
				pd.put("FUND_ID", map.get("FUND_ID"));
				pd.put("MMF", map.get("MMF"));
				pd.put("ACCOUNT_NUM", map.get("ACCOUNT_NUM"));
				pd.put("BOND_CODE", map.get("BOND_CODE"));
				pd.put("BOND_NAME", map.get("BOND_NAME"));
				pd.put("MARKET", map.get("MARKET"));
				pd.put("SUB_TYPE", map.get("SUB_TYPE"));
				pd.put("TRX_STATUS", map.get("TRX_STATUS"));
				pd.put("RESTRICT_TYPE", map.get("RESTRICT_TYPE"));
				pd.put("SUBSCRIBE_DATE", map.get("SUBSCRIBE_DATE"));
				pd.put("SUBSCRIBE_PRICE", map.get("SUBSCRIBE_PRICE"));
				pd.put("LIFTING_DATE", map.get("LIFTING_DATE"));
				pd.put("SUSPENSION_DATE", map.get("SUSPENSION_DATE"));
				pd.put("SUSPENSION_INFO", map.get("SUSPENSION_INFO"));
				pd.put("RESUMPTION_DATE", map.get("RESUMPTION_DATE"));
				pd.put("RESMPATION_OPEN_PRICE", map.get("RESMPATION_OPEN_PRICE"));
				pd.put("CREATOR", "CREATOR");
				pd.put("REVIEWER", "REVIEWER");
				pds.add(pd);
				idx++;
			}
			if (idx % AppUtil.BATCH_INSERT_COUNT == 0) {
				// 批量插入
				dao.save("BondLimitInfoMapper.saveBatch", pds);
				// 清空集合
				pds.clear();
			}
		}
		// 处理最后剩余数量
		if (pds.size() > 0) {
			// 批量插入
			dao.save("BondLimitInfoMapper.saveBatch", pds);
		}
	}
	
}

