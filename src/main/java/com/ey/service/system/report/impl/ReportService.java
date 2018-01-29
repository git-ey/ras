package com.ey.service.system.report.impl;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.stereotype.Service;

import com.ey.dao.DaoSupport;
import com.ey.entity.Page;
import com.ey.service.system.config.ConfigManager;
import com.ey.service.system.dictionaries.DictionariesManager;
import com.ey.service.system.report.ReportManager;
import com.ey.service.wp.output.ReportExportManager;
import com.ey.util.DateUtil;
import com.ey.util.Logger;
import com.ey.util.PageData;
import com.ey.util.StringUtil;
import com.ey.util.fileexport.Constants;
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
    @Resource(name = "reportExportService")
    private ReportExportManager reportExportService;

	private final String CONTRACT_BEGIN_DATE = "合同生效日";

	private final String TRANSFORM_DATE = "转型日";

	private final String BALANCE_SHEET_DATE = "资产负债表日";

	private final String REP_TEMP_PATH = "REP_TEMP_PATH";
	
	private final Logger logger = Logger.getLogger(ReportService.class);

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
	 * 获取日期年月日
	 * 
	 * @param date
	 * @return
	 */
	private String getDateStr(Date date) {
	    DateFormat df = new SimpleDateFormat("yyyy年M月d日");
		return df.format(date);
	}
	
	public static void main(String[] args) {
	    
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
		// 年最后一天
		Date yearLastDate = DateUtil.fomatDate(year + "1231", "yyyyMMdd");
		// 期间日期
		Date periodDate = DateUtil.fomatDate(period, "yyyyMMdd");
		
		// 本期资产负债表日
		infoMap.put("CURRENT_BS_DATE", periodDate);
		
		// 本期年
		infoMap.put("CURRENT_YEAR", year);
		infoMap.put("CURRENT_YEAR_NUM", Integer.parseInt(year));
		
		// 本期起始日来源&&本期起始日文本
		if(DateUtils.truncatedCompareTo(dateFrom, yearFirstDate, Calendar.DATE) >= 0) {
		    if(dateTransform != null && DateUtils.truncatedEquals(dateFrom, dateTransform, Calendar.DATE)) {
		        infoMap.put("CURRENT_INIT_SOURCE", TRANSFORM_DATE);
		        infoMap.put("CURRENT_INIT_TEXT", "基金合同转型日");
		    }else {
		        infoMap.put("CURRENT_INIT_SOURCE", CONTRACT_BEGIN_DATE);
		        infoMap.put("CURRENT_INIT_TEXT", "基金合同生效日");
		    }
		} else {
			infoMap.put("CURRENT_INIT_SOURCE", BALANCE_SHEET_DATE);
			infoMap.put("CURRENT_INIT_TEXT", "资产负债表日");
		}
		
		// 本期起始日
		if (infoMap.get("CURRENT_INIT_SOURCE").equals(BALANCE_SHEET_DATE)) {
			infoMap.put("CURRENT_INIT_DATE", yearFirstDate);
		} else {
			infoMap.put("CURRENT_INIT_DATE", dateFrom);
		}
		
		// 本期截止日&&本期截止日来源
		if (dateTo == null || DateUtils.truncatedCompareTo(dateTo, periodDate, Calendar.DATE) > 0) {
			infoMap.put("CURRENT_END_DATE", periodDate);
			infoMap.put("CURRENT_END_SOURCE", "资产负债表日");
		} else {
			infoMap.put("CURRENT_END_DATE", dateTo);
			infoMap.put("CURRENT_END_SOURCE", "合同终止日");
		}

		// 本期年度
		if(DateUtils.truncatedEquals((Date)infoMap.get("CURRENT_INIT_DATE"), yearFirstDate, Calendar.DATE) 
		        && DateUtils.truncatedEquals((Date)infoMap.get("CURRENT_END_DATE"), yearLastDate, Calendar.DATE)) {
			infoMap.put("CURRENT_PERIOD", year + "年度");
		} else {
			infoMap.put("CURRENT_PERIOD", this.getDateStr((Date) infoMap.get("CURRENT_INIT_DATE"))
					+ '（' + infoMap.get("CURRENT_INIT_TEXT") + '）' + "至" + this.getDateStr((Date) infoMap.get("CURRENT_END_DATE")));
		}

		// 本期截止日文本
		infoMap.put("CURRENT_END_TXT", StringUtils.EMPTY);
		
		// 正文/表格时间段-当期
		String currentPeriod = String.valueOf(infoMap.get("CURRENT_PERIOD"));
		if(currentPeriod.indexOf("年度") > 0) {
		    infoMap.put("TXT_PERIOD_CURRENT", currentPeriod);
		    infoMap.put("TABLE_PERIOD_CURRENT", this.getDateStr((Date) infoMap.get("CURRENT_INIT_DATE")) + "至" + this.getDateStr((Date)infoMap.get("CURRENT_END_DATE")));
		}else {
		    infoMap.put("TXT_PERIOD_CURRENT", currentPeriod + "止期间");
		    infoMap.put("TABLE_PERIOD_CURRENT", currentPeriod);
		}
		String tablePeriodCurrent = String.valueOf(infoMap.get("TABLE_PERIOD_CURRENT"));
		String[] pair = StringUtil.splitStringPair(tablePeriodCurrent, "至", false);
		infoMap.put("TABLE_PERIOD_CURRENT_A", pair[0]);
		infoMap.put("TABLE_PERIOD_CURRENT_B", pair[1]);
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
		infoMap.put("LAST_YEAR_NUM", lastInfoMap.get("CURRENT_YEAR_NUM"));
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
		// 正文时间段-上期
		infoMap.put("TXT_PERIOD_LAST", lastInfoMap.get("TXT_PERIOD_CURRENT"));
		// 表格时间段-上期
		infoMap.put("TABLE_PERIOD_LAST", lastInfoMap.get("TABLE_PERIOD_CURRENT"));
		// 表格时间段-上期-A
        infoMap.put("TABLE_PERIOD_LAST_A", lastInfoMap.get("TABLE_PERIOD_CURRENT_A"));
        // 表格时间段-上期-B
        infoMap.put("TABLE_PERIOD_LAST_B", lastInfoMap.get("TABLE_PERIOD_CURRENT_B"));
		return infoMap;
	}
	
	/**
	 * 运行报告导出程序
	 * 
	 * @param pd
	 */
	public void exportReport(PageData pd) throws Exception {
		Map<String, Object> dateMap = Maps.newHashMap();
		Map<String, Object> dateMapLast = Maps.newHashMap();
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
		String tempNameKey;

		// 遍历处理基金导出
		if(CollectionUtils.isNotEmpty(funds)) {
		    String errorMsg = StringUtils.EMPTY;
		    for (PageData pfund : funds) {
		        Map<String, Object> exportParam = new HashMap<>();
		        // 获取日期信息
		        dateMap = this.getDateInfo(period, (Date) pfund.get("DATE_FROM"), (Date) pfund.get("DATE_TO"), (Date) pfund.get("DATE_TRANSFORM"));
		        dateMapLast = this.getLastDateInfo(period, (Date) pfund.get("DATE_FROM"), (Date) pfund.get("DATE_TO"), (Date) pfund.get("DATE_TRANSFORM"));
		        
//			日期DEBUG程序
//			String[] a = {"CURRENT_YEAR","CURRENT_YEAR_NUM","CURRENT_INIT_DATE","CURRENT_INIT_TEXT","CURRENT_INIT_SOURCE","CURRENT_PERIOD","CURRENT_BS_DATE","CURRENT_END_DATE","CURRENT_END_TXT","CURRENT_END_SOURCE","TXT_PERIOD_CURRENT","TABLE_PERIOD_CURRENT","TABLE_PERIOD_CURRENT_A","TABLE_PERIOD_CURRENT_B"};
//			String[] b = {"LAST_YEAR","LAST_YEAR_NUM","LAST_INIT_DATE","LAST_INIT_TEXT","LAST_INIT_SOURCE","LAST_PERIOD","LAST_BS_DATE","LAST_END_DATE","LAST_END_TXT","LAST_END_SOURCE","TXT_PERIOD_LAST","TABLE_PERIOD_LAST","TABLE_PERIOD_LAST_A","TABLE_PERIOD_LAST_B"};
//			for(String at : a) {
//			    System.out.println(at + " = " + dateMap.get(at));
//			}
//			for(String bt : b) {
//			    System.out.println(bt + " = " + dateMapLast.get(bt));
//			}
		        
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
		            p2TempName = p2TempName + "_" + pfund.getString("FUND_ID") + period + ".xml";
		        } else {
		            p2TempName = p2TempName + ".xml";
		        }
		        // P3
		        p3TempName = p3TempName + tempNameKey + ".ftl";
		        // P4
		        // 如果选择此种规则，则按照基金区分模板
		        if (pd.getString("P4").equals("P4_FSO_BF")) {
		            p4TempName = p4TempName + "_" + pfund.getString("FUND_ID") + period + ".xml";
		        } else {
		            p4TempName = p4TempName + ".xml";
		        }
		        // P5
		        p5TempName = p5TempName + tempNameKey + ".ftl";
		        
		        // 一段一段的整合报告
		        exportParam.put("dateInfo", dateMap);
		        exportParam.put("lastDateInfo", dateMapLast);
		        exportParam.put("PEROID", period);
		        exportParam.put("FUND_ID", pfund.getString("FUND_ID"));
		        
		        Map<String,Object> partName = new HashMap<>();
		        partName.put("P1", p1TempName);
		        partName.put("P2", p2TempName);
		        partName.put("P3", p3TempName);
		        partName.put("P4", p4TempName);
		        partName.put("P5", p5TempName);
		        exportParam.put("partName", partName);
		        exportParam.put("reportTempRootPath", reportTempRootPath);
		        exportParam.put("reportOutBoundPath", reportOutBoundPath);
		        
		        try {
		            this.reportExportService.doExport(reportOutBoundPath, Constants.EXPORT_AIM_FILE_NAME_REPORT, exportParam);
		        }catch (Exception ex) {
		            logger.error("报告导出异常: " + exportParam.toString(), ex);
		            errorMsg += (ex.getMessage() + '\n');
		        }
		    }
		    
		    if(errorMsg.length() != 0) {
		        throw new Exception(errorMsg);
		    }
		}

		// 设置消息
		pd.put("RESULT", "S");
	}

}
