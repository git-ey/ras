package com.ey.service.wp.output.impl;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.ey.dao.DaoSupport;
import com.ey.service.wp.output.ReportExportManager;
import com.ey.util.DocUtil;
import com.ey.util.StringUtil;
import com.ey.util.fileexport.Constants;
import com.ey.util.fileexport.FileExportUtils;
import com.ey.util.fileexport.FreeMarkerUtils;
import com.google.common.collect.Lists;

/**
 * @name GExportService
 * @description 底稿输出服务--Report
 * @author andy.chen 2017年9月7日
 */
@Service("reportExportService")
public class ReportExportService implements ReportExportManager {
    /**
     * dao
     */
    @Resource(name = "daoSupport")
    protected DaoSupport dao;
    private static final String MOTHER_LEVEL = "母基金";
    private static final String A_SMALL_LAVEL = "0";
    /**
     * <p>基金level比较器</p>
     * <p>优化了母基金的排序</p>
     */
    private static final Comparator<String> LEVEL_COMPARATOR = (level1, level2) -> {
        String a,b;
        if(Objects.equals(MOTHER_LEVEL, level1)) {
            a = A_SMALL_LAVEL;
        }else {
            a = level1;
        }
        if(Objects.equals(MOTHER_LEVEL, level2)) {
            b = A_SMALL_LAVEL;
        }else {
            b = level2;
        }
        return a.compareTo(b);
    };
    
    /**
     * 根据基金ID获取基金信息
     * @author Dai Zong 2017年11月8日
     * 
     * @param fundId 基金ID
     * @return 基金信息
     * @throws Exception 基金ID无效
     */
    protected Map<String,String> selectFundInfo(String fundId) throws Exception{
        Map<String, Object> query = new HashMap<String,Object>();
        query.put("fundId", fundId);
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> resMapList = (List<Map<String,Object>>)this.dao.findForList("FundMapper.selectFundInfo", query);
        if(CollectionUtils.isEmpty(resMapList) || resMapList.size() != 1) {
            throw new Exception("基金ID " + fundId + " 无效");
        }
        Map<String, String> res = new HashMap<String,String>();
        resMapList.get(0).forEach((k,v) -> {
            res.put(k, String.valueOf(v));
        });
        return res;
    }

    /**
     * 生成文件内容
     * @author Dai Zong 2017年10月17日
     * 
     * @param fundId
     * @param periodStr
     * @param fundInfo
     * @return
     * @throws Exception
     */
    private String generateFileContent(Map<String,Object> exportParam, Map<String, String> fundInfo) throws Exception {
        Map<String, Object> dataMap = new HashMap<String, Object>();
        Map<String, Object> content = new HashMap<String, Object>();
        
        String fundId = String.valueOf(exportParam.get("FUND_ID"));
        String periodStr = String.valueOf(exportParam.get("PEROID"));
        Long period = Long.parseLong(periodStr.substring(0, 4));
        Long month = Long.parseLong(periodStr.substring(4, 6));
        Long day = Long.parseLong(periodStr.substring(6, 8));
        
        exportParam.put("period", period);
        exportParam.put("month", month);
        exportParam.put("day", day);
        exportParam.put("fundId", fundId);
        exportParam.put("fundInfo", fundInfo);

        Map<String, Object> queryMap = this.createBaseQueryMap(fundId, periodStr);
        @SuppressWarnings("unchecked")
        Map<String,Object> reportExtendInfo = (Map<String,Object>)this.dao.findForObject("ReportMapper.selectReportExtendInfo", queryMap);
        if(reportExtendInfo == null) {
            reportExtendInfo = new HashMap<>(); 
        }
        @SuppressWarnings("unchecked")
        Map<String,Object> fundControl = (Map<String,Object>)this.dao.findForObject("ReportMapper.selectFundControlData", queryMap);
        if(fundControl == null) {
            fundControl = new HashMap<>(); 
        }
        @SuppressWarnings("unchecked")
        Map<String,Object> reportContent = (Map<String,Object>)this.dao.findForObject("ReportMapper.selectReportContentData", queryMap);
        if(reportContent == null) {
            reportContent = new HashMap<>(); 
        }
        
        exportParam.put("reportExtendInfo", reportExtendInfo);
        exportParam.put("fundControl", fundControl);
        exportParam.put("reportContent", reportContent);
        exportParam.put("extraFundInfo", this.getExtraFundInfo(fundId, periodStr));
        
        
        exportParam.forEach((k,v) -> {
            dataMap.put(k, v);
        });
        this.processParts(exportParam, content, queryMap);
        dataMap.put("content", content);

        return FreeMarkerUtils.processTemplateToString(dataMap, Constants.EXPORT_TEMPLATE_FOLDER_PATH, Constants.EXPORT_TEMPLATE_FILE_NAME_REPORT);
    }
    
    @Override
    public boolean doExport(HttpServletRequest request, HttpServletResponse response, Map<String,Object> exportParam) throws Exception {
        String fundId = String.valueOf(exportParam.get("FUND_ID"));
        String periodStr = String.valueOf(exportParam.get("PEROID"));
        Map<String, String> fundInfo = this.selectFundInfo(fundId);
        fundInfo.put("periodStr", periodStr);
        String fileStr = this.generateFileContent(exportParam, fundInfo);
        FileExportUtils.writeFileToHttpResponse(request, response, FreeMarkerUtils.simpleReplace(Constants.EXPORT_AIM_FILE_NAME_REPORT, fundInfo), fileStr);
        return true;
    }

    @Override
    public boolean doExport(String folederName, String fileName, Map<String,Object> exportParam) throws Exception {
        String fundId = String.valueOf(exportParam.get("FUND_ID"));
        String periodStr = String.valueOf(exportParam.get("PEROID"));
        Map<String, String> fundInfo = this.selectFundInfo(fundId);
        fundInfo.put("periodStr", periodStr);
        String fileStr = this.generateFileContent(exportParam, fundInfo);
        FileExportUtils.writeFileToDisk(folederName, FreeMarkerUtils.simpleReplace(fileName, fundInfo), fileStr);
        return true;
    }
    
    /**
     * 处理报告各个组件
     * @author Dai Zong 2017年12月20日
     * 
     * @param exportParam
     * @param content
     * @param queryParam
     * @throws Exception
     */
    private void processParts(Map<String, Object> exportParam, Map<String,Object> content, Map<String,Object> queryParam) throws Exception{
        @SuppressWarnings("unchecked")
        Map<String,Object> partName = (Map<String,Object>)exportParam.get("partName");
        content.put("P1", this.processP1(exportParam, partName, queryParam));
        content.put("P2", this.processP2(exportParam, partName));
        content.put("P3", this.processP3(exportParam, partName, queryParam));
        content.put("P4", this.processP4(exportParam, partName));
        content.put("P5", this.processP5(exportParam, partName, queryParam));
    }
    
    /**
     * 处理Part1
     * @author Dai Zong 2017年12月20日
     * 
     * @param exportParam
     * @param partName
     * @param queryParam
     * @return
     * @throws Exception
     */
    private String processP1(Map<String,Object> exportParam, Map<String,Object> partName, Map<String,Object> queryParam) throws Exception{
        Map<String,Object> P1 = new HashMap<>();
        //====================↓BS↓====================
        Map<String,Object> BS = new HashMap<>();
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> BsMetaDataList = (List<Map<String,Object>>)this.dao.findForList("ReportMapper.selectBsData", queryParam);
        if(BsMetaDataList == null) {
            BsMetaDataList = new ArrayList<>(); 
        }
        Map<String,Object> A01 = new HashMap<>();
        Map<String,Object> A02 = new HashMap<>();
        Map<String,Object> A03 = new HashMap<>();
        Map<String,Object> A04 = new HashMap<>();
        Map<String,Object> A05 = new HashMap<>();
        Map<String,Object> A06 = new HashMap<>();
        Map<String,Object> A07 = new HashMap<>();
        Map<String,Object> A08 = new HashMap<>();
        Map<String,Object> A09 = new HashMap<>();
        Map<String,Object> A10 = new HashMap<>();
        Map<String,Object> A11 = new HashMap<>();
        Map<String,Object> A12 = new HashMap<>();
        Map<String,Object> A13 = new HashMap<>();
        Map<String,Object> A14 = new HashMap<>();
        Map<String,Object> A15 = new HashMap<>();
        Map<String,Object> A16 = new HashMap<>();
        Map<String,Object> A17 = new HashMap<>();
        Map<String,Object> B01 = new HashMap<>();
        Map<String,Object> B02 = new HashMap<>();
        Map<String,Object> B03 = new HashMap<>();
        Map<String,Object> B04 = new HashMap<>();
        Map<String,Object> B05 = new HashMap<>();
        Map<String,Object> B06 = new HashMap<>();
        Map<String,Object> B07 = new HashMap<>();
        Map<String,Object> B08 = new HashMap<>();
        Map<String,Object> B09 = new HashMap<>();
        Map<String,Object> B10 = new HashMap<>();
        Map<String,Object> B11 = new HashMap<>();
        Map<String,Object> B12 = new HashMap<>();
        Map<String,Object> B13 = new HashMap<>();
        Map<String,Object> B14 = new HashMap<>();
        Map<String,Object> B15 = new HashMap<>();
        Map<String,Object> C01 = new HashMap<>();
        Map<String,Object> C02 = new HashMap<>();
        Map<String,Object> SUM1 = new HashMap<>();
        Map<String,Object> SUM2 = new HashMap<>();
        Map<String,Object> SUM3 = new HashMap<>();
        Map<String,Object> SUM4 = new HashMap<>();

        for(Map<String,Object> map : BsMetaDataList) {
            if ("A01".equals(map.get("bsCode"))) {
                A01 = map;
            } else if ("A02".equals(map.get("bsCode"))) {
                A02 = map;
            } else if ("A03".equals(map.get("bsCode"))) {
                A03 = map;
            } else if ("A04".equals(map.get("bsCode"))) {
                A04 = map;
            } else if ("A05".equals(map.get("bsCode"))) {
                A05 = map;
            } else if ("A06".equals(map.get("bsCode"))) {
                A06 = map;
            } else if ("A07".equals(map.get("bsCode"))) {
                A07 = map;
            } else if ("A08".equals(map.get("bsCode"))) {
                A08 = map;
            } else if ("A09".equals(map.get("bsCode"))) {
                A09 = map;
            } else if ("A10".equals(map.get("bsCode"))) {
                A10 = map;
            } else if ("A11".equals(map.get("bsCode"))) {
                A11 = map;
            } else if ("A12".equals(map.get("bsCode"))) {
                A12 = map;
            } else if ("A13".equals(map.get("bsCode"))) {
                A13 = map;
            } else if ("A14".equals(map.get("bsCode"))) {
                A14 = map;
            } else if ("A15".equals(map.get("bsCode"))) {
                A15 = map;
            } else if ("A17".equals(map.get("bsCode"))) {
                A17 = map;
            } else if ("B01".equals(map.get("bsCode"))) {
                B01 = map;
            } else if ("B02".equals(map.get("bsCode"))) {
                B02 = map;
            } else if ("B03".equals(map.get("bsCode"))) {
                B03 = map;
            } else if ("B04".equals(map.get("bsCode"))) {
                B04 = map;
            } else if ("B05".equals(map.get("bsCode"))) {
                B05 = map;
            } else if ("B06".equals(map.get("bsCode"))) {
                B06 = map;
            } else if ("B07".equals(map.get("bsCode"))) {
                B07 = map;
            } else if ("B08".equals(map.get("bsCode"))) {
                B08 = map;
            } else if ("B09".equals(map.get("bsCode"))) {
                B09 = map;
            } else if ("B10".equals(map.get("bsCode"))) {
                B10 = map;
            } else if ("B11".equals(map.get("bsCode"))) {
                B11 = map;
            } else if ("B12".equals(map.get("bsCode"))) {
                B12 = map;
            } else if ("B13".equals(map.get("bsCode"))) {
                B13 = map;
            } else if ("B15".equals(map.get("bsCode"))) {
                B15 = map;
            } else if ("C01".equals(map.get("bsCode"))) {
                C01 = map;
            } else if ("C02".equals(map.get("bsCode"))) {
                C02 = map;
            } else if ("A99".equals(map.get("bsCode"))) {
                SUM1 = map;
            } else if ("B99".equals(map.get("bsCode"))) {
                SUM2 = map;
            } else if ("C99".equals(map.get("bsCode"))) {
                SUM3 = map;
            } else if ("BCS".equals(map.get("bsCode"))) {
                SUM4 = map;
            }
        }
        BS.put("A01", A01);
        BS.put("A02", A02);
        BS.put("A03", A03);
        BS.put("A04", A04);
        BS.put("A05", A05);
        BS.put("A06", A06);
        BS.put("A07", A07);
        BS.put("A08", A08);
        BS.put("A09", A09);
        BS.put("A10", A10);
        BS.put("A11", A11);
        BS.put("A12", A12);
        BS.put("A13", A13);
        BS.put("A14", A14);
        BS.put("A15", A15);
        BS.put("A16", A16);
        BS.put("A17", A17);
        BS.put("B01", B01);
        BS.put("B02", B02);
        BS.put("B03", B03);
        BS.put("B04", B04);
        BS.put("B05", B05);
        BS.put("B06", B06);
        BS.put("B07", B07);
        BS.put("B08", B08);
        BS.put("B09", B09);
        BS.put("B10", B10);
        BS.put("B11", B11);
        BS.put("B12", B12);
        BS.put("B13", B13);
        BS.put("B14", B14);
        BS.put("B15", B15);
        BS.put("C01", C01);
        BS.put("C02", C02);
        BS.put("SUM1", SUM1);
        BS.put("SUM2", SUM2);
        BS.put("SUM3", SUM3);
        BS.put("SUM4", SUM4);
        //====================↑BS↑====================
        
        //====================↓PL↓====================
        Map<String,Object> PL = new HashMap<>();
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> PlMetaDataList = (List<Map<String,Object>>)this.dao.findForList("ReportMapper.selectPlData", queryParam);
        if(PlMetaDataList == null) {
            PlMetaDataList = new ArrayList<>(); 
        }
        Map<String,Object> D01 = new HashMap<>();
        Map<String,Object> D02 = new HashMap<>();
        Map<String,Object> D03 = new HashMap<>();
        Map<String,Object> D04 = new HashMap<>();
        Map<String,Object> D05 = new HashMap<>();
        Map<String,Object> D06 = new HashMap<>();
        Map<String,Object> D07 = new HashMap<>();
        Map<String,Object> D08 = new HashMap<>();
        Map<String,Object> D09 = new HashMap<>();
        Map<String,Object> D10 = new HashMap<>();
        Map<String,Object> D11 = new HashMap<>();
        Map<String,Object> D12 = new HashMap<>();
        Map<String,Object> D13 = new HashMap<>();
        Map<String,Object> D14 = new HashMap<>();
        Map<String,Object> D15 = new HashMap<>();
        Map<String,Object> D16 = new HashMap<>();
        Map<String,Object> D17 = new HashMap<>();
        Map<String,Object> E01 = new HashMap<>();
        Map<String,Object> E02 = new HashMap<>();
        Map<String,Object> E03 = new HashMap<>();
        Map<String,Object> E04 = new HashMap<>();
        Map<String,Object> E05 = new HashMap<>();
        Map<String,Object> E06 = new HashMap<>();
        Map<String,Object> E07 = new HashMap<>();
        SUM1 = new HashMap<>();
        SUM2 = new HashMap<>();
        SUM3 = new HashMap<>();
        
        for(Map<String,Object> map : PlMetaDataList) {
            if ("D01".equals(map.get("plCode"))) {
                D01 = map;
            } else if ("D02".equals(map.get("plCode"))) {
                D02 = map;
            } else if ("D03".equals(map.get("plCode"))) {
                D03 = map;
            } else if ("D04".equals(map.get("plCode"))) {
                D04 = map;
            } else if ("D05".equals(map.get("plCode"))) {
                D05 = map;
            } else if ("D06".equals(map.get("plCode"))) {
                D06 = map;
            } else if ("D07".equals(map.get("plCode"))) {
                D07 = map;
            } else if ("D08".equals(map.get("plCode"))) {
                D08 = map;
            } else if ("D09".equals(map.get("plCode"))) {
                D09 = map;
            } else if ("D10".equals(map.get("plCode"))) {
                D10 = map;
            } else if ("D11".equals(map.get("plCode"))) {
                D11 = map;
            } else if ("D12".equals(map.get("plCode"))) {
                D12 = map;
            } else if ("D13".equals(map.get("plCode"))) {
                D13 = map;
            } else if ("D14".equals(map.get("plCode"))) {
                D14 = map;
            } else if ("D15".equals(map.get("plCode"))) {
                D15 = map;
            } else if ("D16".equals(map.get("plCode"))) {
                D16 = map;
            } else if ("D17".equals(map.get("plCode"))) {
                D17 = map;
            } else if ("E01".equals(map.get("plCode"))) {
                E01 = map;
            } else if ("E02".equals(map.get("plCode"))) {
                E02 = map;
            } else if ("E03".equals(map.get("plCode"))) {
                E03 = map;
            } else if ("E04".equals(map.get("plCode"))) {
                E04 = map;
            } else if ("E05".equals(map.get("plCode"))) {
                E05 = map;
            } else if ("E06".equals(map.get("plCode"))) {
                E06 = map;
            } else if ("E07".equals(map.get("plCode"))) {
                E07 = map;
            } else if ("D99".equals(map.get("plCode"))) {
                SUM1 = map;
            } else if ("E99".equals(map.get("plCode"))) {
                SUM2 = map;
            } else if ("PLN".equals(map.get("plCode"))) {
                SUM3 = map;
            }
        }
        
        PL.put("D01", D01);
        PL.put("D02", D02);
        PL.put("D03", D03);
        PL.put("D04", D04);
        PL.put("D05", D05);
        PL.put("D06", D06);
        PL.put("D07", D07);
        PL.put("D08", D08);
        PL.put("D09", D09);
        PL.put("D10", D10);
        PL.put("D11", D11);
        PL.put("D12", D12);
        PL.put("D13", D13);
        PL.put("D14", D14);
        PL.put("D15", D15);
        PL.put("D16", D16);
        PL.put("D17", D17);
        PL.put("D18", E01);
        PL.put("D19", E02);
        PL.put("D20", E03);
        PL.put("D21", E04);
        PL.put("D22", E05);
        PL.put("D23", E06);
        PL.put("D24", E07);
        PL.put("SUM1", SUM1);
        PL.put("SUM2", SUM2);
        PL.put("SUM3", SUM3);

        //====================↑PL↑====================
        
        //====================↓T500↓====================
        Map<String,Object> T500 = new HashMap<>();
        String periodStr = String.valueOf(queryParam.get("period"));
        String oldPeriodStr = StringUtils.EMPTY;
        try {
            oldPeriodStr = String.valueOf(Long.parseLong(periodStr.substring(0, 4))-1)+"1231";
        }catch (Exception e) {
            oldPeriodStr = String.valueOf(Calendar.getInstance().get(Calendar.YEAR)-1)+"1231";
        }
        Map<String, Object> oldQueryParam = this.createBaseQueryMap(String.valueOf(queryParam.get("fundId")), oldPeriodStr);
        
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> mainMetaDataList = (List<Map<String,Object>>)this.dao.findForList("TExportMapper.selectT500MainData", queryParam);
        if(mainMetaDataList == null) {
            mainMetaDataList = new ArrayList<>();
        }
        Map<String,Map<String,Object>> mainContainer = new HashMap<>();
        for(Map<String,Object> map : mainMetaDataList) {
            mainContainer.put(String.valueOf(map.get("type")), map);
        }
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> mainOldMetaDataList = (List<Map<String,Object>>)this.dao.findForList("TExportMapper.selectT500MainData", oldQueryParam);
        if(mainOldMetaDataList == null) {
            mainOldMetaDataList = new ArrayList<>();
        }
        Map<String,Map<String,Object>> mainOldContainer = new HashMap<>();
        for(Map<String,Object> map : mainOldMetaDataList) {
            mainOldContainer.put(String.valueOf(map.get("type")), map);
        }
        for(int i=1 ; i<=7 ; i++) {
            String tag = "attr" + i;
            Map<String,Object> temp = new HashMap<>();
            if(mainContainer.get("实收基金") != null) {
                temp.put("SS", mainContainer.get("实收基金").get(tag));
            }
            if(mainContainer.get("利润分配") != null) {
                temp.put("WFP", mainContainer.get("利润分配").get(tag));
            }
            if(mainContainer.get("所有者权益合计") != null) {
                temp.put("SYZ", mainContainer.get("所有者权益合计").get(tag));
            }
            if(mainOldContainer.get("实收基金") != null) {
                temp.put("SSOLD", mainOldContainer.get("实收基金").get(tag));
            }
            if(mainOldContainer.get("利润分配") != null) {
                temp.put("WFPOLD", mainOldContainer.get("利润分配").get(tag));
            }
            if(mainOldContainer.get("所有者权益合计") != null) {
                temp.put("SYZOLD", mainOldContainer.get("所有者权益合计").get(tag));
            }
            T500.put(tag, temp);
        }
        //====================↑T500↑====================
        
        P1.put("BS", BS);
        P1.put("PL", PL);
        P1.put("T500", T500);
        exportParam.put("P1", P1);
        return FreeMarkerUtils.processTemplateToStrUseAbsPath(exportParam, String.valueOf(exportParam.get("reportTempRootPath")), String.valueOf(partName.get("P1")));
    }
    
    /**
     * 处理Part2
     * @author Dai Zong 2017年12月20日
     * 
     * @param exportParam
     * @param partName
     * @return
     * @throws IOException
     */
    private String processP2(Map<String,Object> exportParam, Map<String,Object> partName) throws IOException{
        String xml2003Content = DocUtil.getXml2003Content(String.valueOf(exportParam.get("reportTempRootPath")) + String.valueOf(partName.get("P2")), "<w:body><wx:sect><wx:sub-section>(.*)</wx:sub-section>", 1);
        if(StringUtils.isEmpty(xml2003Content)) {
            throw new IOException("Can not get content from P2 template");
        }
        return xml2003Content;
    }
    
    /**
     * 处理Part3
     * @author Dai Zong 2017年12月22日
     * 
     * @param exportParam
     * @param partName
     * @param queryParam
     * @return
     * @throws Exception 
     */
    private String processP3(Map<String,Object> exportParam, Map<String,Object> partName, Map<String,Object> queryParam) throws Exception{
        Map<String,Object> P3 = new HashMap<>();
        
        P3.put("sec1", this.processP3Sec1(queryParam));
        P3.put("sec2", this.processP3Sec2(queryParam));
        P3.put("sec3", this.processP3Sec3(queryParam));
        P3.put("sec4", this.processP3Sec4(queryParam));
        P3.put("sec5", this.processP3Sec5(queryParam));
        P3.put("sec6", this.processP3Sec6(queryParam));
        
        exportParam.put("P3", P3);
        return FreeMarkerUtils.processTemplateToStrUseAbsPath(exportParam, String.valueOf(exportParam.get("reportTempRootPath")), String.valueOf(partName.get("P3")));
    }
    
    /**
     * 处理Part3 第一部分
     * @author Dai Zong 2017年12月23日
     * 
     * @param queryParam
     * @return
     * @throws Exception
     */
    private Map<String,Object> processP3Sec1(Map<String,Object> queryParam) throws Exception{
        Map<String,Object> result = new HashMap<>();
        String FundId = String.valueOf(queryParam.get("fundId"));
        String Period = String.valueOf(queryParam.get("period"));
        String PeriodLast = (Integer.parseInt(Period.substring(0, 4)) - 1) + "1231";
        Map<String, Object> queryParamLast = this.createBaseQueryMap(FundId, PeriodLast);
        //====================↓C10000↓====================
        Map<String,Object> C10000 = new HashMap<>();
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> C10000MetaDataList = (List<Map<String,Object>>)this.dao.findForList("CExportMapper.selectC10000DataForReport", queryParam);
        if(C10000MetaDataList == null) {
            C10000MetaDataList = new ArrayList<>(); 
        }
        @SuppressWarnings("unchecked")
        Map<String,Object> C10000SumMetaData = (Map<String,Object>)this.dao.findForObject("CExportMapper.selectC10000SumDataForReport", queryParam);
        if(C10000SumMetaData == null) {
            C10000SumMetaData = new HashMap<>();
        }
        C10000MetaDataList.add(C10000SumMetaData);
        C10000.put("list", C10000MetaDataList);
        C10000.put("count", C10000MetaDataList.size());
        //====================↑C10000↑====================
        
        //====================↓H10000↓====================
        Map<String,Object> H10000 = new HashMap<>();
        //--------------------↓H10000.tfa↓--------------------
        Map<String,Object> tfa = new HashMap<>();
        
        Map<String,Object> item1 = new HashMap<>();
        Map<String,Object> item2 = new HashMap<>();
        Map<String,Object> item3 = new HashMap<>();
        Map<String,Object> item4 = new HashMap<>();
        Map<String,Object> item5 = new HashMap<>();
        Map<String,Object> item6 = new HashMap<>();
        Map<String,Object> item7 = new HashMap<>();
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> H10000BondDataList = (List<Map<String,Object>>)this.dao.findForList("HExportMapper.selectH10000TFAData", queryParam);
        if(H10000BondDataList == null) {
            H10000BondDataList = new ArrayList<>(); 
        }
        for(Map<String,Object> map : H10000BondDataList) {
            if("交易所".equals(map.get("subItem"))) {
                item3 = map;
            }else if("银行间".equals(map.get("subItem"))){
                item4 = map;
            }
        }
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> H10000NotBondDataList = (List<Map<String,Object>>)this.dao.findForList("HExportMapper.selectH10000TFANotBondDataForReport", queryParam);
        if(H10000NotBondDataList == null) {
            H10000NotBondDataList = new ArrayList<>(); 
        }
        @SuppressWarnings("unchecked")
        Map<String,Object> H10000BondSumData = (Map<String,Object>)this.dao.findForObject("HExportMapper.selectH10000TFABondSumDataForReport", queryParam);
        if(H10000BondSumData == null) {
            H10000BondSumData = new HashMap<>();
        }
        @SuppressWarnings("unchecked")
        Map<String,Object> H10000SumData = (Map<String,Object>)this.dao.findForObject("HExportMapper.selectH10000TFASumDataForReport", queryParam);
        if(H10000SumData == null) {
            H10000SumData = new HashMap<>();
        }
        for(Map<String,Object> map : H10000NotBondDataList) {
            if("股票".equals(map.get("item"))) {
                item1 = map;
            }else if("贵金属投资-金交所黄金合约".equals(map.get("item"))){
                item2 = map;
            }else if("资产支持性证券投资".equals(map.get("item"))){
                item5 = map;
            }else if("基金投资".equals(map.get("item"))){
                item6 = map;
            }else if("其他".equals(map.get("item"))){
                item7 = map;
            }
        }
        tfa.put("item1", item1);
        tfa.put("item2", item2);
        tfa.put("item3", item3);
        tfa.put("item4", item4);
        tfa.put("item5", item5);
        tfa.put("item6", item6);
        tfa.put("item7", item7);
        tfa.put("sumBond", H10000BondSumData);
        tfa.put("sum", H10000SumData);
        tfa.put("bondCount", H10000BondDataList.size());
        tfa.put("otherCount", H10000NotBondDataList.size());
        Object dataSumCheck = this.dao.findForObject("HExportMapper.checkIfH10000TFAHasDataForReport", queryParam);
        tfa.put("dataSumCheck", dataSumCheck == null ? 0d : dataSumCheck);
        
        tfa.put("tfa", tfa);
        H10000.put("tfa", tfa);
        //--------------------↑H10000.tfa↑--------------------
        //--------------------↓H10000.derivative↓--------------------
        Map<String,Object> derivative = new HashMap<>();
        
        item1 = new HashMap<>();
        item2 = new HashMap<>();
        item3 = new HashMap<>();
        item4 = new HashMap<>();
        
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> H10000DerivativeDataList = (List<Map<String,Object>>)this.dao.findForList("HExportMapper.selectH10000DerivativeData", queryParam);
        if(H10000DerivativeDataList == null) {
            H10000DerivativeDataList = new ArrayList<>(); 
        }
        for(Map<String,Object> map : H10000DerivativeDataList) {
            if("利率衍生工具".equals(map.get("item"))) {
                item1 = map;
            }else if("货币衍生工具".equals(map.get("item"))){
                item2 = map;
            }else if("权益衍生工具".equals(map.get("item"))){
                item3 = map;
            }else if("其他衍生工具".equals(map.get("item"))){
                item4 = map;
            }
        }
        @SuppressWarnings("unchecked")
        Map<String,Object> H10000DerivativeSumData = (Map<String,Object>)this.dao.findForObject("HExportMapper.selectH10000DerivativeSumDataForReport", queryParam);
        if(H10000DerivativeSumData == null) {
            H10000DerivativeSumData = new HashMap<>(); 
        }
        derivative.put("item1", item1);
        derivative.put("item2", item2);
        derivative.put("item3", item3);
        derivative.put("item4", item4);
        derivative.put("sum", H10000DerivativeSumData);
        derivative.put("count", H10000DerivativeDataList.size());
        dataSumCheck = this.dao.findForObject("HExportMapper.checkIfH10000DerivativeHasDataForReport", queryParam);
        derivative.put("dataSumCheck", dataSumCheck == null ? 0d : dataSumCheck);
        H10000.put("derivative", derivative);
        //--------------------↑H10000.derivative↑--------------------
        //--------------------↓H10000.futures↓--------------------
        Map<String,Object> futures = new HashMap<>();
        item1 = new HashMap<>();
        item2 = new HashMap<>();
        item3 = new HashMap<>();
        item4 = new HashMap<>();
        item5 = new HashMap<>();
        item6 = new HashMap<>();
        
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> H10000futuresDataList = (List<Map<String,Object>>)this.dao.findForList("HExportMapper.selectH10000FuturesData", queryParam);
        if(H10000futuresDataList == null) {
            H10000futuresDataList = new ArrayList<>(); 
        }
        for(Map<String,Object> map : H10000futuresDataList) {
            if("股指期货合约".equals(map.get("item"))) {
                item1 = map;
            }else if("国债期货合约".equals(map.get("item"))){
                item2 = map;
            }else if("黄金现货延期交收合约".equals(map.get("item"))){
                item3 = map;
            }
        }
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> H10000futuresLastDataList = (List<Map<String,Object>>)this.dao.findForList("HExportMapper.selectH10000FuturesData", queryParamLast);
        if(H10000futuresLastDataList == null) {
            H10000futuresLastDataList = new ArrayList<>(); 
        }
        for(Map<String,Object> map : H10000futuresLastDataList) {
            if("股指期货合约".equals(map.get("item"))) {
                item4 = map;
            }else if("国债期货合约".equals(map.get("item"))){
                item5 = map;
            }else if("黄金现货延期交收合约".equals(map.get("item"))){
                item6 = map;
            }
        }
        @SuppressWarnings("unchecked")
        Map<String,Object> H10000futuresSumData = (Map<String,Object>)this.dao.findForObject("HExportMapper.selectH10000FuturesSumDataForReport", queryParam);
        if(H10000futuresSumData == null) {
            H10000futuresSumData = new HashMap<>(); 
        }
        @SuppressWarnings("unchecked")
        Map<String,Object> H10000futuresSumLastData = (Map<String,Object>)this.dao.findForObject("HExportMapper.selectH10000FuturesSumDataForReport", queryParamLast);
        if(H10000futuresSumLastData == null) {
            H10000futuresSumLastData = new HashMap<>(); 
        }
        
        futures.put("item1", item1);
        futures.put("item2", item2);
        futures.put("item3", item3);
        futures.put("item1Last ", item4);
        futures.put("item2Last ", item5);
        futures.put("item3Last ", item6);
        futures.put("sum", H10000futuresSumData);
        futures.put("sumLast", H10000futuresSumLastData);
        H10000.put("futures", futures);
        //--------------------↑H10000.futures↑--------------------
        //--------------------↓H10000.rmcfs↓--------------------
        Map<String, Object> rmcfs = new HashMap<String,Object>();
        item1 = new HashMap<String,Object>();
        item2 = new HashMap<String,Object>();
        
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> rmcfsMetaDataList = (List<Map<String,Object>>)this.dao.findForList("HExportMapper.selectH10000RmcfsData", queryParam);
        if(rmcfsMetaDataList == null) {
            rmcfsMetaDataList = new ArrayList<>();
        }
        for(Map<String,Object> map : rmcfsMetaDataList) {
            if("交易所市场".equals(map.get("item"))) {
                item1 = map;
            }else if("银行间市场".equals(map.get("item"))) {
                item2 = map;
            }
        }
        @SuppressWarnings("unchecked")
        Map<String,Object> H10000rmcfsSumLastData = (Map<String,Object>)this.dao.findForObject("HExportMapper.selectH10000RmcfsSumDataForReport", queryParam);
        if(H10000rmcfsSumLastData == null) {
            H10000rmcfsSumLastData = new HashMap<>(); 
        }
        
        rmcfs.put("item1", item1);
        rmcfs.put("item2", item2);
        rmcfs.put("sum", H10000rmcfsSumLastData);
        rmcfs.put("count", rmcfsMetaDataList.size());
        dataSumCheck = this.dao.findForObject("HExportMapper.checkIfH10000RmcfsHasDataForReport", queryParam);
        rmcfs.put("dataSumCheck", dataSumCheck == null ? 0d : dataSumCheck);
        H10000.put("rmcfs", rmcfs);
        //--------------------↑H10000.rmcfs↑--------------------
        //--------------------↓H10000.fairValues↓--------------------
        Map<String, Object> fairValues = new HashMap<String,Object>();
        
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> H10000FairValuesDataList = (List<Map<String,Object>>)this.dao.findForList("HExportMapper.selectH10000FairValuesData", queryParam);
        if(H10000FairValuesDataList == null) {
            H10000FairValuesDataList = new ArrayList<>();
        }
        @SuppressWarnings("unchecked")
        Map<String,Object> H10000FairValuesSumData = (Map<String,Object>)this.dao.findForObject("HExportMapper.selectH10000FairValuesSumDataForReport", queryParam);
        if(H10000FairValuesSumData == null) {
            H10000FairValuesSumData = new HashMap<>(); 
        }
        
        fairValues.put("list", H10000FairValuesDataList);
        fairValues.put("count", H10000FairValuesDataList.size());
        fairValues.put("sum", H10000FairValuesSumData);
        dataSumCheck = this.dao.findForObject("HExportMapper.checkIfH10000FairValuesHasDataForReport", queryParam);
        fairValues.put("dataSumCheck", dataSumCheck == null ? 0d : dataSumCheck);
        H10000.put("fairValues", fairValues);
        //--------------------↑H10000.fairValues↑--------------------
        //====================↑H10000↑====================
        
        //====================↓H800↓====================
        Map<String,Object> H800 = new HashMap<>();
        //--------------------↓H800.intestDetail↓--------------------
        Map<String,Object> intestDetail = new HashMap<>();
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> H800intestDetailCurrentDataList = (List<Map<String,Object>>)this.dao.findForList("HExportMapper.selectH800InterestDetailDataForReport", queryParam);
        if(H800intestDetailCurrentDataList == null) {
            H800intestDetailCurrentDataList = new ArrayList<>();
        }
        @SuppressWarnings("unchecked")
        Map<String,Object> H800intestDetailCurrentSumData = (Map<String,Object>)this.dao.findForObject("HExportMapper.selectH800InterestDetailSumDataForReport", queryParam);
        if(H800intestDetailCurrentSumData == null) {
            H800intestDetailCurrentSumData = new HashMap<>(); 
        }
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> H800intestDetailLastDataList = (List<Map<String,Object>>)this.dao.findForList("HExportMapper.selectH800InterestDetailDataForReport", queryParamLast);
        if(H800intestDetailLastDataList == null) {
            H800intestDetailLastDataList = new ArrayList<>();
        }
        @SuppressWarnings("unchecked")
        Map<String,Object> H800intestDetailLastSumData = (Map<String,Object>)this.dao.findForObject("HExportMapper.selectH800InterestDetailSumDataForReport", queryParamLast);
        if(H800intestDetailLastSumData == null) {
            H800intestDetailLastSumData = new HashMap<>(); 
        }
        
        intestDetail.put("listCurrent", H800intestDetailCurrentDataList);
        intestDetail.put("sumCurrent", H800intestDetailCurrentSumData);
        intestDetail.put("countCurrent", H800intestDetailCurrentDataList.size());
        intestDetail.put("listLast", H800intestDetailLastDataList);
        intestDetail.put("sumLast", H800intestDetailLastSumData);
        intestDetail.put("countLast", H800intestDetailLastDataList.size());
        H800.put("intestDetail", intestDetail);
        //--------------------↑H800.intestDetail↑--------------------
        //====================↑H800↑====================
        
        //====================↓E300↓====================
        Map<String, Object> E300 = new HashMap<>();
        //--------------------↓E300.disc↓--------------------
        Map<String, Object> disc = new HashMap<String,Object>();
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> E300DiscDataList = (List<Map<String,Object>>)this.dao.findForList("EExportMapper.selectE300DiscData", queryParam);
        if(E300DiscDataList == null) {
            E300DiscDataList = new ArrayList<Map<String,Object>>(); 
        }
        @SuppressWarnings("unchecked")
        Map<String,Object> E300DiscSumData = (Map<String,Object>)this.dao.findForObject("EExportMapper.selectE300DiscSumDataForReport", queryParam);
        if(E300DiscSumData == null) {
            E300DiscSumData = new HashMap<>(); 
        }
        disc.put("list", E300DiscDataList);
        disc.put("count", E300DiscDataList.size());
        disc.put("sum", E300DiscSumData);
        dataSumCheck = this.dao.findForObject("EExportMapper.checkIfE300DiscHasDataForReport", queryParam);
        disc.put("dataSumCheck", dataSumCheck == null ? 0d : dataSumCheck);
        //--------------------↑E300.disc↑--------------------
        E300.put("disc", disc);
        //====================↑E300↑====================
        
        //====================↓G10000↓====================
        Map<String, Object> G10000 = new HashMap<>();
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> G10000DataList = (List<Map<String,Object>>)this.dao.findForList("GExportMapper.selectG10000Data", queryParam);
        if(G10000DataList == null) {
            G10000DataList = new ArrayList<>();
        }
        @SuppressWarnings("unchecked")
        Map<String,Object> G10000SumData = (Map<String,Object>)this.dao.findForObject("GExportMapper.selectG10000SUmDataForReport", queryParam);
        if(G10000SumData == null) {
            G10000SumData = new HashMap<>(); 
        }
        
        G10000.put("list", G10000DataList);
        G10000.put("count", G10000DataList.size());
        G10000.put("sum", G10000SumData);
        dataSumCheck = this.dao.findForObject("GExportMapper.checkIfG10000HasDataForReport", queryParam);
        G10000.put("dataSumCheck", dataSumCheck == null ? 0d : dataSumCheck);
        //====================↑G10000↑====================
        
        //====================↓N10000↓====================
        Map<String, Object> N10000 = new HashMap<>();
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> N10000DataList = (List<Map<String,Object>>)this.dao.findForList("NExportMapper.selectN10000Data", queryParam);
        if(N10000DataList == null) {
            N10000DataList = new ArrayList<>();
        }
        @SuppressWarnings("unchecked")
        Map<String,Object> N10000SumData = (Map<String,Object>)this.dao.findForObject("NExportMapper.selectN10000SumDataForReport", queryParam);
        if(N10000SumData == null) {
            N10000SumData = new HashMap<>(); 
        }
        
        N10000.put("list", N10000DataList);
        N10000.put("count", N10000DataList.size());
        N10000.put("sum", N10000SumData);
        dataSumCheck = this.dao.findForObject("NExportMapper.checkIfN10000HasDataForReport", queryParam);
        N10000.put("dataSumCheck", dataSumCheck == null ? 0d : dataSumCheck);
        //====================↑N10000↑====================
        
        //====================↓P10000↓====================
        Map<String, Object> P10000 = new HashMap<>();
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> P10000DataList = (List<Map<String,Object>>)this.dao.findForList("PExportMapper.selectP10000Data", queryParam);
        if(P10000DataList == null) {
            P10000DataList = new ArrayList<>();
        }
        @SuppressWarnings("unchecked")
        Map<String,Object> P10000SumData = (Map<String,Object>)this.dao.findForObject("PExportMapper.selectP10000SumDataForReport", queryParam);
        if(P10000SumData == null) {
            P10000SumData = new HashMap<>(); 
        }
        
        P10000.put("list", P10000DataList);
        P10000.put("count", P10000DataList.size());
        P10000.put("sum", P10000SumData);
        dataSumCheck = this.dao.findForObject("PExportMapper.checkIfP10000HasDataForReport", queryParam);
        P10000.put("dataSumCheck", dataSumCheck == null ? 0d : dataSumCheck);
        //====================↑G10000↑====================
        
        //====================↓T10000↓====================
        Map<String, Object> T10000 = new HashMap<>();
        
        List<Map<String, Object>> levels1 = new ArrayList<>();
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> T10000DataList = (List<Map<String,Object>>)this.dao.findForList("TExportMapper.selectT10000DataForReport", queryParam);
        if(T10000DataList == null) {
            T10000DataList = new ArrayList<>();
        }
        List<String> levelNames1 = T10000DataList.stream().map(item -> {
            return String.valueOf(item.get("level"));
        }).distinct().sorted(LEVEL_COMPARATOR).collect(Collectors.toList());
        Map<String, List<Map<String, Object>>> groups1 = T10000DataList.stream().collect(Collectors.groupingBy(item -> {
            Map<String,Object> map = (Map<String,Object>)item;
            return String.valueOf(map.get("level"));
        }));
        for(String levelName : levelNames1) {
            Map<String,Object> level = new HashMap<>();
            level.put("levelName", levelName);
            List<Map<String, Object>> levelDatas = groups1.get(levelName);
            if(levelDatas == null) {
                levelDatas = new ArrayList<>();
            }
            level.put("list", levelDatas);
            level.put("count", levelDatas.size());
            Map<String,Object> levelQueryMap = new HashMap<>();
            levelQueryMap.put("fundId", queryParam.get("fundId"));
            levelQueryMap.put("level", levelName);
            level.put("levelFullName", this.dao.findForObject("FundStructuredMapper.selectLevelNameData", levelQueryMap));
            levels1.add(level);
        }
        T10000.put("levels", levels1);
        T10000.put("levelCount", levels1.size());
        //====================↑T10000↑====================
        
        //====================↓T11000↓====================
        Map<String, Object> T11000 = new HashMap<>();
        //--------------------↓T11000.P4104↓--------------------
        Map<String, Object> P4104 = new HashMap<>();
        List<Map<String, Object>> levels = new ArrayList<>();
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> p4104MetaDataList = (List<Map<String,Object>>)this.dao.findForList("TExportMapper.selectT11000P4104Data", queryParam);
        if(p4104MetaDataList == null) {
            p4104MetaDataList = new ArrayList<>();
        }
        List<String> levelNames = p4104MetaDataList.stream().map(item -> {
            return String.valueOf(item.get("level"));
        }).distinct().sorted(LEVEL_COMPARATOR).collect(Collectors.toList());
        Map<String, Map<String, List<Map<String, Object>>>> groups = p4104MetaDataList.stream().collect(Collectors.groupingBy(item -> {
            Map<String,Object> map = (Map<String,Object>)item;
            return String.valueOf(map.get("level"));
        }, Collectors.groupingBy(item -> {
            Map<String,Object> map = (Map<String,Object>)item;
            return String.valueOf(map.get("type"));
        })));
        for(String levelName : levelNames) {
            Map<String, List<Map<String, Object>>> innerMap = groups.get(levelName);
            Map<String,Object> level = new HashMap<>();
            level.put("levelName",  levelName);
            Map<String,Object> levelQueryMap = new HashMap<>();
            levelQueryMap.put("fundId", queryParam.get("fundId"));
            levelQueryMap.put("level", levelName);
            level.put("levelFullName", this.dao.findForObject("FundStructuredMapper.selectLevelNameData", levelQueryMap));
            if(innerMap == null) {
                innerMap = new HashMap<>();
            }
            for(int i=1 ; i<=7 ; i++) {
                Map<String,Object> temp = new HashMap<>();
                String tag = "attr" + i;
                temp.put("realized", CollectionUtils.isEmpty(innerMap.get("已实现"))?null:innerMap.get("已实现").get(0).get(tag));
                temp.put("unrealized", CollectionUtils.isEmpty(innerMap.get("未实现"))?null:innerMap.get("未实现").get(0).get(tag));
                temp.put("profitSum", CollectionUtils.isEmpty(innerMap.get("未分配利润合计"))?null:innerMap.get("未分配利润合计").get(0).get(tag));
                level.put(tag, temp);
            }
            levels.add(level);
        }
        
        P4104.put("levels", levels);
        P4104.put("levelCount", levels.size());
        //--------------------↑T11000.P4104↑--------------------
        T11000.put("P4104", P4104);
        //====================↑T11000↑====================
        
        //====================↓U10000↓====================
        Map<String, Object> U10000 = new HashMap<>();
        //--------------------↓U10000.interest↓--------------------
        Map<String, Object> interest = new HashMap<>();
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> U10000IntDataList = (List<Map<String,Object>>)this.dao.findForList("UExportMapper.selectU10000InterestData", queryParam);
        if(U10000IntDataList == null) {
            U10000IntDataList = new ArrayList<>();
        }
        @SuppressWarnings("unchecked")
        Map<String,Object> U10000IntSumData = (Map<String,Object>)this.dao.findForObject("UExportMapper.selectU10000InterestSumDataForReport", queryParam);
        if(U10000IntSumData == null) {
            U10000IntSumData = new HashMap<>(); 
        }
        
        interest.put("list", U10000IntDataList);
        interest.put("count", U10000IntDataList.size());
        interest.put("sum", U10000IntSumData);
        //--------------------↑U10000.interest↑--------------------
        //--------------------↓U10000.import↓--------------------
        Map<String, Object> importData = new HashMap<>();
        
        Map<String, Object> STOCKS_BS = new HashMap<>();
        Map<String, Object> STOCKS = new HashMap<>();
        Map<String, Object> STOCKS_R = new HashMap<>();
        Map<String, Object> STOCKS_P = new HashMap<>();
        Map<String, Object> FUND = new HashMap<>();
        Map<String, Object> BOND_BS = new HashMap<>();
        Map<String, Object> BOND = new HashMap<>();
        Map<String, Object> BOND_R = new HashMap<>();
        Map<String, Object> BOND_P = new HashMap<>();
        Map<String, Object> ABS = new HashMap<>();
        Map<String, Object> GOLD_BS = new HashMap<>();
        Map<String, Object> GOLD = new HashMap<>();
        Map<String, Object> GOLD_R = new HashMap<>();
        Map<String, Object> GOLD_P = new HashMap<>();
        Map<String, Object> DI_WARRANT = new HashMap<>();
        Map<String, Object> DI_OTHER = new HashMap<>();
        
        queryParam.put("reportFlag", "Y");
        queryParam.put("type", "STOCKS_BS");
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> U10000StocksBsDataList = (List<Map<String,Object>>)this.dao.findForList("UExportMapper.selectU10000ImportData", queryParam);
        if(U10000StocksBsDataList == null) {
            U10000StocksBsDataList = new ArrayList<>(); 
        }
        queryParam.put("type", "STOCKS");
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> U10000StocksSummaryDataList = (List<Map<String,Object>>)this.dao.findForList("UExportMapper.selectU10000ImportSummaryData", queryParam);
        if(U10000StocksSummaryDataList == null) {
            U10000StocksSummaryDataList = new ArrayList<>(); 
        }
        String[] pair;
        for(Map<String,Object> map : U10000StocksSummaryDataList) {
            pair = StringUtil.splitStringPair(String.valueOf(map.get("item")), "——", true);
            map.put("item_a", pair[0]);
            map.put("item_b", pair[1]);
        }
        if(U10000StocksSummaryDataList.size() != 0) {
            @SuppressWarnings("unchecked")
            Map<String,Object> U10000StocksSummarySumData = (Map<String,Object>)this.dao.findForObject("UExportMapper.selectU10000ImportSummarySumDataForReport", queryParam);
            if(U10000StocksSummarySumData == null) {
                U10000StocksSummarySumData = new HashMap<>();
            }
            U10000StocksSummarySumData.put("item_a", U10000StocksSummarySumData.get("item"));
            U10000StocksSummaryDataList.add(U10000StocksSummarySumData);
        }
        queryParam.put("type", "STOCKS_R");
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> U10000StocksRDataList = (List<Map<String,Object>>)this.dao.findForList("UExportMapper.selectU10000ImportData", queryParam);
        if(U10000StocksRDataList == null) {
            U10000StocksRDataList = new ArrayList<>(); 
        }
        queryParam.put("type", "STOCKS_P");
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> U10000StocksPDataList = (List<Map<String,Object>>)this.dao.findForList("UExportMapper.selectU10000ImportData", queryParam);
        if(U10000StocksPDataList == null) {
            U10000StocksPDataList = new ArrayList<>(); 
        }
        queryParam.put("type", "FUND");
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> U10000FUNDDataList = (List<Map<String,Object>>)this.dao.findForList("UExportMapper.selectU10000ImportData", queryParam);
        if(U10000FUNDDataList == null) {
            U10000FUNDDataList = new ArrayList<>(); 
        }
        queryParam.put("type", "BOND_BS");
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> U10000BondBsDataList = (List<Map<String,Object>>)this.dao.findForList("UExportMapper.selectU10000ImportData", queryParam);
        if(U10000BondBsDataList == null) {
            U10000BondBsDataList = new ArrayList<>(); 
        }
        queryParam.put("type", "BOND");
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> U10000BondSummaryDataList = (List<Map<String,Object>>)this.dao.findForList("UExportMapper.selectU10000ImportSummaryData", queryParam);
        if(U10000BondSummaryDataList == null) {
            U10000BondSummaryDataList = new ArrayList<>(); 
        }
        if(U10000BondSummaryDataList.size() != 0) {
            @SuppressWarnings("unchecked")
            Map<String,Object> U10000BondSummarySumData = (Map<String,Object>)this.dao.findForObject("UExportMapper.selectU10000ImportSummarySumDataForReport", queryParam);
            if(U10000BondSummarySumData == null) {
                U10000BondSummarySumData = new HashMap<>();
            }
            U10000BondSummaryDataList.add(U10000BondSummarySumData);
        }
        queryParam.put("type", "BOND_R");
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> U10000BondRDataList = (List<Map<String,Object>>)this.dao.findForList("UExportMapper.selectU10000ImportData", queryParam);
        if(U10000BondRDataList == null) {
            U10000BondRDataList = new ArrayList<>(); 
        }
        queryParam.put("type", "BOND_P");
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> U10000BondPDataList = (List<Map<String,Object>>)this.dao.findForList("UExportMapper.selectU10000ImportData", queryParam);
        if(U10000BondPDataList == null) {
            U10000BondPDataList = new ArrayList<>(); 
        }
        queryParam.put("type", "ABS");
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> U10000ABSDataList = (List<Map<String,Object>>)this.dao.findForList("UExportMapper.selectU10000ImportData", queryParam);
        if(U10000ABSDataList == null) {
            U10000ABSDataList = new ArrayList<>(); 
        }
        queryParam.put("type", "GOLD_BS");
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> U10000GoldBsDataList = (List<Map<String,Object>>)this.dao.findForList("UExportMapper.selectU10000ImportData", queryParam);
        if(U10000GoldBsDataList == null) {
            U10000GoldBsDataList = new ArrayList<>(); 
        }
        queryParam.put("type", "GOLD");
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> U10000GoldSummaryDataList = (List<Map<String,Object>>)this.dao.findForList("UExportMapper.selectU10000ImportSummaryData", queryParam);
        if(U10000GoldSummaryDataList == null) {
            U10000GoldSummaryDataList = new ArrayList<>(); 
        }
        if(U10000GoldSummaryDataList.size() != 0) {
            @SuppressWarnings("unchecked")
            Map<String,Object> U10000GoldSummarySumData = (Map<String,Object>)this.dao.findForObject("UExportMapper.selectU10000ImportSummarySumDataForReport", queryParam);
            if(U10000GoldSummarySumData == null) {
                U10000GoldSummarySumData = new HashMap<>();
            }
            U10000GoldSummaryDataList.add(U10000GoldSummarySumData);
        }
        queryParam.put("type", "GOLD_R");
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> U10000GoldRDataList = (List<Map<String,Object>>)this.dao.findForList("UExportMapper.selectU10000ImportData", queryParam);
        if(U10000GoldRDataList == null) {
            U10000GoldRDataList = new ArrayList<>(); 
        }
        queryParam.put("type", "GOLD_P");
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> U10000GoldPDataList = (List<Map<String,Object>>)this.dao.findForList("UExportMapper.selectU10000ImportData", queryParam);
        if(U10000GoldPDataList == null) {
            U10000GoldPDataList = new ArrayList<>(); 
        }
        queryParam.put("type", "DI_WARRANT");
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> U10000diWarrantDataList = (List<Map<String,Object>>)this.dao.findForList("UExportMapper.selectU10000ImportData", queryParam);
        if(U10000diWarrantDataList == null) {
            U10000diWarrantDataList = new ArrayList<>(); 
        }
        queryParam.put("type", "DI_OTHER");
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> U10000diOtherDataList = (List<Map<String,Object>>)this.dao.findForList("UExportMapper.selectU10000ImportData", queryParam);
        if(U10000diOtherDataList == null) {
            U10000diOtherDataList = new ArrayList<>(); 
        }
        queryParam.remove("type");
        queryParam.remove("reportFlag");
        
        STOCKS_BS.put("list", U10000StocksBsDataList);
        STOCKS_BS.put("count", U10000StocksBsDataList.size());
        
        STOCKS.put("list", U10000StocksSummaryDataList);
        STOCKS.put("count", U10000StocksSummaryDataList.size());
        
        STOCKS_R.put("list", U10000StocksRDataList);
        STOCKS_R.put("count", U10000StocksRDataList.size());
        
        STOCKS_P.put("list", U10000StocksPDataList);
        STOCKS_P.put("count", U10000StocksPDataList.size());
        
        FUND.put("list", U10000FUNDDataList);
        FUND.put("count", U10000FUNDDataList.size());
        
        BOND_BS.put("list", U10000BondBsDataList);
        BOND_BS.put("count", U10000BondBsDataList.size());
        
        BOND.put("list", U10000BondSummaryDataList);
        BOND.put("count", U10000BondSummaryDataList.size());
        
        BOND_R.put("list", U10000BondRDataList);
        BOND_R.put("count", U10000BondRDataList.size());
        
        BOND_P.put("list", U10000BondPDataList);
        BOND_P.put("count", U10000BondPDataList.size());
        
        ABS.put("list", U10000ABSDataList);
        ABS.put("count", U10000ABSDataList.size());
        
        GOLD_BS.put("list", U10000GoldBsDataList);
        GOLD_BS.put("count", U10000GoldBsDataList.size());
        
        GOLD.put("list", U10000GoldSummaryDataList);
        GOLD.put("count", U10000GoldSummaryDataList.size());
        
        GOLD_R.put("list", U10000GoldRDataList);
        GOLD_R.put("count", U10000GoldRDataList.size());
        
        GOLD_P.put("list", U10000GoldPDataList);
        GOLD_P.put("count", U10000GoldPDataList.size());
        
        DI_WARRANT.put("list", U10000diWarrantDataList);
        DI_WARRANT.put("count", U10000diWarrantDataList.size());
        
        DI_OTHER.put("list", U10000diOtherDataList);
        DI_OTHER.put("count", U10000diOtherDataList.size());
        
        importData.put("STOCKS_BS", STOCKS_BS);
        importData.put("STOCKS", STOCKS);
        importData.put("STOCKS_R", STOCKS_R);
        importData.put("STOCKS_P", STOCKS_P);
        importData.put("FUND", FUND);
        importData.put("BOND_BS", BOND_BS);
        importData.put("BOND", BOND);
        importData.put("BOND_R", BOND_R);
        importData.put("BOND_P", BOND_P);
        importData.put("ABS", ABS);
        importData.put("GOLD_BS", GOLD_BS);
        importData.put("GOLD", GOLD);
        importData.put("GOLD_R", GOLD_R);
        importData.put("GOLD_P", GOLD_P);
        importData.put("DI_WARRANT", DI_WARRANT);
        importData.put("DI_OTHER", DI_OTHER);
        //--------------------↑U10000.import↑--------------------
        //--------------------↓U10000.dividend↓--------------------
        Map<String,Object> dividend = new HashMap<>();
        
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> U10000DividendDataList = (List<Map<String,Object>>)this.dao.findForList("UExportMapper.selectU10000DividendData", queryParam);
        if(U10000DividendDataList == null) {
            U10000DividendDataList = new ArrayList<>(); 
        }
        @SuppressWarnings("unchecked")
        Map<String,Object> U10000DividendSumData = (Map<String,Object>)this.dao.findForObject("UExportMapper.selectU10000DividendSumDataForReport", queryParam);
        if(U10000DividendSumData == null) {
            U10000DividendSumData = new HashMap<>();
        }
        U10000DividendDataList.add(U10000DividendSumData);
        dividend.put("list", U10000DividendDataList);
        dividend.put("count", U10000DividendDataList.size());
        dataSumCheck = this.dao.findForObject("UExportMapper.checkIfU10000DividendHasDataForReport", queryParam);
        dividend.put("dataSumCheck", dataSumCheck == null ? 0d : dataSumCheck);
        //--------------------↑U10000.dividend↑--------------------
        //--------------------↓U10000.other_r↓--------------------
        Map<String,Object> other_r = new HashMap<>();
        
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> U10000OtherRDataList = (List<Map<String,Object>>)this.dao.findForList("UExportMapper.selectU10000OtherRData", queryParam);
        if(U10000OtherRDataList == null) {
            U10000OtherRDataList = new ArrayList<>(); 
        }
        @SuppressWarnings("unchecked")
        Map<String,Object> U10000OtherRSumData = (Map<String,Object>)this.dao.findForObject("UExportMapper.selectU10000OtherRSumDataForReport", queryParam);
        if(U10000OtherRSumData == null) {
            U10000OtherRSumData = new HashMap<>();
        }
        U10000OtherRDataList.add(U10000OtherRSumData);
        other_r.put("list", U10000OtherRDataList);
        other_r.put("count", U10000OtherRDataList.size());
        dataSumCheck = this.dao.findForObject("UExportMapper.checkIfU10000OtherRHasDataForReport", queryParam);
        other_r.put("dataSumCheck", dataSumCheck == null ? 0d : dataSumCheck);
        //--------------------↑U10000.other_r↑--------------------
        //--------------------↓U10000.trxFee↓--------------------
        Map<String,Object> trxFee = new HashMap<>();
        
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> U10000TrxFeeDataList = (List<Map<String,Object>>)this.dao.findForList("UExportMapper.selectU10000TrxFeeData", queryParam);
        if(U10000TrxFeeDataList == null) {
            U10000TrxFeeDataList = new ArrayList<>(); 
        }
        @SuppressWarnings("unchecked")
        Map<String,Object> U10000TrxFeeSumData = (Map<String,Object>)this.dao.findForObject("UExportMapper.selectU10000TrxFeeSumDataForReport", queryParam);
        if(U10000TrxFeeSumData == null) {
            U10000TrxFeeSumData = new HashMap<>();
        }
        U10000TrxFeeDataList.add(U10000TrxFeeSumData);
        trxFee.put("list", U10000TrxFeeDataList);
        trxFee.put("count", U10000TrxFeeDataList.size());
        dataSumCheck = this.dao.findForObject("UExportMapper.checkIfU10000TrxFeeHasDataForReport", queryParam);
        trxFee.put("dataSumCheck", dataSumCheck == null ? 0d : dataSumCheck);
        //--------------------↑U10000.trxFee↑--------------------
        //--------------------↓U10000.other_c↓--------------------
        Map<String,Object> other_c = new HashMap<>();
        
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> U10000OtherCDataList = (List<Map<String,Object>>)this.dao.findForList("UExportMapper.selectU10000OtherCData", queryParam);
        if(U10000OtherCDataList == null) {
            U10000OtherCDataList = new ArrayList<>(); 
        }
        @SuppressWarnings("unchecked")
        Map<String,Object> U10000OtherCSumData = (Map<String,Object>)this.dao.findForObject("UExportMapper.selectU10000OtherCSumData", queryParam);
        if(U10000OtherCSumData == null) {
            U10000OtherCSumData = new HashMap<>();
        }
        U10000OtherCDataList.add(U10000OtherCSumData);
        other_c.put("list", U10000OtherCDataList); 
        other_c.put("count", U10000OtherCDataList.size());
        dataSumCheck = this.dao.findForObject("UExportMapper.checkIfU10000OtherCHasDataForReport", queryParam);
        other_c.put("dataSumCheck", dataSumCheck == null ? 0d : dataSumCheck);
        //--------------------↑U10000.other_c↑--------------------
        U10000.put("interest", interest);
        U10000.put("importData", importData);
        U10000.put("dividend", dividend);
        U10000.put("other_r", other_r);
        U10000.put("trxFee", trxFee);
        U10000.put("other_c", other_c);
        //====================↑U10000↑====================
        
        result.put("C10000", C10000);
        result.put("H10000", H10000);
        result.put("H800", H800);
        result.put("E300", E300);
        result.put("G10000", G10000);
        result.put("N10000", N10000);
        result.put("P10000", P10000);
        result.put("T10000", T10000);
        result.put("T11000", T11000);
        result.put("U10000", U10000);
        return result;
    }
    
    /**
     * 处理Part3 第二部分
     * @author Dai Zong 2017年12月25日
     * 
     * @param queryParam
     * @return
     * @throws Exception
     */
    private Map<String,Object> processP3Sec2(Map<String,Object> queryParam) throws Exception{
        Map<String,Object> result = new HashMap<>();
        
        //====================↓T11000↓====================
        Map<String,Object> T11000 = new HashMap<>();
        //--------------------↓T11000.main↓--------------------
        Map<String,Object> main = new HashMap<>(); 
        List<Map<String,Object>> levels = new ArrayList<>();
        final BigDecimal zero = new BigDecimal(0);
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> T11000DataList = (List<Map<String,Object>>)this.dao.findForList("TExportMapper.selectT11000MainData", queryParam);
        if(T11000DataList == null) {
            T11000DataList = new ArrayList<>(); 
        }
        List<String> levelNames = T11000DataList.stream().map(item -> {
            return String.valueOf(item.get("level"));
        }).distinct().sorted(LEVEL_COMPARATOR).collect(Collectors.toList());
        
        Map<String, List<Map<String, Object>>> groupsT11000 = T11000DataList.stream().collect(Collectors.groupingBy(item -> {
            return String.valueOf(item.get("level"));
        }));
        
        for(String levelName : levelNames) {
            Map<String,Object> level = new HashMap<>();
            List<Map<String, Object>> list_t = groupsT11000.get(levelName);
            if(list_t == null) {
                list_t = new ArrayList<>();
            }
            level.put("levelName", levelName);
            Map<String,Object> levelQueryMap = new HashMap<>();
            levelQueryMap.put("fundId", queryParam.get("fundId"));
            levelQueryMap.put("level", levelName);
            level.put("levelFullName", this.dao.findForObject("FundStructuredMapper.selectLevelNameData", levelQueryMap));
            level.put("list_t", list_t);
            level.put("count_t", list_t.size());
            
            Map<String, Object> sumData = list_t.stream().reduce(new HashMap<>(), (sum, item) -> {
                BigDecimal bonusUnit = (sum.get("bonusUnit") == null ? zero : new BigDecimal(String.valueOf(sum.get("bonusUnit")))).add(
                        (item.get("bonusUnit") == null ? zero : new BigDecimal(String.valueOf(item.get("bonusUnit")))));
                BigDecimal cashAmount = (sum.get("cashAmount") == null ? zero : new BigDecimal(String.valueOf(sum.get("cashAmount")))).add(
                        (item.get("cashAmount") == null ? zero : new BigDecimal(String.valueOf(item.get("cashAmount")))));
                BigDecimal reinvestAmount = (sum.get("reinvestAmount") == null ? zero : new BigDecimal(String.valueOf(sum.get("reinvestAmount")))).add(
                        (item.get("reinvestAmount") == null ? zero : new BigDecimal(String.valueOf(item.get("reinvestAmount")))));
                BigDecimal totalAmount = (sum.get("totalAmount") == null ? zero : new BigDecimal(String.valueOf(sum.get("totalAmount")))).add(
                        (item.get("totalAmount") == null ? zero : new BigDecimal(String.valueOf(item.get("totalAmount")))));
                sum.put("bonusUnit", (bonusUnit.doubleValue() == 0d? null : bonusUnit));
                sum.put("cashAmount", (cashAmount.doubleValue() == 0d? null : cashAmount));
                sum.put("reinvestAmount", (reinvestAmount.doubleValue() == 0d? null : reinvestAmount));
                sum.put("totalAmount", (totalAmount.doubleValue() == 0d? null : totalAmount));
                return sum;  
            });
            level.put("sum", sumData);
            
            levels.add(level);
        }
        main.put("levels", levels);
        main.put("levelCount", levels.size());
        //--------------------↑T11000.main↑--------------------
        T11000.put("main", main);
        //====================↑T11000↑====================
        
        result.put("T11000", T11000);
        return result;
    }
    
    /**
     * 处理Part3 第三部分
     * @author Dai Zong 2017年12月25日
     * 
     * @param queryParam
     * @return
     * @throws Exception
     */
    private Map<String,Object> processP3Sec3(Map<String,Object> queryParam) throws Exception{
        Map<String,Object> result = new HashMap<>();
        //====================↓relatedParty↓====================
        Map<String,Object> relatedParty = new HashMap<>();
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> relatedPartyDataList = (List<Map<String,Object>>)this.dao.findForList("FundRelatedPartyMapper.selectRelatedPartyDataForReport", queryParam);
        if(relatedPartyDataList == null) {
            relatedPartyDataList = new ArrayList<>(); 
        }
        relatedParty.put("list", relatedPartyDataList);
        relatedParty.put("count", relatedPartyDataList.size());
        //====================↑relatedParty↑====================
        
        //====================↓IRP↓====================
        Map<String,Object> IRP = new HashMap<>();
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> IRPDataList = (List<Map<String,Object>>)this.dao.findForList("IExportMapper.selectIRpData", queryParam);
        if(IRPDataList == null) {
            IRPDataList = new ArrayList<>(); 
        }
        IRP.put("list", IRPDataList);
        IRP.put("count", IRPDataList.size());
        //====================↑IRP↑====================
        result.put("relatedParty", relatedParty);
        result.put("IRP", IRP);
        return result;
    }
    
    /**
     * 处理Part3 第四部分
     * @author Dai Zong 2017年12月26日
     * 
     * @param queryParam
     * @return
     * @throws Exception
     */
    private Map<String,Object> processP3Sec4(Map<String,Object> queryParam) throws Exception{
        Map<String,Object> result = new HashMap<>();
        
        Map<String,Object> queryParamLast= new HashMap<>();
        queryParam.forEach((k,v) -> {
            queryParamLast.put(k, v);
        });
        queryParamLast.put("period", (Integer.parseInt(String.valueOf(queryParamLast.get("period")).substring(0, 4)) - 1) + "1231");
        
        //====================↓I↓====================
        Map<String,Object> I = new HashMap<>();
        //--------------------↓I.transaction↓--------------------
        Map<String, Object> transaction = new HashMap<String,Object>();
        Map<String, Object> stock = new HashMap<String,Object>();
        Map<String, Object> bond = new HashMap<String,Object>();
        Map<String, Object> warrant = new HashMap<String,Object>();
        Map<String, Object> repo = new HashMap<String,Object>();
        Map<String, Object> fund = new HashMap<String,Object>();
        
        List<Map<String, Object>> tempList = null;
        tempList = this.selectITransactionDataList(queryParam, "STOCK");
        stock.put("list", tempList);
        stock.put("count", tempList.size());
        tempList = this.selectITransactionDataList(queryParam, "BOND");
        bond.put("list", tempList);
        bond.put("count", tempList.size());
        tempList = this.selectITransactionDataList(queryParam, "WARRANT");
        warrant.put("list", tempList);
        warrant.put("count", tempList.size());
        tempList = this.selectITransactionDataList(queryParam, "REPO");
        repo.put("list", tempList);
        repo.put("count", tempList.size());
        tempList = this.selectITransactionDataList(queryParam, "FUND");
        fund.put("list", tempList);
        fund.put("count", tempList.size());
        
        transaction.put("stock", stock);
        transaction.put("bond", bond);
        transaction.put("warrant", warrant);
        transaction.put("repo", repo);
        transaction.put("fund", fund);
        
        Map<String, Object> related = new HashMap<String,Object>();
        Map<String, Object> current = new HashMap<String,Object>();
        Map<String, Object> last = new HashMap<String,Object>();
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> n500RelatedCurrentMetaDataList = (List<Map<String,Object>>)this.dao.findForList("IExportMapper.selectN500RelatedData", queryParam);
        if(n500RelatedCurrentMetaDataList == null) {
            n500RelatedCurrentMetaDataList = new ArrayList<>();
        }
        current.put("list", n500RelatedCurrentMetaDataList);
        current.put("count", n500RelatedCurrentMetaDataList.size());
        
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> n500RelatedLastMetaDataList = (List<Map<String,Object>>)this.dao.findForList("IExportMapper.selectN500RelatedData", queryParamLast);
        if(n500RelatedLastMetaDataList == null) {
            n500RelatedLastMetaDataList = new ArrayList<>();
        }
        last.put("list", n500RelatedLastMetaDataList);
        last.put("count", n500RelatedLastMetaDataList.size());
        related.put("current", current);
        related.put("last", last);
        Double dataSumCheckCurrent = (Double)this.dao.findForObject("IExportMapper.checkIfN500RelatedHasDataForReport", queryParam);
        Double dataSumCheckLast = (Double)this.dao.findForObject("IExportMapper.checkIfN500RelatedHasDataForReport", queryParamLast);
        Double dataSumCheck = (dataSumCheckCurrent == null ? 0 : dataSumCheckCurrent) + (dataSumCheckLast == null ? 0 : dataSumCheckLast);
        related.put("dataSumCheck", dataSumCheck);
        
        transaction.put("related", related);
        //--------------------↑I.transaction↑--------------------
        //--------------------↓I.manageFee↓--------------------
        Map<String, Object> manageFee = new HashMap<String,Object>();
        
        List<Map<String, Object>> manage = new ArrayList<>();
        List<Map<String, Object>> trustee = new ArrayList<>();
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> manageFeeMetaDataList = (List<Map<String,Object>>)this.dao.findForList("IExportMapper.selectIManageFeeData", queryParam);
        if(manageFeeMetaDataList == null) {
            manageFeeMetaDataList = new ArrayList<>();
        }
        for(Map<String,Object> map : manageFeeMetaDataList) {
            if("MANAGE".equals(map.get("tpye"))) {
                manage.add(map);
            }else if("TRUSTEE".equals(map.get("tpye"))) {
                trustee.add(map);
            }
        }
        
        manageFee.put("manageList", manage);
        manageFee.put("manageCount", manage.size());
        manageFee.put("trusteeList", trustee);
        manageFee.put("trusteeCount", trustee.size());
        //--------------------↑I.manageFee↑--------------------
        //--------------------↓I.salesFee↓--------------------
        Map<String, Object> salesFee = new HashMap<String,Object>();
        //普通数据
        List<Map<String, Object>> salesFeeResultList = new ArrayList<>();
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> salesFeeMetaDataList = (List<Map<String,Object>>)this.dao.findForList("IExportMapper.selectISalesFeeData", queryParam);
        if(salesFeeMetaDataList == null) {
            salesFeeMetaDataList = new ArrayList<>();
        }
        List<String> levelNames = salesFeeMetaDataList.stream().map(item -> {
            return String.valueOf(item.get("level"));
        }).distinct().sorted(LEVEL_COMPARATOR).collect(Collectors.toList());
        Map<String, Map<String, List<Map<String, Object>>>> groups = salesFeeMetaDataList.stream().collect(Collectors.groupingBy(item -> {
            Map<String, Object> map = (Map<String, Object>)item;
            return String.valueOf(map.get("partyShortName"));
        }, Collectors.groupingBy(item -> {
            Map<String, Object> map = (Map<String, Object>)item;
            return String.valueOf(map.get("level"));
        })));
        for(Entry<String, Map<String, List<Map<String, Object>>>> entry : groups.entrySet()) {
            Map<String, Object> middleMap = new HashMap<String,Object>();
            List<Map<String,Object>> levelDataList = new ArrayList<>();
            
            String partyShortName = entry.getKey();
            Map<String, List<Map<String, Object>>> levelMap = entry.getValue();
            
            Double salesCommisionAmtSum = new Double(0d);
            Double salesCommisionAmtLastSum = new Double(0d);
            for(String levelName : levelNames) {
                List<Map<String, Object>> oneLevelList = levelMap.get(levelName);
                if(CollectionUtils.isNotEmpty(oneLevelList)) {
                    Map<String, Object> temp = oneLevelList.get(0);
                    levelDataList.add(temp);
                    salesCommisionAmtSum += (temp.get("salesCommisionAmt")==null?0d:Double.parseDouble(String.valueOf(temp.get("salesCommisionAmt"))));
                    salesCommisionAmtLastSum += (temp.get("salesCommisionAmtLast")==null?0d:Double.parseDouble(String.valueOf(temp.get("salesCommisionAmtLast"))));
                }
            }
            
            Map<String, Object> one = null;
            if(CollectionUtils.isNotEmpty(levelDataList)) {
                one = levelDataList.get(0);
            }else {
                one = new HashMap<>();
            }
            
            middleMap.put("partyShortName", partyShortName);
            middleMap.put("leves", levelDataList);
            middleMap.put("count", levelDataList.size());
            middleMap.put("salesCommisionBal", one.get("salesCommisionBal"));
            middleMap.put("salesCommisionBalLast", one.get("salesCommisionBalLast"));
            middleMap.put("salesCommisionAmtSum", salesCommisionAmtSum.doubleValue() == 0d?null:salesCommisionAmtSum);
            middleMap.put("salesCommisionAmtLastSum", salesCommisionAmtLastSum.doubleValue() == 0d?null:salesCommisionAmtLastSum);
            salesFeeResultList.add(middleMap);
        }
        //汇总数据
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> salesFeeSumDataList = (List<Map<String,Object>>)this.dao.findForList("IExportMapper.selectISalesFeeSumDataForReport", queryParam);
        if(salesFeeSumDataList == null) {
            salesFeeSumDataList = new ArrayList<>();
        }
        {
            Map<String, Object> sumMap = new HashMap<>();
            List<Map<String, Object>> levelDataSumList = new ArrayList<>();
            Map<String, List<Map<String, Object>>> levelMap = salesFeeSumDataList.stream().collect(Collectors.groupingBy(item -> {
                return String.valueOf(item.get("level"));
            }));
            
            Double salesCommisionAmtSum = new Double(0d);
            Double salesCommisionAmtLastSum = new Double(0d);
            for(String levelName : levelNames) {
                List<Map<String, Object>> oneLevelList = levelMap.get(levelName);
                if(CollectionUtils.isNotEmpty(oneLevelList)) {
                    Map<String, Object> temp = oneLevelList.get(0);
                    levelDataSumList.add(temp);
                    salesCommisionAmtSum += (temp.get("salesCommisionAmt")==null?0d:Double.parseDouble(String.valueOf(temp.get("salesCommisionAmt"))));
                    salesCommisionAmtLastSum += (temp.get("salesCommisionAmtLast")==null?0d:Double.parseDouble(String.valueOf(temp.get("salesCommisionAmtLast"))));
                }
            }
            
            Map<String, Object> one = null;
            if(CollectionUtils.isNotEmpty(levelDataSumList)) {
                one = levelDataSumList.get(0);
            }else {
                one = new HashMap<>();
            }
            
            sumMap.put("partyShortName", "合计");
            sumMap.put("leves", levelDataSumList);
            sumMap.put("count", levelDataSumList.size());
            sumMap.put("salesCommisionBal", one.get("salesCommisionBal"));
            sumMap.put("salesCommisionBalLast", one.get("salesCommisionBalLast"));
            sumMap.put("salesCommisionAmtSum", salesCommisionAmtSum.doubleValue() == 0d?null:salesCommisionAmtSum);
            sumMap.put("salesCommisionAmtLastSum", salesCommisionAmtLastSum.doubleValue() == 0d?null:salesCommisionAmtLastSum);
            salesFeeResultList.add(sumMap);
        }
        salesFee.put("levelNames", levelNames);
        salesFee.put("levelCount", levelNames.size());
        salesFee.put("list", salesFeeResultList);
        salesFee.put("count", salesFeeResultList.size());
        //--------------------↑I.salesFee↑--------------------
        //--------------------↓I.bankThx↓--------------------
        Map<String, Object> bankThx = new HashMap<String,Object>();
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> bankThxDataList = (List<Map<String,Object>>)this.dao.findForList("IExportMapper.selectIBankThxData", queryParam);
        if(bankThxDataList == null) {
            bankThxDataList = new ArrayList<>();
        }
        bankThx.put("list", bankThxDataList);
        bankThx.put("count", bankThxDataList.size());
        //--------------------↑I.bankThx↑--------------------
        //--------------------↓I.mgerHoldFund↓--------------------
        Map<String, Object> mgerHoldFund = new HashMap<String,Object>();
        List<Map<String,Object>> levels = new ArrayList<>(); 
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> mgerHoldFundDataList = (List<Map<String,Object>>)this.dao.findForList("IExportMapper.selectIMgerHoldFundData", queryParam);
        if(mgerHoldFundDataList == null) {
            mgerHoldFundDataList = new ArrayList<>();
        }
        levelNames = mgerHoldFundDataList.stream().map(item -> {
            return String.valueOf(item.get("level"));
        }).distinct().sorted(LEVEL_COMPARATOR).collect(Collectors.toList());
        Map<String, List<Map<String, Object>>> groups2 = mgerHoldFundDataList.stream().collect(Collectors.groupingBy(item -> {
            return String.valueOf(item.get("level"));
        }));
        for(String levelName : levelNames) {
            Map<String, Object> level = new HashMap<String,Object>();
            List<Map<String, Object>> levelList = groups2.get(levelName);
            if(levelList == null) {
                levelList = new ArrayList<>();
            }
            
            Map<String,Object> levelQueryMap = new HashMap<>();
            levelQueryMap.put("fundId", queryParam.get("fundId"));
            levelQueryMap.put("level", levelName);
            level.put("levelFullName", this.dao.findForObject("FundStructuredMapper.selectLevelNameData", levelQueryMap));
            level.put("levelFakeFullName", this.dao.findForObject("FundStructuredMapper.selectFakeLevelNameData", levelQueryMap));
            level.put("list", levelList);
            level.put("count", levelList.size());
            levels.add(level);
        }
        mgerHoldFund.put("levels", levels);
        mgerHoldFund.put("levelsCount", levels.size());
        Object dataSumCheck2 = this.dao.findForObject("IExportMapper.checkIfIMgerHoldFundHasDataForReport", queryParam);
        mgerHoldFund.put("dataSumCheck", dataSumCheck2 == null ? 0d : dataSumCheck2);
        //--------------------↑I.mgerHoldFund↑--------------------
        //--------------------↓I.unmgerHoldFund↓--------------------
        Map<String, Object> unmgerHoldFund = new HashMap<String,Object>();
        List<Map<String,Object>> resultList = new ArrayList<>();
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> unmgerHoldFundMetaDataList = (List<Map<String,Object>>)this.dao.findForList("IExportMapper.selectIUnmgerHoldFundData", queryParam);
        if(unmgerHoldFundMetaDataList == null) {
            unmgerHoldFundMetaDataList = new ArrayList<>();
        }
        levelNames = unmgerHoldFundMetaDataList.stream().map(item -> {
            return String.valueOf(item.get("level"));
        }).distinct().sorted(LEVEL_COMPARATOR).collect(Collectors.toList());
        groups2 = unmgerHoldFundMetaDataList.stream().collect(Collectors.groupingBy(item -> {
            Map<String,Object> map = (Map<String,Object>)item;
            return String.valueOf(map.get("level"));
        }));
        for(String levelName :levelNames) {
            Map<String,Object> result2 = new HashMap<>();
            List<Map<String, Object>> levelList = groups2.get(levelName);
            if(levelList == null) {
                levelList = new ArrayList<>();
            }
            result2.put("list", levelList);
            result2.put("count", levelList.size());
            Map<String,Object> levelQueryMap = new HashMap<>();
            levelQueryMap.put("fundId", queryParam.get("fundId"));
            levelQueryMap.put("level", levelName);
            result2.put("levelFullName", this.dao.findForObject("FundStructuredMapper.selectLevelNameData", levelQueryMap));
            result2.put("levelFakeFullName", this.dao.findForObject("FundStructuredMapper.selectFakeLevelNameData", levelQueryMap));
            resultList.add(result2);
        }
        unmgerHoldFund.put("levels", resultList);
        unmgerHoldFund.put("levelsCount", resultList.size());
        //--------------------↑I.unmgerHoldFund↑--------------------
        //--------------------↓I.bank↓--------------------
        Map<String, Object> bank = new HashMap<String,Object>();
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> IBankDataList = (List<Map<String,Object>>)this.dao.findForList("IExportMapper.selectIBankData", queryParam);
        if(IBankDataList == null) {
            IBankDataList = new ArrayList<>();
        }
        bank.put("list", IBankDataList);
        bank.put("count", IBankDataList.size());
        //--------------------↑I.bank↑--------------------
        //--------------------↓I.underWrite↓--------------------
        Map<String, Object> underWrite = new HashMap<String,Object>();
        Map<String, Object> current2 = new HashMap<String,Object>();
        Map<String, Object> last2 = new HashMap<String,Object>();
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> underWriteCurrentMetaDataList = (List<Map<String,Object>>)this.dao.findForList("IExportMapper.selectIUnderWriteData", queryParam);
        if(underWriteCurrentMetaDataList == null) {
            underWriteCurrentMetaDataList = new ArrayList<>();
        }
        current2.put("list", underWriteCurrentMetaDataList);
        current2.put("count", underWriteCurrentMetaDataList.size());
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> underWriteLastMetaDataList = (List<Map<String,Object>>)this.dao.findForList("IExportMapper.selectIUnderWriteData", queryParamLast);
        if(underWriteLastMetaDataList == null) {
            underWriteLastMetaDataList = new ArrayList<>();
        }
        last2.put("list", underWriteLastMetaDataList);
        last2.put("count", underWriteLastMetaDataList.size());
        underWrite.put("current", current2);
        underWrite.put("last", last2);
        //--------------------↑I.underWrite↑--------------------
        
        I.put("transaction", transaction);
        I.put("manageFee", manageFee);
        I.put("salesFee", salesFee);
        I.put("bankThx", bankThx);
        I.put("mgerHoldFund", mgerHoldFund);
        I.put("unmgerHoldFund", unmgerHoldFund);
        I.put("bank", bank);
        I.put("underWrite", underWrite);
        //====================↑I↑====================
        result.put("I", I);
        return result;
    }
    
    /**
     * 处理Part3 第五部分
     * @author Dai Zong 2017年12月27日
     * 
     * @param queryParam
     * @return
     * @throws Exception
     */
    private Map<String,Object> processP3Sec5(Map<String,Object> queryParam) throws Exception{
        Map<String,Object> result = new HashMap<>();
        //====================↓T310↓====================
        Map<String,Object> T310 = new HashMap<>();
        List<Map<String,Object>> levels = new ArrayList<>();
        final BigDecimal zero = new BigDecimal(0);
        
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> T310DataList = (List<Map<String,Object>>)this.dao.findForList("TExportMapper.selectT310DataForReport", queryParam);
        if(T310DataList == null) {
            T310DataList = new ArrayList<>(); 
        }
        List<String> levelNames= T310DataList.stream().map(item -> {
            return String.valueOf(item.get("level"));
        }).distinct().sorted(LEVEL_COMPARATOR).collect(Collectors.toList());
        Map<String, List<Map<String, Object>>> groupsT310 = T310DataList.stream().collect(Collectors.groupingBy(item -> {
            return String.valueOf(item.get("level"));
        }));
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> N800DisDataList = (List<Map<String,Object>>)this.dao.findForList("NExportMapper.selectN800DisDataForReport", queryParam);
        if(N800DisDataList == null) {
            N800DisDataList = new ArrayList<>(); 
        }
        List<String> levelNamesN800Dis = N800DisDataList.stream().map(item -> {
            return String.valueOf(item.get("level"));
        }).distinct().sorted(LEVEL_COMPARATOR).collect(Collectors.toList());
        for(String levelNameN800Dis : levelNamesN800Dis) {
            if(!levelNames.contains(levelNameN800Dis)) {
                levelNames.add(levelNameN800Dis);
            }
        }
        Map<String, List<Map<String, Object>>> groupsN800Dis = N800DisDataList.stream().collect(Collectors.groupingBy(item -> {
            return String.valueOf(item.get("level"));
        }));
        for(String levelName : levelNames) {
            Map<String,Object> level = new HashMap<>();
            List<Map<String, Object>> list_t = groupsT310.get(levelName);
            if(list_t == null) {
                list_t = new ArrayList<>();
            }
            List<Map<String, Object>> list_n = groupsN800Dis.get(levelName);
            if(list_n == null) {
                list_n = new ArrayList<>();
            }
            level.put("list_t", list_t);
            level.put("count_t", list_t.size());
            level.put("list_n", list_n);
            level.put("count_n", list_n.size());
            Map<String,Object> levelQueryMap = new HashMap<>();
            levelQueryMap.put("fundId", queryParam.get("fundId"));
            levelQueryMap.put("period", queryParam.get("period"));
            levelQueryMap.put("level", levelName);
            level.put("levelFullName", this.dao.findForObject("FundStructuredMapper.selectLevelNameData", levelQueryMap));
            level.put("T11000Count", this.dao.findForObject("TExportMapper.selectT11000MainCountDataForReport", levelQueryMap));
            
            Map<String, Object> sumData = list_t.stream().reduce(new HashMap<>(), (sum, item) -> {
                BigDecimal bonus = (sum.get("bonus") == null ? zero : new BigDecimal(String.valueOf(sum.get("bonus")))).add(
                        (item.get("bonus") == null ? zero : new BigDecimal(String.valueOf(item.get("bonus")))));
                BigDecimal cash = (sum.get("cash") == null ? zero : new BigDecimal(String.valueOf(sum.get("cash")))).add(
                        (item.get("cash") == null ? zero : new BigDecimal(String.valueOf(item.get("cash")))));
                BigDecimal reinvestAmount = (sum.get("reinvestAmount") == null ? zero : new BigDecimal(String.valueOf(sum.get("reinvestAmount")))).add(
                        (item.get("reinvestAmount") == null ? zero : new BigDecimal(String.valueOf(item.get("reinvestAmount")))));
                BigDecimal profitEy = (sum.get("profitEy") == null ? zero : new BigDecimal(String.valueOf(sum.get("profitEy")))).add(
                        (item.get("profitEy") == null ? zero : new BigDecimal(String.valueOf(item.get("profitEy")))));
                sum.put("bonus", (bonus.doubleValue() == 0d? null : bonus));
                sum.put("cash", (cash.doubleValue() == 0d? null : cash));
                sum.put("reinvestAmount", (reinvestAmount.doubleValue() == 0d? null : reinvestAmount));
                sum.put("profitEy", (profitEy.doubleValue() == 0d? null : profitEy));
                return sum;  
            });
            level.put("sum", sumData);
            
            levels.add(level);
        }
        T310.put("levels", levels);
        T310.put("levelCount", levels.size());
        Object dataSumCheck = this.dao.findForObject("TExportMapper.checkIfT310DataHasDataForReport", queryParam);
        T310.put("dataSumCheck", dataSumCheck == null ? 0d : dataSumCheck);
        //====================↑T310↑====================
        
        result.put("T310", T310);
        return result;
    }
    
    /**
     * 处理Part3 第六部分
     * @author Dai Zong 2017年12月27日
     * 
     * @param queryParam
     * @return
     * @throws Exception
     */
    private Map<String,Object> processP3Sec6(Map<String,Object> queryParam) throws Exception{
        Map<String,Object> result = new HashMap<>();
        //====================↓H11000↓====================
        Map<String,Object> H11000 = new HashMap<>();
        //--------------------↓H11000.additian↓--------------------
        Map<String,Object> additian = new HashMap<>();
        List<Map<String,Object>> stocksList = new ArrayList<>();
        List<Map<String,Object>> bondList = new ArrayList<>();
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> additianDataList = (List<Map<String,Object>>)this.dao.findForList("HExportMapper.selectH11000AdditianDataForReport", queryParam);
        if(additianDataList == null) {
            additianDataList = new ArrayList<>();
        }
        for(Map<String,Object> map : additianDataList) {
            if("股票".equals(map.get("type"))) {
                stocksList.add(map);
            }else if("债券".equals(map.get("type"))) {
                bondList.add(map);
            }
        }
        additian.put("stocksList", stocksList);
        additian.put("stocksCount", stocksList.size());
        additian.put("bondList", bondList);
        additian.put("bondCount", bondList.size());
        //--------------------↑H11000.additian↑--------------------
        //--------------------↓H11000.suspension↓--------------------
        Map<String, Object> suspension = new HashMap<String,Object>();
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> suspensionMetaDataList = (List<Map<String,Object>>)this.dao.findForList("HExportMapper.selectH11000SuspensionDataForReport", queryParam);
        if(suspensionMetaDataList == null) {
            suspensionMetaDataList = new ArrayList<>();
        }
        suspension.put("list", suspensionMetaDataList);
        suspension.put("count", suspensionMetaDataList.size());
        //--------------------↑H11000.suspension↑--------------------
        //--------------------↓H11000.saleIn↓--------------------
        Map<String, Object> saleIn = new HashMap<String,Object>();
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> saleInMetaDataList = (List<Map<String,Object>>)this.dao.findForList("HExportMapper.selectH11000SaleInDataForReport", queryParam);
        if(saleInMetaDataList == null) {
            saleInMetaDataList = new ArrayList<>();
        }
        saleIn.put("list", saleInMetaDataList);
        saleIn.put("count", saleInMetaDataList.size());
        @SuppressWarnings("unchecked")
        Map<String,Object> saleInSumData = (Map<String,Object>)this.dao.findForObject("HExportMapper.selectH11000SaleInSumDataForReport", queryParam);
        if(saleInSumData == null) {
            saleInSumData = new HashMap<>();
        }
        saleIn.put("sum", saleInSumData);
        saleIn.put("P600BankSum", this.dao.findForObject("PExportMapper.selectP600BankSumDataForReport", queryParam));
        //--------------------↑H11000.saleIn↑--------------------
        //--------------------↓H11000.note↓--------------------
        @SuppressWarnings("unchecked")
        Map<String,Object> noteData = (Map<String,Object>)this.dao.findForObject("HExportMapper.selectHNoteAData", queryParam);
        if(noteData == null) {
            noteData = new HashMap<>();
        }
        @SuppressWarnings("unchecked")
        List<String> noteDataList = (List<String>)this.dao.findForList("HExportMapper.selectH11000NoteData", queryParam);
        if(noteDataList == null) {
            noteDataList = new ArrayList<>();
        }
        SimpleDateFormat sdfin = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat sdfout = new SimpleDateFormat("yyyy年MM月dd日");
        String noteDates = noteDataList.stream().filter(item -> {
            return item != null  && !StringUtils.EMPTY.equals(item);
        }).map(item -> {
            try {
                return sdfout.format(sdfin.parse(item));
            } catch (ParseException e) {
                return StringUtils.EMPTY;
            }
        }).distinct().collect(Collectors.joining("、"));
        noteData.put("noteDates", noteDates);
        //--------------------↑H11000.note↑--------------------
        
        H11000.put("additian", additian);
        H11000.put("suspension", suspension);
        H11000.put("saleIn", saleIn);
        H11000.put("note", noteData);
        //====================↑H11000↑====================
        result.put("H11000", H11000);
        return result;
    }
    
    /**
     * 查询I表交易信息
     * @author Dai Zong 2017年12月16日
     * 
     * @param queryMap 基本查询Map
     * @param type 交易类型
     * @return I表交易信息
     * @throws Exception
     */
    private List<Map<String,Object>> selectITransactionDataList(Map<String, Object> queryMap, String type) throws Exception{
        queryMap.put("type", type);
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> metaDataList = (List<Map<String,Object>>)this.dao.findForList("IExportMapper.selectITransactionData", queryMap);
        if(metaDataList == null) {
            metaDataList = new ArrayList<>();
        }
        queryMap.remove("type");
        return metaDataList;
    }
    
    /**
     * 处理Part4
     * @author Dai Zong 2017年12月22日
     * 
     * @param exportParam
     * @param partName
     * @return
     * @throws IOException
     */
    private String processP4(Map<String,Object> exportParam, Map<String,Object> partName) throws IOException{
        String xml2003Content = DocUtil.getXml2003Content(String.valueOf(exportParam.get("reportTempRootPath")) + String.valueOf(partName.get("P4")), "<w:body><wx:sect><wx:sub-section>(.*)</wx:sub-section>", 1);
        if(StringUtils.isEmpty(xml2003Content)) {
            throw new IOException("Can not get content from P4 template");
        }
        return xml2003Content;
    }
    
    /**
     * 处理Part5
     * @author Dai Zong 2017年12月22日
     * 
     * @param exportParam
     * @param partName
     * @param queryParam
     * @return
     * @throws Exception 
     */
    private String processP5(Map<String,Object> exportParam, Map<String,Object> partName, Map<String,Object> queryParam) throws Exception{
        Map<String,Object> P5 = new HashMap<>();
        String FundId = String.valueOf(queryParam.get("fundId"));
        String Period = String.valueOf(queryParam.get("period"));
        String PeriodLast = (Integer.parseInt(Period.substring(0, 4)) - 1) + "1231";
        Map<String, Object> queryParamLast = this.createBaseQueryMap(FundId, PeriodLast);
        
        //====================↓V300↓====================
        Map<String,Object> V300 = new HashMap<>();
        @SuppressWarnings("unchecked")
        List<String> V300IntRistPeriodsDataList = (List<String>)this.dao.findForList("VExportMapper.selectV300IntRiskPeriodData", queryParam);
        if(V300IntRistPeriodsDataList == null) {
            V300IntRistPeriodsDataList = new ArrayList<>(); 
        }
        
        List<Integer> emptyList = Lists.newArrayList();
        for(int i=0 ; i<V300IntRistPeriodsDataList.size(); i++) {
            emptyList.add(0);
        }
        
        Map<String, Object> detail = new HashMap<String,Object>();
        
        Map<String, Object> attr1 = new HashMap<String, Object>();
        attr1.put("list", emptyList);
        attr1.put("count", 0);
        Map<String, Object> attr2 = new HashMap<String, Object>();
        attr2.put("list", emptyList);
        attr2.put("count", 0);
        Map<String, Object> attr3 = new HashMap<String, Object>();
        attr3.put("list", emptyList);
        attr3.put("count", 0);
        Map<String, Object> attr4 = new HashMap<String, Object>();
        attr4.put("list", emptyList);
        attr4.put("count", 0);
        Map<String, Object> attr5 = new HashMap<String, Object>();
        attr5.put("list", emptyList);
        attr5.put("count", 0);
        Map<String, Object> attr6 = new HashMap<String, Object>();
        attr6.put("list", emptyList);
        attr6.put("count", 0);
        Map<String, Object> attr7 = new HashMap<String, Object>();
        attr7.put("list", emptyList);
        attr7.put("count", 0);
        Map<String, Object> attr8 = new HashMap<String, Object>();
        attr8.put("list", emptyList);
        attr8.put("count", 0);
        Map<String, Object> attr9 = new HashMap<String, Object>();
        attr9.put("list", emptyList);
        attr9.put("count", 0);
        Map<String, Object> attr10 = new HashMap<String, Object>();
        attr10.put("list", emptyList);
        attr10.put("count", 0);
        Map<String, Object> attr11 = new HashMap<String, Object>();
        attr11.put("list", emptyList);
        attr11.put("count", 0);
        Map<String, Object> attr12 = new HashMap<String, Object>();
        attr12.put("list", emptyList);
        attr12.put("count", 0);
        Map<String, Object> attr13 = new HashMap<String, Object>();
        attr13.put("list", emptyList);
        attr13.put("count", 0);
        Map<String, Object> attr14 = new HashMap<String, Object>();
        attr14.put("list", emptyList);
        attr14.put("count", 0);
        Map<String, Object> attr15 = new HashMap<String, Object>();
        attr15.put("list", emptyList);
        attr15.put("count", 0);
        Map<String, Object> attr16 = new HashMap<String, Object>();
        attr16.put("list", emptyList);
        attr16.put("count", 0);
        Map<String, Object> attr17 = new HashMap<String, Object>();
        attr17.put("list", emptyList);
        attr17.put("count", 0);
        Map<String, Object> attr18 = new HashMap<String, Object>();
        attr18.put("list", emptyList);
        attr18.put("count", 0);
        Map<String, Object> attr19 = new HashMap<String, Object>();
        attr19.put("list", emptyList);
        attr19.put("count", 0);
        Map<String, Object> attr20 = new HashMap<String, Object>();
        attr20.put("list", emptyList);
        attr20.put("count", 0);
        Map<String, Object> attr21 = new HashMap<String, Object>();
        attr21.put("list", emptyList);
        attr21.put("count", 0);
        Map<String, Object> attr22 = new HashMap<String, Object>();
        attr22.put("list", emptyList);
        attr22.put("count", 0);
        Map<String, Object> attr23 = new HashMap<String, Object>();
        attr23.put("list", emptyList);
        attr23.put("count", 0);
        Map<String, Object> attr24 = new HashMap<String, Object>();
        attr24.put("list", emptyList);
        attr24.put("count", 0);
        Map<String, Object> attr25 = new HashMap<String, Object>();
        attr25.put("list", emptyList);
        attr25.put("count", 0);
        Map<String, Object> sum1 = new HashMap<String, Object>();
        sum1.put("list", emptyList);
        sum1.put("count", 0);
        Map<String, Object> sum2 = new HashMap<String, Object>();
        sum2.put("list", emptyList);
        sum2.put("count", 0);
        Map<String, Object> sum = new HashMap<String, Object>();
        
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> V300MetaDataList = (List<Map<String,Object>>)this.dao.findForList("VExportMapper.selectV300Data", queryParam);
        if(V300MetaDataList == null) {
            V300MetaDataList = new ArrayList<>(); 
        }
        
        Map<String, List<Map<String, Object>>> groups = V300MetaDataList.parallelStream().collect(Collectors.groupingBy(item -> {
            return String.valueOf(item.get("type"));
        }));
        
        final List<String> V300IntRistPeriodsDataListFinal = V300IntRistPeriodsDataList;
        List<Map<String,Double>> sum1List = new ArrayList<>();
        List<Map<String,Double>> sum2List = new ArrayList<>();
        for(@SuppressWarnings("unused") String intRistPeriod : V300IntRistPeriodsDataList) {
            Map<String,Double> temp1 = new HashMap<>();
            Map<String,Double> temp2 = new HashMap<>();
            sum1List.add(temp1);
            sum2List.add(temp2);
        }
        for(Entry<String, List<Map<String, Object>>> entry : groups.entrySet()) {
            String type = entry.getKey();
            List<Map<String, Object>> list = entry.getValue();
            Map<String,Map<String,Object>> tempMap = new HashMap<>();
            for(Map<String,Object> map : list) {
                tempMap.put(String.valueOf(map.get("intRiskPeriod")), map);
            }
            List<Map<String,Object>> tempList = new ArrayList<>();
            for(String period :V300IntRistPeriodsDataListFinal) {
                if(tempMap.get(period) != null) {
                   tempList.add(tempMap.get(period));
                }else {
                    tempList.add(new HashMap<>());
                }
            }
            list = tempList;
            int count = list.size();
            Double lineAmountSum;
            Double lineAmountLastSum;
            switch (type) {
                case "银行存款":
                    attr1.put("list", list);
                    attr1.put("count", count);
                    lineAmountSum = new Double(0d);
                    lineAmountLastSum = new Double(0d);
                    for(int i = 0 ; i < list.size() ; i++) {
                        Map<String,Object> map = list.get(i);
                        Map<String, Double> sumMap = sum1List.get(i);
                        Double temp1 = map.get("amount") == null ? 0 : Double.parseDouble(String.valueOf(map.get("amount")));
                        Double temp2 = map.get("amountLast") == null ? 0 : Double.parseDouble(String.valueOf(map.get("amountLast")));
                        lineAmountSum += temp1;
                        lineAmountLastSum +=temp2;
                        sumMap.put("amount", sumMap.get("amount") == null ? 0 : sumMap.get("amount") + temp1);
                        sumMap.put("amountLast", sumMap.get("amountLast") == null ? 0 : sumMap.get("amountLast") + temp2);
                    }
                    attr1.put("lineAmountSum", lineAmountSum == 0 ? null : lineAmountSum);
                    attr1.put("lineAmountLastSum", lineAmountLastSum == 0 ? null : lineAmountLastSum);
                    break;
                case "结算备付金":
                    attr2.put("list", list);
                    attr2.put("count", count);
                    lineAmountSum = new Double(0d);
                    lineAmountLastSum = new Double(0d);
                    for(int i = 0 ; i < list.size() ; i++) {
                        Map<String,Object> map = list.get(i);
                        Map<String, Double> sumMap = sum1List.get(i);
                        Double temp1 = map.get("amount") == null ? 0 : Double.parseDouble(String.valueOf(map.get("amount")));
                        Double temp2 = map.get("amountLast") == null ? 0 : Double.parseDouble(String.valueOf(map.get("amountLast")));
                        lineAmountSum += temp1;
                        lineAmountLastSum +=temp2;
                        sumMap.put("amount", sumMap.get("amount") == null ? 0 : sumMap.get("amount") + temp1);
                        sumMap.put("amountLast", sumMap.get("amountLast") == null ? 0 : sumMap.get("amountLast") + temp2);
                    }
                    attr2.put("lineAmountSum", lineAmountSum == 0 ? null : lineAmountSum);
                    attr2.put("lineAmountLastSum", lineAmountLastSum == 0 ? null : lineAmountLastSum);
                    break;
                case "存出保证金":
                    attr3.put("list", list);
                    attr3.put("count", count);
                    lineAmountSum = new Double(0d);
                    lineAmountLastSum = new Double(0d);
                    for(int i = 0 ; i < list.size() ; i++) {
                        Map<String,Object> map = list.get(i);
                        Map<String, Double> sumMap = sum1List.get(i);
                        Double temp1 = map.get("amount") == null ? 0 : Double.parseDouble(String.valueOf(map.get("amount")));
                        Double temp2 = map.get("amountLast") == null ? 0 : Double.parseDouble(String.valueOf(map.get("amountLast")));
                        lineAmountSum += temp1;
                        lineAmountLastSum +=temp2;
                        sumMap.put("amount", sumMap.get("amount") == null ? 0 : sumMap.get("amount") + temp1);
                        sumMap.put("amountLast", sumMap.get("amountLast") == null ? 0 : sumMap.get("amountLast") + temp2);
                    }
                    attr3.put("lineAmountSum", lineAmountSum == 0 ? null : lineAmountSum);
                    attr3.put("lineAmountLastSum", lineAmountLastSum == 0 ? null : lineAmountLastSum);
                    break;
                case "交易性金融资产":
                    attr4.put("list", list);
                    attr4.put("count", count);
                    lineAmountSum = new Double(0d);
                    lineAmountLastSum = new Double(0d);
                    for(int i = 0 ; i < list.size() ; i++) {
                        Map<String,Object> map = list.get(i);
                        Map<String, Double> sumMap = sum1List.get(i);
                        Double temp1 = map.get("amount") == null ? 0 : Double.parseDouble(String.valueOf(map.get("amount")));
                        Double temp2 = map.get("amountLast") == null ? 0 : Double.parseDouble(String.valueOf(map.get("amountLast")));
                        lineAmountSum += temp1;
                        lineAmountLastSum +=temp2;
                        sumMap.put("amount", sumMap.get("amount") == null ? 0 : sumMap.get("amount") + temp1);
                        sumMap.put("amountLast", sumMap.get("amountLast") == null ? 0 : sumMap.get("amountLast") + temp2);
                    }
                    attr4.put("lineAmountSum", lineAmountSum == 0 ? null : lineAmountSum);
                    attr4.put("lineAmountLastSum", lineAmountLastSum == 0 ? null : lineAmountLastSum);
                    break;
                case "衍生金融资产":
                    attr5.put("list", list);
                    attr5.put("count", count);
                    lineAmountSum = new Double(0d);
                    lineAmountLastSum = new Double(0d);
                    for(int i = 0 ; i < list.size() ; i++) {
                        Map<String,Object> map = list.get(i);
                        Map<String, Double> sumMap = sum1List.get(i);
                        Double temp1 = map.get("amount") == null ? 0 : Double.parseDouble(String.valueOf(map.get("amount")));
                        Double temp2 = map.get("amountLast") == null ? 0 : Double.parseDouble(String.valueOf(map.get("amountLast")));
                        lineAmountSum += temp1;
                        lineAmountLastSum +=temp2;
                        sumMap.put("amount", sumMap.get("amount") == null ? 0 : sumMap.get("amount") + temp1);
                        sumMap.put("amountLast", sumMap.get("amountLast") == null ? 0 : sumMap.get("amountLast") + temp2);
                    }
                    attr5.put("lineAmountSum", lineAmountSum == 0 ? null : lineAmountSum);
                    attr5.put("lineAmountLastSum", lineAmountLastSum == 0 ? null : lineAmountLastSum);
                    break;
                case "买入返售金融资产":
                    attr6.put("list", list);
                    attr6.put("count", count);
                    lineAmountSum = new Double(0d);
                    lineAmountLastSum = new Double(0d);
                    for(int i = 0 ; i < list.size() ; i++) {
                        Map<String,Object> map = list.get(i);
                        Map<String, Double> sumMap = sum1List.get(i);
                        Double temp1 = map.get("amount") == null ? 0 : Double.parseDouble(String.valueOf(map.get("amount")));
                        Double temp2 = map.get("amountLast") == null ? 0 : Double.parseDouble(String.valueOf(map.get("amountLast")));
                        lineAmountSum += temp1;
                        lineAmountLastSum +=temp2;
                        sumMap.put("amount", sumMap.get("amount") == null ? 0 : sumMap.get("amount") + temp1);
                        sumMap.put("amountLast", sumMap.get("amountLast") == null ? 0 : sumMap.get("amountLast") + temp2);
                    }
                    attr6.put("lineAmountSum", lineAmountSum == 0 ? null : lineAmountSum);
                    attr6.put("lineAmountLastSum", lineAmountLastSum == 0 ? null : lineAmountLastSum);
                    break;
                case "应收证券清算款":
                    attr7.put("list", list);
                    attr7.put("count", count);
                    lineAmountSum = new Double(0d);
                    lineAmountLastSum = new Double(0d);
                    for(int i = 0 ; i < list.size() ; i++) {
                        Map<String,Object> map = list.get(i);
                        Map<String, Double> sumMap = sum1List.get(i);
                        Double temp1 = map.get("amount") == null ? 0 : Double.parseDouble(String.valueOf(map.get("amount")));
                        Double temp2 = map.get("amountLast") == null ? 0 : Double.parseDouble(String.valueOf(map.get("amountLast")));
                        lineAmountSum += temp1;
                        lineAmountLastSum +=temp2;
                        sumMap.put("amount", sumMap.get("amount") == null ? 0 : sumMap.get("amount") + temp1);
                        sumMap.put("amountLast", sumMap.get("amountLast") == null ? 0 : sumMap.get("amountLast") + temp2);
                    }
                    attr7.put("lineAmountSum", lineAmountSum == 0 ? null : lineAmountSum);
                    attr7.put("lineAmountLastSum", lineAmountLastSum == 0 ? null : lineAmountLastSum);
                    break;
                case "应收利息":
                    attr8.put("list", list);
                    attr8.put("count", count);
                    lineAmountSum = new Double(0d);
                    lineAmountLastSum = new Double(0d);
                    for(int i = 0 ; i < list.size() ; i++) {
                        Map<String,Object> map = list.get(i);
                        Map<String, Double> sumMap = sum1List.get(i);
                        Double temp1 = map.get("amount") == null ? 0 : Double.parseDouble(String.valueOf(map.get("amount")));
                        Double temp2 = map.get("amountLast") == null ? 0 : Double.parseDouble(String.valueOf(map.get("amountLast")));
                        lineAmountSum += temp1;
                        lineAmountLastSum +=temp2;
                        sumMap.put("amount", sumMap.get("amount") == null ? 0 : sumMap.get("amount") + temp1);
                        sumMap.put("amountLast", sumMap.get("amountLast") == null ? 0 : sumMap.get("amountLast") + temp2);
                    }
                    attr8.put("lineAmountSum", lineAmountSum == 0 ? null : lineAmountSum);
                    attr8.put("lineAmountLastSum", lineAmountLastSum == 0 ? null : lineAmountLastSum);
                    break;
                case "应收股利":
                    attr9.put("list", list);
                    attr9.put("count", count);
                    lineAmountSum = new Double(0d);
                    lineAmountLastSum = new Double(0d);
                    for(int i = 0 ; i < list.size() ; i++) {
                        Map<String,Object> map = list.get(i);
                        Map<String, Double> sumMap = sum1List.get(i);
                        Double temp1 = map.get("amount") == null ? 0 : Double.parseDouble(String.valueOf(map.get("amount")));
                        Double temp2 = map.get("amountLast") == null ? 0 : Double.parseDouble(String.valueOf(map.get("amountLast")));
                        lineAmountSum += temp1;
                        lineAmountLastSum +=temp2;
                        sumMap.put("amount", sumMap.get("amount") == null ? 0 : sumMap.get("amount") + temp1);
                        sumMap.put("amountLast", sumMap.get("amountLast") == null ? 0 : sumMap.get("amountLast") + temp2);
                    }
                    attr9.put("lineAmountSum", lineAmountSum == 0 ? null : lineAmountSum);
                    attr9.put("lineAmountLastSum", lineAmountLastSum == 0 ? null : lineAmountLastSum);
                    break;
                case "应收申购款":
                    attr10.put("list", list);
                    attr10.put("count", count);
                    lineAmountSum = new Double(0d);
                    lineAmountLastSum = new Double(0d);
                    for(int i = 0 ; i < list.size() ; i++) {
                        Map<String,Object> map = list.get(i);
                        Map<String, Double> sumMap = sum1List.get(i);
                        Double temp1 = map.get("amount") == null ? 0 : Double.parseDouble(String.valueOf(map.get("amount")));
                        Double temp2 = map.get("amountLast") == null ? 0 : Double.parseDouble(String.valueOf(map.get("amountLast")));
                        lineAmountSum += temp1;
                        lineAmountLastSum +=temp2;
                        sumMap.put("amount", sumMap.get("amount") == null ? 0 : sumMap.get("amount") + temp1);
                        sumMap.put("amountLast", sumMap.get("amountLast") == null ? 0 : sumMap.get("amountLast") + temp2);
                    }
                    attr10.put("lineAmountSum", lineAmountSum == 0 ? null : lineAmountSum);
                    attr10.put("lineAmountLastSum", lineAmountLastSum == 0 ? null : lineAmountLastSum);
                    break;
                case "其他资产":
                    attr11.put("list", list);
                    attr11.put("count", count);
                    lineAmountSum = new Double(0d);
                    lineAmountLastSum = new Double(0d);
                    for(int i = 0 ; i < list.size() ; i++) {
                        Map<String,Object> map = list.get(i);
                        Map<String, Double> sumMap = sum1List.get(i);
                        Double temp1 = map.get("amount") == null ? 0 : Double.parseDouble(String.valueOf(map.get("amount")));
                        Double temp2 = map.get("amountLast") == null ? 0 : Double.parseDouble(String.valueOf(map.get("amountLast")));
                        lineAmountSum += temp1;
                        lineAmountLastSum +=temp2;
                        sumMap.put("amount", sumMap.get("amount") == null ? 0 : sumMap.get("amount") + temp1);
                        sumMap.put("amountLast", sumMap.get("amountLast") == null ? 0 : sumMap.get("amountLast") + temp2);
                    }
                    attr11.put("lineAmountSum", lineAmountSum == 0 ? null : lineAmountSum);
                    attr11.put("lineAmountLastSum", lineAmountLastSum == 0 ? null : lineAmountLastSum);
                    break;
                case "短期借款":
                    attr12.put("list", list);
                    attr12.put("count", count);
                    lineAmountSum = new Double(0d);
                    lineAmountLastSum = new Double(0d);
                    for(int i = 0 ; i < list.size() ; i++) {
                        Map<String,Object> map = list.get(i);
                        Map<String, Double> sumMap = sum2List.get(i);
                        Double temp1 = map.get("amount") == null ? 0 : Double.parseDouble(String.valueOf(map.get("amount")));
                        Double temp2 = map.get("amountLast") == null ? 0 : Double.parseDouble(String.valueOf(map.get("amountLast")));
                        lineAmountSum += temp1;
                        lineAmountLastSum +=temp2;
                        sumMap.put("amount", sumMap.get("amount") == null ? 0 : sumMap.get("amount") + temp1);
                        sumMap.put("amountLast", sumMap.get("amountLast") == null ? 0 : sumMap.get("amountLast") + temp2);
                    }
                    attr12.put("lineAmountSum", lineAmountSum == 0 ? null : lineAmountSum);
                    attr12.put("lineAmountLastSum", lineAmountLastSum == 0 ? null : lineAmountLastSum);
                    break;
                case "交易性金融负债":
                    attr13.put("list", list);
                    attr13.put("count", count);
                    lineAmountSum = new Double(0d);
                    lineAmountLastSum = new Double(0d);
                    for(int i = 0 ; i < list.size() ; i++) {
                        Map<String,Object> map = list.get(i);
                        Map<String, Double> sumMap = sum2List.get(i);
                        Double temp1 = map.get("amount") == null ? 0 : Double.parseDouble(String.valueOf(map.get("amount")));
                        Double temp2 = map.get("amountLast") == null ? 0 : Double.parseDouble(String.valueOf(map.get("amountLast")));
                        lineAmountSum += temp1;
                        lineAmountLastSum +=temp2;
                        sumMap.put("amount", sumMap.get("amount") == null ? 0 : sumMap.get("amount") + temp1);
                        sumMap.put("amountLast", sumMap.get("amountLast") == null ? 0 : sumMap.get("amountLast") + temp2);
                    }
                    attr13.put("lineAmountSum", lineAmountSum == 0 ? null : lineAmountSum);
                    attr13.put("lineAmountLastSum", lineAmountLastSum == 0 ? null : lineAmountLastSum);
                    break;
                case "衍生金融负债":
                    attr14.put("list", list);
                    attr14.put("count", count);
                    lineAmountSum = new Double(0d);
                    lineAmountLastSum = new Double(0d);
                    for(int i = 0 ; i < list.size() ; i++) {
                        Map<String,Object> map = list.get(i);
                        Map<String, Double> sumMap = sum2List.get(i);
                        Double temp1 = map.get("amount") == null ? 0 : Double.parseDouble(String.valueOf(map.get("amount")));
                        Double temp2 = map.get("amountLast") == null ? 0 : Double.parseDouble(String.valueOf(map.get("amountLast")));
                        lineAmountSum += temp1;
                        lineAmountLastSum +=temp2;
                        sumMap.put("amount", sumMap.get("amount") == null ? 0 : sumMap.get("amount") + temp1);
                        sumMap.put("amountLast", sumMap.get("amountLast") == null ? 0 : sumMap.get("amountLast") + temp2);
                    }
                    attr14.put("lineAmountSum", lineAmountSum == 0 ? null : lineAmountSum);
                    attr14.put("lineAmountLastSum", lineAmountLastSum == 0 ? null : lineAmountLastSum);
                    break;
                case "卖出回购金融资产款":
                    attr15.put("list", list);
                    attr15.put("count", count);
                    lineAmountSum = new Double(0d);
                    lineAmountLastSum = new Double(0d);
                    for(int i = 0 ; i < list.size() ; i++) {
                        Map<String,Object> map = list.get(i);
                        Map<String, Double> sumMap = sum2List.get(i);
                        Double temp1 = map.get("amount") == null ? 0 : Double.parseDouble(String.valueOf(map.get("amount")));
                        Double temp2 = map.get("amountLast") == null ? 0 : Double.parseDouble(String.valueOf(map.get("amountLast")));
                        lineAmountSum += temp1;
                        lineAmountLastSum +=temp2;
                        sumMap.put("amount", sumMap.get("amount") == null ? 0 : sumMap.get("amount") + temp1);
                        sumMap.put("amountLast", sumMap.get("amountLast") == null ? 0 : sumMap.get("amountLast") + temp2);
                    }
                    attr15.put("lineAmountSum", lineAmountSum == 0 ? null : lineAmountSum);
                    attr15.put("lineAmountLastSum", lineAmountLastSum == 0 ? null : lineAmountLastSum);
                    break;
                case "应付证券清算款":
                    attr16.put("list", list);
                    attr16.put("count", count);
                    lineAmountSum = new Double(0d);
                    lineAmountLastSum = new Double(0d);
                    for(int i = 0 ; i < list.size() ; i++) {
                        Map<String,Object> map = list.get(i);
                        Map<String, Double> sumMap = sum2List.get(i);
                        Double temp1 = map.get("amount") == null ? 0 : Double.parseDouble(String.valueOf(map.get("amount")));
                        Double temp2 = map.get("amountLast") == null ? 0 : Double.parseDouble(String.valueOf(map.get("amountLast")));
                        lineAmountSum += temp1;
                        lineAmountLastSum +=temp2;
                        sumMap.put("amount", sumMap.get("amount") == null ? 0 : sumMap.get("amount") + temp1);
                        sumMap.put("amountLast", sumMap.get("amountLast") == null ? 0 : sumMap.get("amountLast") + temp2);
                    }
                    attr16.put("lineAmountSum", lineAmountSum == 0 ? null : lineAmountSum);
                    attr16.put("lineAmountLastSum", lineAmountLastSum == 0 ? null : lineAmountLastSum);
                    break;
                case "应付赎回款":
                    attr17.put("list", list);
                    attr17.put("count", count);
                    lineAmountSum = new Double(0d);
                    lineAmountLastSum = new Double(0d);
                    for(int i = 0 ; i < list.size() ; i++) {
                        Map<String,Object> map = list.get(i);
                        Map<String, Double> sumMap = sum2List.get(i);
                        Double temp1 = map.get("amount") == null ? 0 : Double.parseDouble(String.valueOf(map.get("amount")));
                        Double temp2 = map.get("amountLast") == null ? 0 : Double.parseDouble(String.valueOf(map.get("amountLast")));
                        lineAmountSum += temp1;
                        lineAmountLastSum +=temp2;
                        sumMap.put("amount", sumMap.get("amount") == null ? 0 : sumMap.get("amount") + temp1);
                        sumMap.put("amountLast", sumMap.get("amountLast") == null ? 0 : sumMap.get("amountLast") + temp2);
                    }
                    attr17.put("lineAmountSum", lineAmountSum == 0 ? null : lineAmountSum);
                    attr17.put("lineAmountLastSum", lineAmountLastSum == 0 ? null : lineAmountLastSum);
                    break;
                case "应付管理人报酬":
                    attr18.put("list", list);
                    attr18.put("count", count);
                    lineAmountSum = new Double(0d);
                    lineAmountLastSum = new Double(0d);
                    for(int i = 0 ; i < list.size() ; i++) {
                        Map<String,Object> map = list.get(i);
                        Map<String, Double> sumMap = sum2List.get(i);
                        Double temp1 = map.get("amount") == null ? 0 : Double.parseDouble(String.valueOf(map.get("amount")));
                        Double temp2 = map.get("amountLast") == null ? 0 : Double.parseDouble(String.valueOf(map.get("amountLast")));
                        lineAmountSum += temp1;
                        lineAmountLastSum +=temp2;
                        sumMap.put("amount", sumMap.get("amount") == null ? 0 : sumMap.get("amount") + temp1);
                        sumMap.put("amountLast", sumMap.get("amountLast") == null ? 0 : sumMap.get("amountLast") + temp2);
                    }
                    attr18.put("lineAmountSum", lineAmountSum == 0 ? null : lineAmountSum);
                    attr18.put("lineAmountLastSum", lineAmountLastSum == 0 ? null : lineAmountLastSum);
                    break;
                case "应付托管费":
                    attr19.put("list", list);
                    attr19.put("count", count);
                    lineAmountSum = new Double(0d);
                    lineAmountLastSum = new Double(0d);
                    for(int i = 0 ; i < list.size() ; i++) {
                        Map<String,Object> map = list.get(i);
                        Map<String, Double> sumMap = sum2List.get(i);
                        Double temp1 = map.get("amount") == null ? 0 : Double.parseDouble(String.valueOf(map.get("amount")));
                        Double temp2 = map.get("amountLast") == null ? 0 : Double.parseDouble(String.valueOf(map.get("amountLast")));
                        lineAmountSum += temp1;
                        lineAmountLastSum +=temp2;
                        sumMap.put("amount", sumMap.get("amount") == null ? 0 : sumMap.get("amount") + temp1);
                        sumMap.put("amountLast", sumMap.get("amountLast") == null ? 0 : sumMap.get("amountLast") + temp2);
                    }
                    attr19.put("lineAmountSum", lineAmountSum == 0 ? null : lineAmountSum);
                    attr19.put("lineAmountLastSum", lineAmountLastSum == 0 ? null : lineAmountLastSum);
                    break;
                case "应付销售服务费":
                    attr20.put("list", list);
                    attr20.put("count", count);
                    lineAmountSum = new Double(0d);
                    lineAmountLastSum = new Double(0d);
                    for(int i = 0 ; i < list.size() ; i++) {
                        Map<String,Object> map = list.get(i);
                        Map<String, Double> sumMap = sum2List.get(i);
                        Double temp1 = map.get("amount") == null ? 0 : Double.parseDouble(String.valueOf(map.get("amount")));
                        Double temp2 = map.get("amountLast") == null ? 0 : Double.parseDouble(String.valueOf(map.get("amountLast")));
                        lineAmountSum += temp1;
                        lineAmountLastSum +=temp2;
                        sumMap.put("amount", sumMap.get("amount") == null ? 0 : sumMap.get("amount") + temp1);
                        sumMap.put("amountLast", sumMap.get("amountLast") == null ? 0 : sumMap.get("amountLast") + temp2);
                    }
                    attr20.put("lineAmountSum", lineAmountSum == 0 ? null : lineAmountSum);
                    attr20.put("lineAmountLastSum", lineAmountLastSum == 0 ? null : lineAmountLastSum);
                    break;
                case "应付交易费用":
                    attr21.put("list", list);
                    attr21.put("count", count);
                    lineAmountSum = new Double(0d);
                    lineAmountLastSum = new Double(0d);
                    for(int i = 0 ; i < list.size() ; i++) {
                        Map<String,Object> map = list.get(i);
                        Map<String, Double> sumMap = sum2List.get(i);
                        Double temp1 = map.get("amount") == null ? 0 : Double.parseDouble(String.valueOf(map.get("amount")));
                        Double temp2 = map.get("amountLast") == null ? 0 : Double.parseDouble(String.valueOf(map.get("amountLast")));
                        lineAmountSum += temp1;
                        lineAmountLastSum +=temp2;
                        sumMap.put("amount", sumMap.get("amount") == null ? 0 : sumMap.get("amount") + temp1);
                        sumMap.put("amountLast", sumMap.get("amountLast") == null ? 0 : sumMap.get("amountLast") + temp2);
                    }
                    attr21.put("lineAmountSum", lineAmountSum == 0 ? null : lineAmountSum);
                    attr21.put("lineAmountLastSum", lineAmountLastSum == 0 ? null : lineAmountLastSum);
                    break;
                case "应付税费":
                    attr22.put("list", list);
                    attr22.put("count", count);
                    lineAmountSum = new Double(0d);
                    lineAmountLastSum = new Double(0d);
                    for(int i = 0 ; i < list.size() ; i++) {
                        Map<String,Object> map = list.get(i);
                        Map<String, Double> sumMap = sum2List.get(i);
                        Double temp1 = map.get("amount") == null ? 0 : Double.parseDouble(String.valueOf(map.get("amount")));
                        Double temp2 = map.get("amountLast") == null ? 0 : Double.parseDouble(String.valueOf(map.get("amountLast")));
                        lineAmountSum += temp1;
                        lineAmountLastSum +=temp2;
                        sumMap.put("amount", sumMap.get("amount") == null ? 0 : sumMap.get("amount") + temp1);
                        sumMap.put("amountLast", sumMap.get("amountLast") == null ? 0 : sumMap.get("amountLast") + temp2);
                    }
                    attr22.put("lineAmountSum", lineAmountSum == 0 ? null : lineAmountSum);
                    attr22.put("lineAmountLastSum", lineAmountLastSum == 0 ? null : lineAmountLastSum);
                    break;
                case "应付利息":
                    attr23.put("list", list);
                    attr23.put("count", count);
                    lineAmountSum = new Double(0d);
                    lineAmountLastSum = new Double(0d);
                    for(int i = 0 ; i < list.size() ; i++) {
                        Map<String,Object> map = list.get(i);
                        Map<String, Double> sumMap = sum2List.get(i);
                        Double temp1 = map.get("amount") == null ? 0 : Double.parseDouble(String.valueOf(map.get("amount")));
                        Double temp2 = map.get("amountLast") == null ? 0 : Double.parseDouble(String.valueOf(map.get("amountLast")));
                        lineAmountSum += temp1;
                        lineAmountLastSum +=temp2;
                        sumMap.put("amount", sumMap.get("amount") == null ? 0 : sumMap.get("amount") + temp1);
                        sumMap.put("amountLast", sumMap.get("amountLast") == null ? 0 : sumMap.get("amountLast") + temp2);
                    }
                    attr23.put("lineAmountSum", lineAmountSum == 0 ? null : lineAmountSum);
                    attr23.put("lineAmountLastSum", lineAmountLastSum == 0 ? null : lineAmountLastSum);
                    break;
                case "应付利润":
                    attr24.put("list", list);
                    attr24.put("count", count);
                    lineAmountSum = new Double(0d);
                    lineAmountLastSum = new Double(0d);
                    for(int i = 0 ; i < list.size() ; i++) {
                        Map<String,Object> map = list.get(i);
                        Map<String, Double> sumMap = sum2List.get(i);
                        Double temp1 = map.get("amount") == null ? 0 : Double.parseDouble(String.valueOf(map.get("amount")));
                        Double temp2 = map.get("amountLast") == null ? 0 : Double.parseDouble(String.valueOf(map.get("amountLast")));
                        lineAmountSum += temp1;
                        lineAmountLastSum +=temp2;
                        sumMap.put("amount", sumMap.get("amount") == null ? 0 : sumMap.get("amount") + temp1);
                        sumMap.put("amountLast", sumMap.get("amountLast") == null ? 0 : sumMap.get("amountLast") + temp2);
                    }
                    attr24.put("lineAmountSum", lineAmountSum == 0 ? null : lineAmountSum);
                    attr24.put("lineAmountLastSum", lineAmountLastSum == 0 ? null : lineAmountLastSum);
                    break;
                case "其他负债":
                    attr25.put("list", list);
                    attr25.put("count", count);
                    lineAmountSum = new Double(0d);
                    lineAmountLastSum = new Double(0d);
                    for(int i = 0 ; i < list.size() ; i++) {
                        Map<String,Object> map = list.get(i);
                        Map<String, Double> sumMap = sum2List.get(i);
                        Double temp1 = map.get("amount") == null ? 0 : Double.parseDouble(String.valueOf(map.get("amount")));
                        Double temp2 = map.get("amountLast") == null ? 0 : Double.parseDouble(String.valueOf(map.get("amountLast")));
                        lineAmountSum += temp1;
                        lineAmountLastSum +=temp2;
                        sumMap.put("amount", sumMap.get("amount") == null ? 0 : sumMap.get("amount") + temp1);
                        sumMap.put("amountLast", sumMap.get("amountLast") == null ? 0 : sumMap.get("amountLast") + temp2);
                    }
                    attr25.put("lineAmountSum", lineAmountSum == 0 ? null : lineAmountSum);
                    attr25.put("lineAmountLastSum", lineAmountLastSum == 0 ? null : lineAmountLastSum);
                    break;
                default:
                    break;
            }
        }
        
        Double sum1LineAmountSum = new Double(0d);
        Double sum1LineAmountLastSum = new Double(0d);
        for(Map<String,Double> sum1Map : sum1List) {
            sum1LineAmountSum += (sum1Map.get("amount") == null ? 0 : sum1Map.get("amount"));
            sum1LineAmountLastSum += (sum1Map.get("amountLast") == null ? 0 : sum1Map.get("amountLast"));
        }
        Double sum2LineAmountSum = new Double(0d);
        Double sum2LineAmountLastSum = new Double(0d);
        for(Map<String,Double> sum2Map : sum2List) {
            sum2LineAmountSum += (sum2Map.get("amount") == null ? 0 : sum2Map.get("amount"));
            sum2LineAmountLastSum += (sum2Map.get("amountLast") == null ? 0 : sum2Map.get("amountLast"));
        }
        sum1.put("list", sum1List);
        sum1.put("count", sum1List.size());
        sum1.put("lineAmountSum", sum1LineAmountSum);
        sum1.put("lineAmountLastSum",sum1LineAmountLastSum);
        sum2.put("list", sum2List);
        sum2.put("count", sum2List.size());
        sum2.put("lineAmountSum", sum2LineAmountSum);
        sum2.put("lineAmountLastSum",sum2LineAmountLastSum);
        List<Map<String,Object>> sumList = new ArrayList<>();
        for(int i = 0 ; i < V300IntRistPeriodsDataList.size() ; i++) {
            Map<String,Object> temp = new HashMap<>();
            Map<String, Double> sum11 = sum1List.get(i);
            Map<String, Double> sum22 = sum2List.get(i);
            temp.put("amount", (sum11.get("amount")==null?0:sum11.get("amount")) - (sum22.get("amount")==null?0:sum22.get("amount")));
            temp.put("amountLast", (sum11.get("amountLast")==null?0:sum11.get("amountLast")) - (sum22.get("amountLast")==null?0:sum22.get("amountLast")));
            sumList.add(temp);
        }
        sum.put("list", sumList);
        sum.put("count", V300IntRistPeriodsDataList.size());
        sum.put("lineAmountSum", (sum1.get("lineAmountSum")==null ? 0 :Double.parseDouble(String.valueOf(sum1.get("lineAmountSum")))) - (sum2.get("lineAmountSum")==null ? 0 :Double.parseDouble(String.valueOf(sum2.get("lineAmountSum")))));
        sum.put("lineAmountLastSum", Double.parseDouble(String.valueOf(sum1.get("lineAmountLastSum"))) - Double.parseDouble(String.valueOf(sum2.get("lineAmountLastSum"))));
        
        detail.put("attr1", attr1);
        detail.put("attr2", attr2);
        detail.put("attr3", attr3);
        detail.put("attr4", attr4);
        detail.put("attr5", attr5);
        detail.put("attr6", attr6);
        detail.put("attr7", attr7);
        detail.put("attr8", attr8);
        detail.put("attr9", attr9);
        detail.put("attr10", attr10);
        detail.put("attr11", attr11);
        detail.put("attr12", attr12);
        detail.put("attr13", attr13);
        detail.put("attr14", attr14);
        detail.put("attr15", attr15);
        detail.put("attr16", attr16);
        detail.put("attr17", attr17);
        detail.put("attr18", attr18);
        detail.put("attr19", attr19);
        detail.put("attr20", attr20);
        detail.put("attr21", attr21);
        detail.put("attr22", attr22);
        detail.put("attr23", attr23);
        detail.put("attr24", attr24);
        detail.put("attr25", attr25);
        detail.put("sum1", sum1);
        detail.put("sum2", sum2);
        detail.put("sum", sum);
        
        V300.put("intRistPeriods", V300IntRistPeriodsDataList);
        V300.put("intRistPeriodsCount", V300IntRistPeriodsDataList.size());
        V300.put("detail", detail);
        //====================↑V300↑====================
        
        //====================↓V400↓====================
        Map<String,Object> V400 = new HashMap<>();
        
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> V400HypothesisDataList = (List<Map<String,Object>>)this.dao.findForList("VExportMapper.selectV400HypothesisDataForReport", queryParam);
        if(V400HypothesisDataList == null) {
            V400HypothesisDataList = new ArrayList<>(); 
        }
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> V400TestMetaData = (List<Map<String,Object>>)this.dao.findForList("VExportMapper.selectV400TestData", queryParam);
        V400TestMetaData = ListUtils.emptyIfNull(V400TestMetaData);
        while(V400TestMetaData.size() < 2) {
            V400TestMetaData.add(new HashMap<>());
        }
        Map<String,Object> summaryCurrent = V400TestMetaData.get(0);
        Map<String,Object> summaryLast = V400TestMetaData.get(1);
        
        V400.put("hypothesis", V400HypothesisDataList);
        V400.put("hypothesisCount", V400HypothesisDataList.size());
        V400.put("summaryCurrent", summaryCurrent);
        V400.put("summaryLast", summaryLast);
        //====================↑V400↑====================
        
        //====================↓V500↓====================
        Map<String, Object> V500 = new HashMap<String,Object>();
        
        Map<String, Object> riskExposure = new HashMap<String,Object>();
        Double netValue = 0D;
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> V500riskExposureMetaDataList = (List<Map<String,Object>>)this.dao.findForList("VExportMapper.selectV500riskExposureData", queryParam);
        if(CollectionUtils.isEmpty(V500riskExposureMetaDataList)) {
            V500riskExposureMetaDataList = new ArrayList<>(); 
        }else {
            netValue = Double.parseDouble(String.valueOf(V500riskExposureMetaDataList.get(0).get("netValueCurrent")));
        }
        riskExposure.put("list", V500riskExposureMetaDataList);
        riskExposure.put("count", V500riskExposureMetaDataList.size());
        riskExposure.put("netValue", netValue);
        @SuppressWarnings("unchecked")
        Map<String,Object> V500riskExposureSumMetaDataList = (Map<String,Object>)this.dao.findForObject("VExportMapper.selectV500riskExposureSumData", queryParam);
        if(V500riskExposureSumMetaDataList != null) {
            sum = V500riskExposureSumMetaDataList;
        }
        riskExposure.put("sum", sum);
        
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> V500HypothesisDataList = (List<Map<String,Object>>)this.dao.findForList("VExportMapper.selectV500HypothesisDataForReport", queryParam);
        if(V500HypothesisDataList == null) {
            V500HypothesisDataList = new ArrayList<>(); 
        }
        
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> V500TestMetaData = (List<Map<String,Object>>)this.dao.findForList("VExportMapper.selectV500TestData", queryParam);
        V500TestMetaData = ListUtils.emptyIfNull(V500TestMetaData);
        while(V500TestMetaData.size() < 2) {
            V500TestMetaData.add(new HashMap<>());
        }
        Map<String,Object> summaryCurrent2 = V500TestMetaData.get(0);
        Map<String,Object> summaryLast2 = V500TestMetaData.get(1);
        
        V500.put("riskExposure", riskExposure);
        V500.put("hypothesis", V500HypothesisDataList);
        V500.put("hypothesisCount", V500HypothesisDataList.size());
        V500.put("summaryCurrent", summaryCurrent2);
        V500.put("summaryLast", summaryLast2);
        //====================↑V500↑====================        
        
        //====================↓H10000↓====================   
        Map<String,Object> H10000 = new HashMap<>();
        Map<String,Object> threeLevel = new HashMap<>();
        
        @SuppressWarnings("unchecked")
        Map<String,Object> H10000ThreeLevelCurrentData = (Map<String,Object>)this.dao.findForObject("HExportMapper.selectH10000ThreeLevelData", queryParam);
        if(H10000ThreeLevelCurrentData == null) {
            H10000ThreeLevelCurrentData = new HashMap<>(); 
        }
        @SuppressWarnings("unchecked")
        Map<String,Object> H10000ThreeLevelLastData = (Map<String,Object>)this.dao.findForObject("HExportMapper.selectH10000ThreeLevelData", queryParamLast);
        if(H10000ThreeLevelLastData == null) {
            H10000ThreeLevelLastData = new HashMap<>(); 
        }
        threeLevel.put("current", H10000ThreeLevelCurrentData);
        threeLevel.put("last", H10000ThreeLevelLastData);
        
        H10000.put("threeLevel", threeLevel);
        //====================↑H10000↑====================        
        
        P5.put("V300", V300);
        P5.put("V400", V400);
        P5.put("V500", V500);
        P5.put("H10000", H10000);
        exportParam.put("P5", P5);
        return FreeMarkerUtils.processTemplateToStrUseAbsPath(exportParam, String.valueOf(exportParam.get("reportTempRootPath")), String.valueOf(partName.get("P5")));
    }
    
    /**
     * 构建基本查询Map
     * @author Dai Zong 2017年9月12日
     * 
     * @param fundId
     * @param periodStr
     * @return
     */
    private Map<String,Object> createBaseQueryMap(String fundId, String periodStr){
        Map<String, Object> res = new HashMap<String,Object>();
        res.put("fundId", fundId);
        res.put("period", periodStr);
        return res;
    }
    
    /**
     * 获取基金额外属性
     * @author Dai Zong 2017年12月2日
     * 
     * @param fundId
     * @param periodStr
     * @return
     * @throws Exception
     */
    private Map<String,Object> getExtraFundInfo(String fundId, String periodStr) throws Exception{
        Map<String, Object> queryMap = this.createBaseQueryMap(fundId, periodStr);
        
        @SuppressWarnings("unchecked")
        Map<String,Object> fundInfo = (Map<String,Object>)this.dao.findForObject("TExportMapper.selectExtraFundInfo", queryMap);
        if(fundInfo == null) {
            fundInfo = new HashMap<>();
        }
        
        Integer startYear = 2000, startMonth = 01, startDay = 01;
        if(fundInfo.get("dateFrom") != null) {
            String[] splits = String.valueOf(fundInfo.get("dateFrom")).split("-");
            try {
                if(splits.length == 1) {
                    startYear = Integer.parseInt(splits[0]);
                }else if(splits.length == 2) {
                    startYear = Integer.parseInt(splits[0]);
                    startMonth = Integer.parseInt(splits[1]);
                }else if(splits.length >= 3) {
                    startYear = Integer.parseInt(splits[0]);
                    startMonth = Integer.parseInt(splits[1]);
                    startDay = Integer.parseInt(splits[2]);
                }
            }catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        
        fundInfo.put("startYear", startYear);
        fundInfo.put("startMonth", startMonth);
        fundInfo.put("startDay", startDay);
        return fundInfo;
    }
    
}