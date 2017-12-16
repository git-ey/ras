package com.ey.service.system.report.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.ey.dao.DaoSupport;
import com.ey.entity.Page;
import com.ey.service.system.dictionaries.DictionariesManager;
import com.ey.service.system.report.ReportManager;
import com.ey.util.DateUtil;
import com.ey.util.PageData;
import com.google.common.collect.Maps;

/**
 * 说明： 报告导出 创建人：andychen 创建时间：2017-12-05
 * 
 * @version
 */
@Service("reportService")
public class ReportService implements ReportManager {

	@Resource(name = "daoSupport")
	private DaoSupport dao;
	@Resource(name = "dictionariesService")
	private DictionariesManager dictionariesManager;

	private final String CONTRACT_BEGIN_DATE = "合同生效日";

	private final String TRANSFORM_DATE = "转型日";

	private final String BALANCE_SHEET_DATE = "资产负债表日";

	/**
	 * 新增
	 * 
	 * @param pd
	 * @throws Exception
	 */
	@Override
	public void save(PageData pd) throws Exception {
		dao.save("ReportMapper.save", pd);
	}

	/**
	 * 删除
	 * 
	 * @param pd
	 * @throws Exception
	 */
	@Override
	public void delete(PageData pd) throws Exception {
		dao.delete("ReportMapper.delete", pd);
	}

	/**
	 * 修改
	 * 
	 * @param pd
	 * @throws Exception
	 */
	@Override
	public void edit(PageData pd) throws Exception {
		dao.update("ReportMapper.edit", pd);
	}

	/**
	 * 列表
	 * 
	 * @param page
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<PageData> list(Page page) throws Exception {
		return (List<PageData>) dao.findForList("ReportMapper.datalistPage", page);
	}

	/**
	 * 列表(全部)
	 * 
	 * @param pd
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<PageData> listAll(PageData pd) throws Exception {
		return (List<PageData>) dao.findForList("ReportMapper.listAll", pd);
	}

	/**
	 * 通过id获取数据
	 * 
	 * @param pd
	 * @throws Exception
	 */
	@Override
	public PageData findById(PageData pd) throws Exception {
		return (PageData) dao.findForObject("ReportMapper.findById", pd);
	}

	/**
	 * 批量删除
	 * 
	 * @param ArrayDATA_IDS
	 * @throws Exception
	 */
	@Override
	public void deleteAll(String[] ArrayDATA_IDS) throws Exception {
		dao.delete("ReportMapper.deleteAll", ArrayDATA_IDS);
	}

	/**
	 * 列表段落模板
	 * 
	 * @param pd
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<PageData> listParagraph(String paragraphCode) throws Exception {
		return (List<PageData>) dao.findForList("ReportMapper.listParagraph", paragraphCode);
	}

	/**
	 * 列表段落模板(全部)
	 * 
	 * @param pd
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<PageData> listParagraphAll(PageData pd) throws Exception {
		return (List<PageData>) dao.findForList("ReportMapper.listParagraphAll", pd);
	}

	/**
	 * 获取报告日期信息
	 * 
	 * @param period
	 *            期间
	 * @param dateFrom
	 * @param dateTo
	 * @param dateTransform
	 * @return
	 */
	@Override
	public Map<String, Object> getDateInfo(String period, Date dateFrom, Date dateTo, Date dateTransform)
			throws Exception {
		Map<String, Object> infoMap = Maps.newHashMap();
		// 年
		String year = period.substring(0, 4);
		// 年第一天
		Date yearFirstDate = DateUtil.fomatDate(year + "0101", "yyyyMMdd");
		// 期间日期
		Date periodDate = DateUtil.fomatDate(period, "yyyyMMdd");
		// 本期资产负债表日
		infoMap.put("CURRENT_BS_DATE", periodDate);
		// 本期年
		infoMap.put("CURRENT_YEAR", year);
		// 本期年度
		infoMap.put("CURRENT_PERIOD", year + "年度");
		// 本期起始日来源&&本期起始日文本
		if (dateFrom.getTime() >= yearFirstDate.getTime() && dateFrom != dateTransform) {
			infoMap.put("CURRENT_INIT_SOURCE", CONTRACT_BEGIN_DATE);
			infoMap.put("CURRENT_INIT_TEXT", "基金合同生效日");
		} else if (dateFrom.getTime() >= yearFirstDate.getTime() && dateFrom == dateTransform) {
			infoMap.put("CURRENT_INIT_SOURCE", TRANSFORM_DATE);
			infoMap.put("CURRENT_INIT_TEXT", "基金转型日");
		} else {
			infoMap.put("CURRENT_INIT_SOURCE", BALANCE_SHEET_DATE);
			infoMap.put("CURRENT_INIT_TEXT", "");
		}
		// 本期起始日
		if (infoMap.get("CURRENT_INIT_SOURCE").equals(BALANCE_SHEET_DATE)) {
			infoMap.put("CURRENT_INIT_DATE", yearFirstDate);
		} else {
			infoMap.put("CURRENT_INIT_DATE", dateFrom);
		}
		// 本期截止日&&本期截止日来源
		if (dateTo == null) {
			infoMap.put("CURRENT_END_DATE", periodDate);
			infoMap.put("CURRENT_END_SOURCE", "资产负债表日");

		} else {
			infoMap.put("CURRENT_END_DATE", dateTo);
			infoMap.put("CURRENT_END_SOURCE", "合同终止日");
		}
		// 本期截止日文本
		infoMap.put("CURRENT_END_TXT", "");
		return infoMap;
	}

	/**
	 * 获取上一期报告日期信息
	 * 
	 * @param period
	 *            当前期间
	 * @param dateFrom
	 * @param dateTo
	 * @param dateTransform
	 * @return
	 */
	@Override
	public Map<String, Object> getLastDateInfo(String period, Date dateFrom, Date dateTo, Date dateTransform)
			throws Exception {
		String lastPeriod = (Integer.parseInt(period.substring(0, 4)) - 1) + period.substring(4, 8);
		Map<String, Object> lastInfoMap = this.getDateInfo(lastPeriod, dateFrom, dateTo, dateTransform);
		Map<String, Object> infoMap = Maps.newHashMap();
		// 上期资产负债表日
		infoMap.put("LAST_BS_DATE", lastInfoMap.get("CURRENT_BS_DATE"));
		// 上期年
		infoMap.put("LAST_YEAR", lastInfoMap.get("CURRENT_YEAR"));
		// 上期年度
		infoMap.put("LAST_PERIOD", lastInfoMap.get("CURRENT_PERIOD"));
		// 上期起始日
		infoMap.put("LAST_INIT_DATE", lastInfoMap.get("CURRENT_INIT_DATE"));
		// 上期起始日来源
		infoMap.put("LAST_INIT_SOURCE", lastInfoMap.get("CURRENT_INIT_SOURCE"));
		// 上期起始日文本
		infoMap.put("LAST_INIT_TEXT", lastInfoMap.get("CURRENT_INIT_TEXT"));
		// 上期截止日
		infoMap.put("LAST_END_DATE", lastInfoMap.get("CURRENT_END_DATE"));
		// 上期截止日来源
		infoMap.put("LAST_END_SOURCE", lastInfoMap.get("CURRENT_END_SOURCE"));
		// 上期截止日文本
		infoMap.put("LAST_END_TXT", lastInfoMap.get("CURRENT_END_TXT"));
		return infoMap;
	}

	/**
	 * 运行报告导出程序
	 * 
	 * @param pd
	 */
	public void exportReport(PageData pd) throws Exception {
		// 根据配置代码获取信息
		PageData p1 = dictionariesManager.findByCode(pd.getString("P1"));

		// 设置消息
		pd.put("RESULT", "S");
	}

}
