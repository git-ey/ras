package com.ey.service.system.fund.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.ey.dao.DaoSupport;
import com.ey.entity.Page;
import com.ey.service.system.fund.FundManager;
import com.ey.util.AppUtil;
import com.ey.util.PageData;

/** 
 * 说明： 基金信息
 * 创建人：andychen
 * 创建时间：2017-08-22
 * @version
 */
@Service("fundService")
public class FundService implements FundManager{

	@Resource(name = "daoSupport")
	private DaoSupport dao;
	
	/**新增
	 * @param pd
	 * @throws Exception
	 */
	@Override
    public void save(PageData pd)throws Exception{
		dao.save("FundMapper.save", pd);
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
			pd.put("FUND_ID", map.get("FUND_ID"));
			pd.put("FUND_CODE", map.get("FUND_CODE"));
			pd.put("FIRM_CODE", map.get("FIRM_CODE"));
			pd.put("SHORT_NAME", map.get("SHORT_NAME"));
			pd.put("FULL_NAME", map.get("FULL_NAME"));
			pd.put("FULL_NAME_ORIGINAL", map.get("FULL_NAME_ORIGINAL"));
			pd.put("LEDGER_NUM", map.get("LEDGER_NUM"));
			pd.put("TA_NAME", map.get("TA_NAME"));
			pd.put("FIN_SYSTEM", map.get("FIN_SYSTEM"));
			pd.put("STRUCTURED", map.get("STRUCTURED"));
			pd.put("GUARANTEED", map.get("GUARANTEED"));
			pd.put("CLOSED", map.get("CLOSED"));
			pd.put("SHHK", map.get("SHHK"));
			pd.put("QD", map.get("QD"));
			pd.put("MF", map.get("MF"));
			pd.put("IDX", map.get("IDX"));
			pd.put("LOF", map.get("LOF"));
			pd.put("ETF", map.get("ETF"));
			pd.put("FOF", map.get("FOF"));
			pd.put("UNIT", map.get("UNIT"));
			pd.put("DATE_FROM", map.get("DATE_FROM"));
			pd.put("DATE_TO", map.get("DATE_TO"));
			pd.put("DATE_TRANSFORM", map.get("DATE_TRANSFORM"));
			pd.put("DEPOSIT_TERM_PERIOD", map.get("DEPOSIT_TERM_PERIOD"));
			pd.put("INTEREST_RATE_PERIOD", map.get("INTEREST_RATE_PERIOD"));
			pd.put("OTHER_LIABILITIES", map.get("OTHER_LIABILITIES"));
			pd.put("DIRECT_DEALER_NAME", map.get("DIRECT_DEALER_NAME"));
			pd.put("DEALER_SEPERATE", map.get("DEALER_SEPERATE"));
			pd.put("U_ROW_SET", map.get("U_ROW_SET"));
			pd.put("RISK_VARIABLE", map.get("RISK_VARIABLE"));
			pd.put("PRICE_VARIABLE", map.get("PRICE_VARIABLE"));
			pd.put("INTEREST_SENSTVT_METHOD", map.get("INTEREST_SENSTVT_METHOD"));
			pd.put("INTEREST_SENSTVT_SOURSE", map.get("INTEREST_SENSTVT_SOURSE"));
			pd.put("PRICE_SENSTVT_METHOD", map.get("PRICE_SENSTVT_METHOD"));
			pd.put("PRICE_SENSTVT_INDEX", map.get("PRICE_SENSTVT_INDEX"));
			pd.put("INDEX_SOURSE", map.get("INDEX_SOURSE"));
			pd.put("PRICE_SENSTVT_BETA", map.get("PRICE_SENSTVT_BETA"));
			pd.put("BETA_SOURSE", map.get("BETA_SOURSE"));
			pd.put("INTEREST_SENSITIVITY", map.get("INTEREST_SENSITIVITY"));
			pd.put("PRICE_SENSITIVITY", map.get("PRICE_SENSITIVITY"));
			pd.put("LEVEL_SHARE", map.get("LEVEL_SHARE"));
			pd.put("NAV_ROUND", map.get("NAV_ROUND"));
			pd.put("COMPOUND", map.get("COMPOUND"));
			pd.put("ROR_BASELINE", map.get("ROR_BASELINE"));
			pd.put("ROR", map.get("ROR"));
			pd.put("LEVERAGE", map.get("LEVERAGE"));
			pd.put("EXERCISE_DATE_METHOD", map.get("EXERCISE_DATE_METHOD"));
			pd.put("ACTIVE", null == map.get("ACTIVE") ? "Y" : map.get("ACTIVE"));
			pd.put("STATUS", null == map.get("STATUS") ? "INITIAL" : map.get("STATUS"));
			pds.add(pd);
			if (idx % AppUtil.BATCH_INSERT_COUNT == 0) {
				// 批量插入
				dao.save("FundMapper.saveBatch", pds);
				// 清空集合
				pds.clear();
			}
			idx++;
		}
		// 处理最后剩余数量
		if (pds.size() > 0) {
			// 批量插入
			dao.save("FundMapper.saveBatch", pds);
		}
		// 批量更新
		dao.update("FundMapper.updateBatch", null);
	}
	
	/**删除
	 * @param pd
	 * @throws Exception
	 */
	@Override
    public void delete(PageData pd)throws Exception{
		dao.delete("FundMapper.delete", pd);
	}
	
	/**修改
	 * @param pd
	 * @throws Exception
	 */
	@Override
    public void edit(PageData pd)throws Exception{
		dao.update("FundMapper.edit", pd);
	}
	
	/**列表
	 * @param page
	 * @throws Exception
	 */
	@Override
    @SuppressWarnings("unchecked")
	public List<PageData> list(Page page)throws Exception{
		return (List<PageData>)dao.findForList("FundMapper.datalistPage", page);
	}
	
	/**列表(全部)
	 * @param pd
	 * @throws Exception
	 */
	@Override
    @SuppressWarnings("unchecked")
	public List<PageData> listAll(PageData pd)throws Exception{
		return (List<PageData>)dao.findForList("FundMapper.listAll", pd);
	}
	
	/**列表全部基金
	 * @param pd
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<PageData> listAllFund(PageData pd) throws Exception {
		return (List<PageData>)dao.findForList("FundMapper.listAllFund", pd);
	}
	
	/**通过id获取数据
	 * @param pd
	 * @throws Exception
	 */
	@Override
    public PageData findById(PageData pd)throws Exception{
		return (PageData)dao.findForObject("FundMapper.findById", pd);
	}
	
	/**批量删除
	 * @param ArrayDATA_IDS
	 * @throws Exception
	 */
	@Override
    public void deleteAll(String[] ArrayDATA_IDS)throws Exception{
		dao.delete("FundMapper.deleteAll", ArrayDATA_IDS);
	}
	
	/**查询明细总数
	 * @param pd
	 * @throws Exception
	 */
	@Override
    public PageData findCount(PageData pd) throws Exception {
		return (PageData)dao.findForObject("FundMapper.findCount", pd);
	}
	
}

