package com.ey.service.system.report.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.ey.dao.DaoSupport;
import com.ey.entity.Page;
import com.ey.service.system.config.ConfigManager;
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
	@Resource(name = "configService")
	private ConfigManager configService;

	private final String CONTRACT_BEGIN_DATE = "合同生效日";

	private final String TRANSFORM_DATE = "转型日";

	private final String BALANCE_SHEET_DATE = "资产负债表日";

	private final String REP_TEMP_PATH = "REP_TEMP_PATH";

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
	 * 根据报告导出参数获取基金
	 * 
	 * @param pd
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<PageData> listReportFund(PageData pd) throws Exception {
		return (List<PageData>) dao.findForList("ReportMapper.listReportFund", pd);
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
		if (dateTo == null || dateTo.getTime() > periodDate.getTime()) {
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
		Map<String, Object> dateMap = Maps.newHashMap();
		// 根据参数获取基金信息
		// 期间
		String period = pd.getString("PERIOD");

		// 此次导出的基金集合
		List<PageData> funds = listReportFund(pd);

		// 报告导出模板根路径
		String reportTempRootPath = configService.findByCode(REP_TEMP_PATH);
		
		// 报告导出路径
		String reportOutBoundPath = pd.getString("OUTBOND_PATH");

		// 根据配置代码获取信息
		PageData p1 = dictionariesManager.findByCode(pd.getString("P1"));
		PageData p2 = dictionariesManager.findByCode(pd.getString("P2"));
		PageData p3 = dictionariesManager.findByCode(pd.getString("P3"));
		PageData p4 = dictionariesManager.findByCode(pd.getString("P4"));
		PageData p5 = dictionariesManager.findByCode(pd.getString("P5"));
		// 英文名用于存文件名关键字
		String p1TempName = p1.getString("NAME_EN");
		String p2TempName = p2.getString("NAME_EN");
		String p3TempName = p3.getString("NAME_EN");
		String p4TempName = p4.getString("NAME_EN");
		String p5TempName = p5.getString("NAME_EN");

		// 模板一期、二期关键字
		String tempNameKey = "_YOY";

		// 遍历处理基金导出
		for (PageData pfund : funds) {
			// 获取日期信息
			dateMap = this.getDateInfo(period, (Date) pfund.get("DATE_FROM"), (Date) pfund.get("DATE_TO"),
					(Date) pfund.get("DATE_TRANSFORM"));
			// -------获取报告模板地址-------//
			// 如果 本期起始日来源 为 “资产负债表日”取YOY,否则取Y
			if (dateMap.get("CURRENT_INIT_SOURCE").equals("资产负债表日")) {
				tempNameKey = "_YOY";
			} else {
				tempNameKey = "_Y";
			}
			// P1
			p1TempName = p1TempName + tempNameKey + ".ftl";
			// P2
			// 如果选择此种规则，则按照基金区分模板
			if (pd.getString("P2").equals("P2_FSO_BF")) {
				p2TempName = p2TempName + tempNameKey + "_" + pfund.getString("FUND_ID") + ".docx";
			} else {
				p2TempName = p2TempName + tempNameKey + ".docx";
			}
			// P3
			p3TempName = p3TempName + tempNameKey + ".ftl";
			// P4
			p4TempName = p4TempName + tempNameKey + ".ftl";
			// P5
			// 如果选择此种规则，则按照基金区分模板
			if (pd.getString("P5").equals("P5_FSO_BF")) {
				p5TempName = p5TempName + tempNameKey + "_" + pfund.getString("FUND_ID") + ".docx";
			} else {
				p5TempName = p5TempName + tempNameKey + ".docx";
			}
			
			// 一段一段的整合报告

		}


		// 设置消息
		pd.put("RESULT", "S");
	}

}
