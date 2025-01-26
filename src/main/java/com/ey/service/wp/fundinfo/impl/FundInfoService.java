package com.ey.service.wp.fundinfo.impl;

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
import com.ey.service.wp.fundinfo.FundInfoManager;

/** 
 * 说明： 基金投资信息
 * 创建人：andychen
 * 创建时间：2017-12-03
 * @version
 */
@Service("fundinfoService")
public class FundInfoService implements FundInfoManager{

	@Resource(name = "daoSupport")
	private DaoSupport dao;
	
	/**新增
	 * @param pd
	 * @throws Exception
	 */
	public void save(PageData pd)throws Exception{
		dao.save("FundInfoMapper.save", pd);
	}
	
	/**删除
	 * @param pd
	 * @throws Exception
	 */
	public void delete(PageData pd)throws Exception{
		dao.delete("FundInfoMapper.delete", pd);
	}
	
	/**修改
	 * @param pd
	 * @throws Exception
	 */
	public void edit(PageData pd)throws Exception{
		dao.update("FundInfoMapper.edit", pd);
	}
	
	/**列表
	 * @param page
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List<PageData> list(Page page)throws Exception{
		return (List<PageData>)dao.findForList("FundInfoMapper.datalistPage", page);
	}
	
	/**列表(全部)
	 * @param pd
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List<PageData> listAll(PageData pd)throws Exception{
		return (List<PageData>)dao.findForList("FundInfoMapper.listAll", pd);
	}
	
	/**通过id获取数据
	 * @param pd
	 * @throws Exception
	 */
	public PageData findById(PageData pd)throws Exception{
		return (PageData)dao.findForObject("FundInfoMapper.findById", pd);
	}
	
	/**批量删除
	 * @param ArrayDATA_IDS
	 * @throws Exception
	 */
	public void deleteAll(String[] ArrayDATA_IDS)throws Exception{
		dao.delete("FundInfoMapper.deleteAll", ArrayDATA_IDS);
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
				pd.put("FUNDINFO_ID", UuidUtil.get32UUID());
				pd.put("PERIOD", map.get("PERIOD"));
				pd.put("FUND_CODE", map.get("FUND_CODE"));
				pd.put("FUND_NAME", map.get("FUND_NAME"));
				pd.put("FUND_FULLNAME", map.get("FUND_FULLNAME"));
				pd.put("MARKET", map.get("MARKET"));
				pd.put("TRX_STATUS", map.get("TRX_STATUS"));
				pd.put("RE_STATUS", map.get("RE_STATUS"));
				pd.put("REGULAR_OPEN_FUND", map.get("REGULAR_OPEN_FUND"));
				pd.put("SFC_TYPE", map.get("SFC_TYPE"));
				pd.put("CLOSING_STATUS", map.get("CLOSING_STATUS"));
				pd.put("FUND_SUB_TYPE", map.get("FUND_SUB_TYPE"));
				pd.put("CLOSING_PRICE", map.get("CLOSING_PRICE"));
				pd.put("UNIT_NAV", map.get("UNIT_NAV"));
				pd.put("MODE", map.get("MODE")); // yury，20200910，EY_BOND_INFO新增MODE基金运作方式字段
				pds.add(pd);
				idx++;
			}
			if (idx % AppUtil.BATCH_INSERT_COUNT == 0) {
				// 批量插入
				dao.save("FundInfoMapper.saveBatch", pds);
				// 清空集合
				pds.clear();
			}
		}
		// 处理最后剩余数量
		if (pds.size() > 0) {
			// 批量插入
			dao.save("FundInfoMapper.saveBatch", pds);
		}
	}
	
}

