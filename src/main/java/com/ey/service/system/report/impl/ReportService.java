package com.ey.service.system.report.impl;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
import com.ey.util.VbsUtil;
import com.ey.util.VbsUtil.Scripts;
import com.ey.util.fileexport.Constants;
import com.ey.util.fileexport.FileExportUtils;
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
     * @param paragraphCode
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
     * @param
     * @return
     *
     * 汇总：
     * CURRENT_BS_DATE       ：资产负债表日，格式：20201231，String类型
     * CURRENT_YEAR          ：资产负债表日的年，格式：2020，String类型
     * CURRENT_YEAR_NUM      ：资产负债表日的年，格式：2020，int类型
     * CURRENT_INIT_SOURCE   ：文字描述，不是日期。转型日 / 合同生效日 / 资产负债表日
     * CURRENT_INIT_TEXT     ：文字描述，不是日期。基金合同转型日 / 基金合同生效日 / 资产负债表日
     * CURRENT_INIT_DATE     ：（重要）本期起始日，转型基金为转型日，新基金为合同生效日，除此之外为0101。格式：20200101、20200603，Date对象
     * CURRENT_INIT_DATE_TEXT：（重要）本期起始日的文字格式。格式：2020年1月1日、2020年6月23日
     * CURRENT_END_DATE      ：（重要）本期截止日，普通基金为资产负债表日，终止的或需要转型的基金为合同终止日或转型日，格式：20201231、20200406，Date对象
     * CURRENT_END_DATE_TEXT ：（重要）本期截止日的文字格式。格式：2020年12月31日、2020年4月6日
     * CURRENT_END_SOURCE    ：文字描述，不是日期。资产负债表日 / 合同终止日
     * CURRENT_PERIOD        ：文字描述，不是日期。本期年度。有以下三种：
     *                         （1）期初为0101，且期末为1231，则为：2020年度
     *                         （2）期初为0101，期末 < 1231， 则为：2020年1月1日至2020年7月19日
     *                         （3）除以上情况，              则为：2020年x月x日（${CURRENT_INIT_TEXT}）至2020年12月31日。其中x月x日为${CURRENT_INIT_DATE}格式化为string
     * CURRENT_END_TXT       ：空
     * TXT_PERIOD_CURRENT    ：正文中的当期时间段文字描述，有两种情况：
     *                         （1）${CURRENT_PERIOD}包含“年度”两个字，则为：${CURRENT_PERIOD}
     *                         （2）除以上情况，                     ，则为：${CURRENT_PERIOD}止期间
     * TABLE_PERIOD_CURRENT  ：表格中的当期时间段文字描述，有两种情况：
     *                         （1）${CURRENT_PERIOD}包含“年度”两个字，则为：2020年1月1日至2020年12月31日
     *                         （2）除以上情况，                     ，则为：${CURRENT_PERIOD}止期间
     * TABLE_PERIOD_CURRENT_A：${TABLE_PERIOD_CURRENT}按照“至”分割后的第0个字符串（至在前）
     * TABLE_PERIOD_CURRENT_A：${TABLE_PERIOD_CURRENT}按照“至”分割后的第1个字符串（至在前）
     * TABLE_PERIOD_CURRENT_A1：${TABLE_PERIOD_CURRENT}按照“至”分割后的第0个字符串（至在后）
     * TABLE_PERIOD_CURRENT_B1：${TABLE_PERIOD_CURRENT}按照“至”分割后的第0个字符串（至在后）
     */
    @Override
    public Map<String, Object> getDateInfo(String period, Date dateFrom, Date dateTo, Date dateTransform,String fundType) // chenhy,20240223,新增基金和产品的区分
            throws Exception {
        Map<String, Object> infoMap = Maps.newHashMap();

        // 年，例如：2020
        String year = period.substring(0, 4);
        // 年第一天，例如：20200101，转日期对象
        Date yearFirstDate = DateUtil.fomatDate(year + "0101", "yyyyMMdd");
        // 年最后一天，例如：20201231，转日期对象
        Date yearLastDate = DateUtil.fomatDate(year + "1231", "yyyyMMdd");
        // 期间日期，例如：20201231，20200630，转日期对象
        Date periodDate = DateUtil.fomatDate(period, "yyyyMMdd");

        // 本期资产负债表日
        infoMap.put("CURRENT_BS_DATE", periodDate);

        // 本期年
        infoMap.put("CURRENT_YEAR", year);
        infoMap.put("CURRENT_YEAR_NUM", Integer.parseInt(year));


        if ("产品".equals(fundType)){
            // 本期起始日来源&&本期起始日文本
            if (DateUtils.truncatedCompareTo(dateFrom, yearFirstDate, Calendar.DATE) >= 0) {
                if (dateTransform != null && DateUtils.truncatedEquals(dateFrom, dateTransform, Calendar.DATE)) {
                    infoMap.put("CURRENT_INIT_SOURCE", TRANSFORM_DATE);
                    infoMap.put("CURRENT_INIT_TEXT", "资产管理计划转型日");
                } else {
                    infoMap.put("CURRENT_INIT_SOURCE", CONTRACT_BEGIN_DATE);
                    infoMap.put("CURRENT_INIT_TEXT", "资产管理计划成立日");
                }
            } else {
                infoMap.put("CURRENT_INIT_SOURCE", BALANCE_SHEET_DATE);
                infoMap.put("CURRENT_INIT_TEXT", "资产负债表日");
            }

        } else {
            // 本期起始日来源&&本期起始日文本
            if (DateUtils.truncatedCompareTo(dateFrom, yearFirstDate, Calendar.DATE) >= 0) {
                if (dateTransform != null && DateUtils.truncatedEquals(dateFrom, dateTransform, Calendar.DATE)) {
                    infoMap.put("CURRENT_INIT_SOURCE", TRANSFORM_DATE);
                    infoMap.put("CURRENT_INIT_TEXT", "基金合同转型日");
                } else {
                    infoMap.put("CURRENT_INIT_SOURCE", CONTRACT_BEGIN_DATE);
                    infoMap.put("CURRENT_INIT_TEXT", "基金合同生效日");
                }
            } else {
                infoMap.put("CURRENT_INIT_SOURCE", BALANCE_SHEET_DATE);
                infoMap.put("CURRENT_INIT_TEXT", "资产负债表日");
            }

        }

        // 本期起始日
        if (infoMap.get("CURRENT_INIT_SOURCE").equals(BALANCE_SHEET_DATE)) {
            infoMap.put("CURRENT_INIT_DATE", yearFirstDate);
        } else {
            infoMap.put("CURRENT_INIT_DATE", dateFrom);
        }
        infoMap.put("CURRENT_INIT_DATE_TEXT", this.getDateStr((Date)infoMap.get("CURRENT_INIT_DATE")));

        // 本期截止日&&本期截止日来源
        if (dateTo == null || DateUtils.truncatedCompareTo(dateTo, periodDate, Calendar.DATE) > 0) {
            infoMap.put("CURRENT_END_DATE", periodDate);
            infoMap.put("CURRENT_END_SOURCE", "资产负债表日");
        } else {
            infoMap.put("CURRENT_END_DATE", dateTo);
            infoMap.put("CURRENT_END_SOURCE", "合同终止日");
        }
        infoMap.put("CURRENT_END_DATE_TEXT", this.getDateStr((Date)infoMap.get("CURRENT_END_DATE")));

        // 本期年度
        if (DateUtils.truncatedEquals((Date) infoMap.get("CURRENT_INIT_DATE"), yearFirstDate, Calendar.DATE)
                && DateUtils.truncatedEquals((Date) infoMap.get("CURRENT_END_DATE"), yearLastDate, Calendar.DATE)) {
            infoMap.put("CURRENT_PERIOD", year + "年度");
        } else if (infoMap.get("CURRENT_INIT_SOURCE").equals(BALANCE_SHEET_DATE)) {
            infoMap.put("CURRENT_PERIOD", this.getDateStr((Date) infoMap.get("CURRENT_INIT_DATE")) + "至"
                    + this.getDateStr((Date) infoMap.get("CURRENT_END_DATE")));
        } else {
            infoMap.put("CURRENT_PERIOD",
                    this.getDateStr((Date) infoMap.get("CURRENT_INIT_DATE")) + '（' + infoMap.get("CURRENT_INIT_TEXT")
                            + '）' + "至" + this.getDateStr((Date) infoMap.get("CURRENT_END_DATE")));
        }

        // 本期截止日文本
        infoMap.put("CURRENT_END_TXT", StringUtils.EMPTY);

        // 正文/表格时间段-当期
        String currentPeriod = String.valueOf(infoMap.get("CURRENT_PERIOD"));
        if (currentPeriod.indexOf("年度") > 0) {
            infoMap.put("TXT_PERIOD_CURRENT", currentPeriod);
            infoMap.put("TABLE_PERIOD_CURRENT", this.getDateStr((Date) infoMap.get("CURRENT_INIT_DATE")) + "至"
                    + this.getDateStr((Date) infoMap.get("CURRENT_END_DATE")));
        } else {
            infoMap.put("TXT_PERIOD_CURRENT", currentPeriod + "止期间");
            infoMap.put("TABLE_PERIOD_CURRENT", currentPeriod);
        }
        String tablePeriodCurrent = String.valueOf(infoMap.get("TABLE_PERIOD_CURRENT"));
        String[] pair = StringUtil.splitStringPair(tablePeriodCurrent, "至", false);
        infoMap.put("TABLE_PERIOD_CURRENT_A", pair[0]);
        infoMap.put("TABLE_PERIOD_CURRENT_B", pair[1]);
        String[] pair1 = StringUtil.splitStringPair(tablePeriodCurrent, "至", true);
        infoMap.put("TABLE_PERIOD_CURRENT_A1", pair1[0]);
        infoMap.put("TABLE_PERIOD_CURRENT_B1", pair1[1]);
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
     * @param fundType
     * @return
     */
    @Override
    public Map<String, Object> getLastDateInfo(String period, Date dateFrom, Date dateTo, Date dateTransform, String fundType) //chenhy,20240223,新增基金和产品的区分
            throws Exception {
        // String lastPeriod = (Integer.parseInt(period.substring(0, 4)) - 1) +
        // period.substring(4, 8);
        String lastPeriod = (Integer.parseInt(period.substring(0, 4)) - 1) + "1231";
        Map<String, Object> lastInfoMap = this.getDateInfo(lastPeriod, dateFrom, dateTo, dateTransform,fundType);

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
        // 上期起始日（日期）文本,chenhy,20220922
        infoMap.put("LAST_INIT_DATE_TEXT", lastInfoMap.get("CURRENT_INIT_DATE_TEXT"));
        // 上期截止日
        infoMap.put("LAST_END_DATE", lastInfoMap.get("CURRENT_END_DATE"));
        // 上期截止日（日期）文本,chenhy,20220922
        infoMap.put("LAST_END_DATE_TEXT", lastInfoMap.get("CURRENT_END_DATE_TEXT"));
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
        // 表格时间段-上期-A1
        infoMap.put("TABLE_PERIOD_LAST_A1", lastInfoMap.get("TABLE_PERIOD_CURRENT_A1"));
        // 表格时间段-上期-B1
        infoMap.put("TABLE_PERIOD_LAST_B1", lastInfoMap.get("TABLE_PERIOD_CURRENT_B1"));
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
        String reptype = pd.getString("REPTYPE"); // 20200507,yury
        String templatePath = Constants.ExportPathEnum.getExportPath(pd.getString("WP_TYPE_C"));

        // 此次导出的基金集合
        List<PageData> funds = listReportFund(pd);

        // 报告导出模板根路径
        String reportTempRootPath = configService.findByCode(REP_TEMP_PATH);

        // 报告导出路径
        String reportOutBoundPath = pd.getString("OUTBOND_PATH");
        String reportOutBoundTempPath = reportOutBoundPath;
        int reportOutBoundPathLength = reportOutBoundPath.length();
        if(reportOutBoundPath.charAt(reportOutBoundPathLength - 1) == '/' || reportOutBoundPath.charAt(reportOutBoundPathLength - 1) == '\\') {
            reportOutBoundTempPath = reportOutBoundTempPath.substring(0, reportOutBoundPathLength - 1);
        }
        reportOutBoundTempPath = reportOutBoundTempPath + "_temp" + File.separatorChar;

        // 根据配置代码获取信息
        PageData p1 = dictionariesManager.findByCode(pd.getString("P1"));
        PageData p2 = dictionariesManager.findByCode(pd.getString("P2"));
        PageData p3 = dictionariesManager.findByCode(pd.getString("P3"));
        // PageData p4 = dictionariesManager.findByCode(pd.getString("P4"));
        PageData p5 = dictionariesManager.findByCode(pd.getString("P5"));
        // 英文名用于存文件名关键字
        String p1TempName = p1.getString("NAME_EN");
        String p2TempName = p2.getString("NAME_EN");
        String p3TempName = p3.getString("NAME_EN");
        // String p4TempName = p4.getString("NAME_EN");
        String p5TempName = p5.getString("NAME_EN");

        // 模板一期、二期关键字
        String tempNameKey;

        // 遍历处理基金导出
        if (CollectionUtils.isNotEmpty(funds)) {
            String errorMsg = StringUtils.EMPTY;
            for (PageData pfund : funds) {
                Map<String, Object> exportParam = new HashMap<>();
                // 获取日期信息
                // chenhy,20240223,新增基金和产品的区分
                dateMap = this.getDateInfo(period, (Date) pfund.get("DATE_FROM"), (Date) pfund.get("DATE_TO"),
                        (Date) pfund.get("DATE_TRANSFORM"),(String) pfund.get("FUND_TYPE"));
                dateMapLast = this.getLastDateInfo(period, (Date) pfund.get("DATE_FROM"), (Date) pfund.get("DATE_TO"),
                        (Date) pfund.get("DATE_TRANSFORM"), (String) pfund.get("FUND_TYPE"));

                // 日期DEBUG程序
                // String[] a =
                // {"CURRENT_YEAR","CURRENT_YEAR_NUM","CURRENT_INIT_DATE","CURRENT_INIT_TEXT","CURRENT_INIT_SOURCE","CURRENT_PERIOD","CURRENT_BS_DATE","CURRENT_END_DATE","CURRENT_END_TXT","CURRENT_END_SOURCE","TXT_PERIOD_CURRENT","TABLE_PERIOD_CURRENT","TABLE_PERIOD_CURRENT_A","TABLE_PERIOD_CURRENT_B","TABLE_PERIOD_CURRENT_A1","TABLE_PERIOD_CURRENT_B1"};
                // String[] b =
                // {"LAST_YEAR","LAST_YEAR_NUM","LAST_INIT_DATE","LAST_INIT_TEXT","LAST_INIT_SOURCE","LAST_PERIOD","LAST_BS_DATE","LAST_END_DATE","LAST_END_TXT","LAST_END_SOURCE","TXT_PERIOD_LAST","TABLE_PERIOD_LAST","TABLE_PERIOD_LAST_A","TABLE_PERIOD_LAST_B"};
                // for(String at : a) {
                // System.out.println(at + " = " + dateMap.get(at));
                // }
                // for(String bt : b) {
                // System.out.println(bt + " = " + dateMapLast.get(bt));
                // }
                // ↓-------获取报告模板地址-------↓//
                // 如果 本期起始日来源 为 “资产负债表日”取YOY,否则取Y
                if (dateMap.get("CURRENT_INIT_SOURCE").equals("资产负债表日")) {
                    tempNameKey = "_YOY";
                } else {
                    tempNameKey = "_Y";
                }
                String p1TempNameFinal = null;
                String p2TempNameFinal = null;
                String p3TempNameFinal = null;
                // String p4TempNameFinal = null;
                String p5TempNameFinal = null;
                // P1
                p1TempNameFinal = p1TempName + tempNameKey + ".ftl";
                // P2
                // 如果选择此种规则，则按照基金区分模板
                if (pd.getString("P2").equals("P2_FSO_BF")) {
                    p2TempNameFinal = p2TempName + "_" + pfund.getString("FUND_ID") + period + ".xml";
                } else {
                    p2TempNameFinal = p2TempName + ".xml";
                }
                // P3
                p3TempNameFinal = reptype.equals("年审报告")? p3TempName + tempNameKey + ".ftl" : p3TempName + tempNameKey + "_Mid.ftl"; // 20200507,yury,新增年报或中期的判断，中期报告的P3有单独的模板
                // P4
                // 如果选择此种规则，则按照基金区分模板
                // if (pd.getString("P4").equals("P4_FSO_BF")) {
                //     p4TempNameFinal = p4TempName + "_" + pfund.getString("FUND_ID") + period + ".xml";
                // } else {
                //     p4TempNameFinal = p4TempName + ".xml";
                // }
                // P5
                p5TempNameFinal = p5TempName + tempNameKey + ".ftl";
                // 归档
                Map<String, Object> partName = new HashMap<>();
                partName.put("P1", p1TempNameFinal);
                partName.put("P2", p2TempNameFinal);
                partName.put("P3", p3TempNameFinal);
                // partName.put("P4", p4TempNameFinal);
                partName.put("P5", p5TempNameFinal);
                // ↑-------获取报告模板地址-------↑//

                // 整理exportParam
                exportParam.put("dateInfo", dateMap);
                exportParam.put("lastDateInfo", dateMapLast);
                exportParam.put("PEROID", period);
                exportParam.put("FUND_ID", pfund.getString("FUND_ID"));
                exportParam.put("partName", partName);
                exportParam.put("reportTempRootPath", reportTempRootPath);
                exportParam.put("reportOutBoundPath", reportOutBoundTempPath);
                exportParam.put("REPTYPE", reptype); // 20200507,yury
                // 开始导出
                try {
                    this.reportExportService.doExport(reportOutBoundTempPath, Constants.EXPORT_AIM_FILE_NAME_REPORT, exportParam, templatePath);
                } catch (Exception ex) {
                    logger.error("报告导出异常: " + exportParam.toString(), ex);
                    errorMsg += (ex.getMessage() + '\n');
                }
            }
            // 异常处理
            if (errorMsg.length() != 0) {
                throw new Exception(errorMsg);
            }
            // ↓ daigaokuo@hotmail.com 2019-03-18 ↓
            // [IMP] VBS脚本运行时按期间+公司代码隔离
            String firmCode = pd.getString("FIRM_CODE");
            /* vbs创建文件夹时不能自动创建父级,因此手工创建一个父级目录 */
            FileExportUtils.createDir(reportOutBoundPath + File.separatorChar + firmCode);
            VbsUtil.callScript(
                            Scripts.WORKPAPER_AND_REPORT_CONVERTER,
                            reportOutBoundTempPath + File.separatorChar + firmCode,
                            reportOutBoundPath + File.separatorChar + firmCode
                        );
            // ↑ daigaokuo@hotmail.com 2019-03-18 ↑
//            FileUtils.deleteDirectory(new File(reportOutBoundTempPath));
        }

        // 设置消息
        pd.put("RESULT", "S");
    }

    /**
     * 导出单个报告到HttpResponse
     * @author Dai Zong 2018年3月13日
     *
     * @param request
     * @param response
     * @param pd
     * @throws Exception
     */
    public void exportReport(HttpServletRequest request, HttpServletResponse response, PageData pd) throws Exception {
        Map<String, Object> dateMap = Maps.newHashMap();
        Map<String, Object> dateMapLast = Maps.newHashMap();
        // 根据参数获取基金信息
        // 期间
        String period = pd.getString("PERIOD");
        String repType = period.substring(4, 8).equals("0630")? "中期报告" : "年审报告"; // 20200507,yury

        // 此次导出的基金集合
        PageData pfund = (PageData) dao.findForObject("ReportMapper.selectReportFundById", pd);
        if(pfund == null) {
            throw new IllegalArgumentException("没有此基金信息");
        }

        // 报告导出模板根路径
        String reportTempRootPath = configService.findByCode(REP_TEMP_PATH);

        // 根据配置代码获取信息
        PageData p1 = dictionariesManager.findByCode("P1_FSO_SH");
        PageData p2 = dictionariesManager.findByCode("P2_FSO_TY");
        PageData p3 = dictionariesManager.findByCode("P3_FSO_SH");
        // PageData p4 = dictionariesManager.findByCode("P4_FSO_TY");
        PageData p5 = dictionariesManager.findByCode("P5_FSO_SH");
        // 英文名用于存文件名关键字
        String p1TempName = p1.getString("NAME_EN");
        String p2TempName = p2.getString("NAME_EN");
        String p3TempName = p3.getString("NAME_EN");
        // String p4TempName = p4.getString("NAME_EN");
        String p5TempName = p5.getString("NAME_EN");

        // 模板一期、二期关键字
        String tempNameKey;

        // 遍历处理基金导出
        Map<String, Object> exportParam = new HashMap<>();
        // 获取日期信息
        // chenhy,20240223,新增基金和产品的区分
        dateMap = this.getDateInfo(period, (Date) pfund.get("DATE_FROM"), (Date) pfund.get("DATE_TO"), (Date) pfund.get("DATE_TRANSFORM"), (String) pfund.get("FUND_TYPE"));
        dateMapLast = this.getLastDateInfo(period, (Date) pfund.get("DATE_FROM"), (Date) pfund.get("DATE_TO"), (Date) pfund.get("DATE_TRANSFORM"), (String) pfund.get("FUND_TYPE"));

        // ↓-------获取报告模板地址-------↓//
        // 如果 本期起始日来源 为 “资产负债表日”取YOY,否则取Y
        if (dateMap.get("CURRENT_INIT_SOURCE").equals("资产负债表日")) {
            tempNameKey = "_YOY";
        } else {
            tempNameKey = "_Y";
        }
        String p1TempNameFinal = null;
        String p2TempNameFinal = null;
        String p3TempNameFinal = null;
        // String p4TempNameFinal = null;
        String p5TempNameFinal = null;
        // P1
        p1TempNameFinal = p1TempName + tempNameKey + ".ftl";
        // P2
        p2TempNameFinal = p2TempName + ".xml";
        // P3
        p3TempNameFinal = repType.equals("年审报告")? p3TempName + tempNameKey + ".ftl" : p3TempName + tempNameKey + "_Mid.ftl"; // 20200507,yury,新增年报或中期的判断，中期报告的P3有单独的模板
        // P4
        // 如果选择此种规则，则按照基金区分模板
        // p4TempNameFinal = p4TempName + ".xml";
        // P5
        p5TempNameFinal = p5TempName + tempNameKey + ".ftl";
        // 归档
        Map<String, Object> partName = new HashMap<>();
        partName.put("P1", p1TempNameFinal);
        partName.put("P2", p2TempNameFinal);
        partName.put("P3", p3TempNameFinal);
        // partName.put("P4", p4TempNameFinal);
        partName.put("P5", p5TempNameFinal);
        // ↑-------获取报告模板地址-------↑//

        // 整理exportParam
        exportParam.put("dateInfo", dateMap);
        exportParam.put("lastDateInfo", dateMapLast);
        exportParam.put("PEROID", period);
        exportParam.put("FIRM_CODE", pfund.getString("FIRM_CODE"));
        exportParam.put("FUND_ID", pfund.getString("FUND_ID"));
        exportParam.put("partName", partName);
        exportParam.put("reportTempRootPath", reportTempRootPath);
        exportParam.put("REPTYPE", repType.toString()); // 20200507,yury
        String templatePath = Constants.ExportPathEnum.getExportPath(pd.getString("WP_TYPE_C"));
        // 开始导出
        this.reportExportService.doExport(request, response, exportParam, templatePath);
    }

}
