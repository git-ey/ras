package com.ey.service.wp.output.impl;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
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
    private static final BigDecimal ZERO = new BigDecimal(0);
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
    private String firmCode;
    
    /**
     * 根据基金ID获取基金信息
     * @author Dai Zong 2017年11月8日
     * 
     * @param fundId 基金ID
     * @return 基金信息
     * @throws Exception 基金ID无效
     */
    protected Map<String,String> selectFundInfo(String fundId) throws Exception{
        Map<String, Object> query = new HashMap<>();
        query.put("fundId", fundId);
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> resMapList = (List<Map<String,Object>>)this.dao.findForList("FundMapper.selectFundInfo", query);
        if(CollectionUtils.isEmpty(resMapList) || resMapList.size() != 1) {
            throw new Exception("基金ID " + fundId + " 无效");
        }
        Map<String, String> res = new HashMap<>();
        resMapList.get(0).forEach((k,v) -> {
            res.put(k, String.valueOf(v));
        });
        return res;
    }

    /**
     * 生成文件内容
     * @author Dai Zong 2017年10月17日
     * 
     * @param exportParam
     * @param fundInfo
     * @return
     * @throws Exception
     */
    private String generateFileContent(Map<String,Object> exportParam, Map<String, String> fundInfo) throws Exception {
        Map<String, Object> dataMap = new HashMap<>();
        Map<String, Object> content = new HashMap<>();
        
        String firmCode = String.valueOf(exportParam.get("FIRM_CODE"));
        String fundId = String.valueOf(exportParam.get("FUND_ID"));
        String periodStr = String.valueOf(exportParam.get("PEROID"));
        Long period = Long.parseLong(periodStr.substring(0, 4));
        Long month = Long.parseLong(periodStr.substring(4, 6));
        Long day = Long.parseLong(periodStr.substring(6, 8));
        
        exportParam.put("period", period);
        exportParam.put("month", month);
        exportParam.put("day", day);
        exportParam.put("firmCode", firmCode);
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
        String temp = (String)reportContent.get("LR");
        if (temp != null){
            reportContent.put("LR", temp.split("\n"));
        }
        temp = (String)reportContent.get("EPA");
        if (temp != null){
            reportContent.put("EPA", temp.split("\n"));
        }
        temp = (String)reportContent.get("PLR");
        if (temp != null){
            reportContent.put("PLR", temp.split("\n"));
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
        String repType = String.valueOf(exportParam.get("REPTYPE")); // 20200507,yury
        Map<String, String> fundInfo = this.selectFundInfo(fundId);
        fundInfo.put("periodStr", periodStr);
        fundInfo.put("repType", repType); // 20200507,yury
        String fileStr = this.generateFileContent(exportParam, fundInfo);
        FileExportUtils.writeFileToHttpResponse(request, response, FreeMarkerUtils.simpleReplace(Constants.EXPORT_AIM_FILE_NAME_REPORT, fundInfo), fileStr);
        return true;
    }

    @Override
    public boolean doExport(String folederName, String fileName, Map<String,Object> exportParam) throws Exception {
        String fundId = String.valueOf(exportParam.get("FUND_ID"));
        String periodStr = String.valueOf(exportParam.get("PEROID"));
        String repType = String.valueOf(exportParam.get("REPTYPE")); // 20200507,yury
        Map<String, String> fundInfo = this.selectFundInfo(fundId);
        fundInfo.put("periodStr", periodStr);
        fundInfo.put("repType", repType); // 20200507,yury
        String fileStr = this.generateFileContent(exportParam, fundInfo);
        // ↓ daigaokuo@hotmail.com 2019-02-13 ↓
        // [IMP] 最终输出文件夹路径中添加firm code
        folederName += fundInfo.get("firmCode") + File.separatorChar;
        // ↑ daigaokuo@hotmail.com 2019-02-13 ↑
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
        // content.put("P4", this.processP4(exportParam, partName));
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
        String FundId = String.valueOf(queryParam.get("fundId"));
        String Period = String.valueOf(queryParam.get("period"));
        String PeriodLast = (Integer.parseInt(Period.substring(0, 4)) - 1) + "1231";
        Map<String, Object> queryParamLast = this.createBaseQueryMap(FundId, PeriodLast);
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
        Map<String,Object> A18 = new HashMap<>();
        Map<String,Object> A19 = new HashMap<>();
        Map<String,Object> A20 = new HashMap<>();
        Map<String,Object> A21 = new HashMap<>();
        Map<String,Object> A22 = new HashMap<>();
        Map<String,Object> A23 = new HashMap<>();
        Map<String,Object> A24 = new HashMap<>();
        Map<String,Object> A25 = new HashMap<>();
        Map<String,Object> A26 = new HashMap<>();
        Map<String,Object> A27 = new HashMap<>();
        Map<String,Object> A28 = new HashMap<>();
        Map<String,Object> A29 = new HashMap<>();
        Map<String,Object> A30 = new HashMap<>();
        Map<String,Object> A31 = new HashMap<>();
        Map<String,Object> A32 = new HashMap<>();
        Map<String,Object> A33 = new HashMap<>();
        Map<String,Object> A34 = new HashMap<>();
        Map<String,Object> A35 = new HashMap<>();
        Map<String,Object> A36 = new HashMap<>();
        Map<String,Object> A37 = new HashMap<>();
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
        Map<String,Object> B16 = new HashMap<>();
        Map<String,Object> C01 = new HashMap<>();
        Map<String,Object> C02 = new HashMap<>();
        Map<String,Object> C03 = new HashMap<>();
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
            } else if ("A18".equals(map.get("bsCode"))) {
                A18 = map;
            } else if ("A19".equals(map.get("bsCode"))) {
                A19 = map;
            } else if ("A20".equals(map.get("bsCode"))) {
                A20 = map;
            } else if ("A21".equals(map.get("bsCode"))) {
                A21 = map;
            } else if ("A22".equals(map.get("bsCode"))) {
                A22 = map;
            } else if ("A23".equals(map.get("bsCode"))) {
                A23 = map;
            } else if ("A24".equals(map.get("bsCode"))) {
                A24 = map;
            } else if ("A25".equals(map.get("bsCode"))) {
                A25 = map;
            } else if ("A26".equals(map.get("bsCode"))) {
                A26 = map;
            } else if ("A27".equals(map.get("bsCode"))) {
                A27 = map;
            } else if ("A28".equals(map.get("bsCode"))) {
                A28 = map;
            } else if ("A29".equals(map.get("bsCode"))) {
                A29 = map;
            } else if ("A30".equals(map.get("bsCode"))) {
                A30 = map;
            } else if ("A31".equals(map.get("bsCode"))) {
                A31 = map;
            } else if ("A32".equals(map.get("bsCode"))) {
                A32 = map;
            } else if ("A33".equals(map.get("bsCode"))) {
                A33 = map;
            } else if ("A34".equals(map.get("bsCode"))) {
                A34 = map;
            } else if ("A35".equals(map.get("bsCode"))) {
                A35 = map;
            } else if ("A36".equals(map.get("bsCode"))) {
                A36 = map;
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
            } else if ("B16".equals(map.get("bsCode"))) {
                B16 = map;
            } else if ("C01".equals(map.get("bsCode"))) {
                C01 = map;
            } else if ("C02".equals(map.get("bsCode"))) {
                C02 = map;
            } else if ("C03".equals(map.get("bsCode"))) {
                C03 = map;
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
        BS.put("A18", A18);
        BS.put("A19", A19);
        BS.put("A20", A20);
        BS.put("A21", A21);
        BS.put("A22", A22);
        BS.put("A23", A23);
        BS.put("A24", A24);
        BS.put("A25", A25);
        BS.put("A26", A26);
        BS.put("A27", A27);
        BS.put("A28", A28);
        BS.put("A29", A29);
        BS.put("A30", A30);
        BS.put("A31", A31);
        BS.put("A32", A32);
        BS.put("A33", A33);
        BS.put("A34", A34);
        BS.put("A35", A35);
        BS.put("A36", A36);
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
        BS.put("B16", B16);
        BS.put("C01", C01);
        BS.put("C02", C02);
        BS.put("C03", C03);
        BS.put("SUM1", SUM1);
        BS.put("SUM2", SUM2);
        BS.put("SUM3", SUM3);
        BS.put("SUM4", SUM4);

        // chenhy,20240219,新增专户逻辑，根据财务报表附注浮动列示附注号
        Object bsDerivativedataSumCheck = this.dao.findForObject("HExportMapper.checkIfH10000DerivativeHasDataForReport", queryParam);
        BS.put("derivativeDataSumCheck", bsDerivativedataSumCheck == null ? 0d : bsDerivativedataSumCheck);
        //====================↑BS↑====================

        //====================↓BS表注释文字段↓====================
        // yury,20200907,新增资产负债表下注释
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> bsNoteList = (List<Map<String, Object>>)this.dao.findForList("ReportMapper.selectBsNote", queryParam);
        if (bsNoteList == null){
            bsNoteList = new ArrayList<>();
        }
        int count = bsNoteList.size();
        Map<String, Object> BSNote = new HashMap<String, Object>();
        BSNote.put("count", count);
        BSNote.put("list", bsNoteList);

        // yury,20200907,新增资产负债表下注释上年

        @SuppressWarnings("unchecked")
        List<Map<String,Object>> bsNoteListLast = (List<Map<String, Object>>)this.dao.findForList("ReportMapper.selectBsNote", queryParamLast);
        if (bsNoteList == null){
            bsNoteList = new ArrayList<>();
        }
        count = bsNoteListLast.size();
        Map<String, Object> BSNoteLast = new HashMap<String, Object>();
        BSNoteLast.put("count", count);
        BSNoteLast.put("list", bsNoteListLast);
        //====================↑BS表注释文字段↑====================

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
        Map<String,Object> D18 = new HashMap<>();
        Map<String,Object> D19 = new HashMap<>();
        Map<String,Object> D20 = new HashMap<>();
        Map<String,Object> E01 = new HashMap<>();
        Map<String,Object> E02 = new HashMap<>();
        Map<String,Object> E03 = new HashMap<>();
        Map<String,Object> E04 = new HashMap<>();
        Map<String,Object> E05 = new HashMap<>();
        Map<String,Object> E06 = new HashMap<>();
        Map<String,Object> E07 = new HashMap<>();
        Map<String,Object> E08 = new HashMap<>();
		Map<String,Object> E09 = new HashMap<>();
		Map<String,Object> E20 = new HashMap<>();
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
            } else if ("D18".equals(map.get("plCode"))) {
                D18 = map;
            } else if ("D19".equals(map.get("plCode"))) {
                D19 = map;
            } else if ("D20".equals(map.get("plCode"))) {
                D20 = map;
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
            } else if ("E08".equals(map.get("plCode"))) {
                E08 = map;
			} else if ("E09".equals(map.get("plCode"))) {
                E09 = map;
			} else if ("E20".equals(map.get("plCode"))) {
                E20 = map;
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
        PL.put("D18", D18);
        PL.put("D19", D19);
        PL.put("D20", D20);
        PL.put("E01", E01);// Mapping: D18 <-> E01
        PL.put("E02", E02);// Mapping: D19 <-> E02
        PL.put("E03", E03);// Mapping: D20 <-> E03
        PL.put("E04", E04);// Mapping: D21 <-> E04
        PL.put("E05", E05);// Mapping: D22 <-> E05
        PL.put("E06", E06);// Mapping: D23 <-> E06
        PL.put("E07", E07);// Mapping: D24 <-> E07
        PL.put("E08", E08);// Mapping: D25 <-> E08
		PL.put("E09", E09);
		PL.put("E20", E20);
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
        for(int i=1 ; i<=12 ; i++) {
            String tag = "attr" + i;
            Map<String,Object> temp = new HashMap<>();
            if(mainContainer.get("实收基金") != null) {
                temp.put("SS", mainContainer.get("实收基金").get(tag));
            }
            if(mainContainer.get("未分配利润") != null) {
                temp.put("WFP", mainContainer.get("未分配利润").get(tag));
            }
            if(mainContainer.get("净资产合计") != null) {
                temp.put("SYZ", mainContainer.get("净资产合计").get(tag));
            }
            if(mainOldContainer.get("实收基金") != null) {
                temp.put("SSOLD", mainOldContainer.get("实收基金").get(tag));
            }
            if(mainOldContainer.get("未分配利润") != null) {
                temp.put("WFPOLD", mainOldContainer.get("未分配利润").get(tag));
            }
            if(mainOldContainer.get("净资产合计") != null) {
                temp.put("SYZOLD", mainOldContainer.get("净资产合计").get(tag));
            }
            T500.put(tag, temp);
        }
        //====================↑T500↑====================
        
        // -----------------irene20230904新增 应交税费/应付税费-----------------------
        @SuppressWarnings("unchecked")
        List<String> V300TaxPayableNameDataForBS = (List<String>) this.dao
                .findForList("VExportMapper.selectV300IntRiskTaxPayableNameData", queryParam);
        if (V300TaxPayableNameDataForBS == null) {
            V300TaxPayableNameDataForBS = new ArrayList<>();
        }
        // -----------------irene20230904新增 应交税费/应付税费 结束-----------------------

        P1.put("BS", BS);
        P1.put("BSNote", BSNote);
        P1.put("BSNoteLast", BSNoteLast);
        P1.put("PL", PL);
        P1.put("T500", T500);
        P1.put("TaxPayableName", V300TaxPayableNameDataForBS);
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
        String xml2003Content = DocUtil.getXml2003Content(String.valueOf(exportParam.get("reportTempRootPath")) + String.valueOf(partName.get("P2")), "<w:body><wx:sect>.*?<wx:sub-section>(.*)</wx:sub-section>", 1);
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
        P3.put("sec7", this.processP3Sec7(queryParam));// 吴老师 新增 转融通

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
        Map<String,Object> item1 = new HashMap<>();
        Map<String,Object> item2 = new HashMap<>();
        Map<String,Object> item3 = new HashMap<>();
        Map<String,Object> item4 = new HashMap<>();
        Map<String,Object> item5 = new HashMap<>();
        Map<String,Object> item6 = new HashMap<>();
        Map<String,Object> C10000SumDemandandOtherMetaData= new HashMap<>();

        @SuppressWarnings("unchecked")
        List<Map<String,Object>> C10000MetaDataList = (List<Map<String,Object>>)this.dao.findForList("CExportMapper.selectC10000DataForReport", queryParam);
        if(C10000MetaDataList == null) {
            C10000MetaDataList = new ArrayList<>(); 
        }
        for(Map<String,Object> map : C10000MetaDataList) {
            if("100".equals(map.get("sort")) || "活期存款".equals(map.get("item"))) {
                item1 = map;
            }else if("200".equals(map.get("sort")) || "定期存款".equals(map.get("item"))){
                item2 = map;
            }else if("300".equals(map.get("sort")) || "其他存款".equals(map.get("item"))){
                item3 = map;
            }else if("201".equals(map.get("sort"))){
                item4 = map;
            }else if("202".equals(map.get("sort"))){
                item5 = map;
            }else if("203".equals(map.get("sort"))){
                item6 = map;
            }
        }
        @SuppressWarnings("unchecked")
        Map<String,Object> C10000SumMetaData = (Map<String,Object>)this.dao.findForObject("CExportMapper.selectC10000SumDataForReport", queryParam);
        if(C10000SumMetaData == null) {
            C10000SumMetaData = new HashMap<>();
        }
        
        //chenhy,20240621,新增券商结算Flag
        String QsjsFlag = (String)this.dao.findForObject("CExportMapper.selectQsjsFlag", queryParam);
        C10000.put("QsjsFlag", QsjsFlag);

        C10000MetaDataList.add(C10000SumMetaData);
        C10000.put("list", C10000MetaDataList);
        C10000.put("count", C10000MetaDataList.size());
        C10000.put("timeDeposit", item1);
        C10000.put("demandDeposit", item2);
        C10000.put("otherDeposit", item3);
        C10000.put("demandDepositCost", item4);
        C10000.put("demandDepositInterest", item5);
        C10000.put("demandDepositBadDebt", item6);
        C10000.put("sum", C10000SumMetaData);

        C10000SumDemandandOtherMetaData.put("endBalance", this.addNumber(item1.get("endBalance"), item3.get("endBalance")));
        C10000SumDemandandOtherMetaData.put("beginBalance", this.addNumber(item1.get("beginBalance"), item3.get("beginBalance")));
        C10000.put("SumDemandandOther", C10000SumDemandandOtherMetaData);

        //====================↑C10000↑====================
        
        //====================↓H10000↓====================
        Map<String,Object> H10000 = new HashMap<>();
        //--------------------↓H10000.tfa↓--------------------
        Map<String,Object> tfa = new HashMap<>();
        
        item1 = new HashMap<>();
        item2 = new HashMap<>();
        item3 = new HashMap<>();
        item4 = new HashMap<>();
        item5 = new HashMap<>();
        item6 = new HashMap<>();
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
            }else if("资产支持证券投资".equals(map.get("item"))){
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
        Map<String,Object> bondAndItem5Sum = new HashMap<>();
        bondAndItem5Sum.put("cost", this.addNumber(item5.get("cost"), H10000BondSumData.get("cost")));
        bondAndItem5Sum.put("bookValue", this.addNumber(item5.get("bookValue"), H10000BondSumData.get("bookValue")));//-----20220715新增摊余成本
        bondAndItem5Sum.put("interestAR", this.addNumber(item5.get("interestAR"), H10000BondSumData.get("interestAR")));//-----20220616新增应计利息
        bondAndItem5Sum.put("mktValue", this.addNumber(item5.get("mktValue"), H10000BondSumData.get("mktValue")));
        bondAndItem5Sum.put("appreciation", this.addNumber(item5.get("appreciation"), H10000BondSumData.get("appreciation")));
        bondAndItem5Sum.put("costLast", this.addNumber(item5.get("costLast"), H10000BondSumData.get("costLast")));
        bondAndItem5Sum.put("bookValueLast", this.addNumber(item5.get("bookValueLast"), H10000BondSumData.get("bookValueLast")));//-----20220715新增摊余成本
        bondAndItem5Sum.put("interestARLast", this.addNumber(item5.get("interestARLast"), H10000BondSumData.get("interestARLast")));//-----20220616新增应计利息
        bondAndItem5Sum.put("mktValueLast", this.addNumber(item5.get("mktValueLast"), H10000BondSumData.get("mktValueLast")));
        bondAndItem5Sum.put("appreciationLast", this.addNumber(item5.get("appreciationLast"), H10000BondSumData.get("appreciationLast")));
        tfa.put("bondAndItem5Sum", bondAndItem5Sum);
        
        @SuppressWarnings("unchecked")
        Map<String,Object> diviatonMetaData = (Map<String,Object>)this.dao.findForObject("HExportMapper.selectH500DiviatonData", queryParam);
        if(diviatonMetaData == null) {
            diviatonMetaData = new HashMap<>();
        }
        
        @SuppressWarnings("unchecked")
        Map<String,Object> lastDiviatonMetaData = (Map<String,Object>)this.dao.findForObject("HExportMapper.selectH500DiviatonData", queryParamLast);
        if(lastDiviatonMetaData == null) {
        	lastDiviatonMetaData = new HashMap<>();
        }
        diviatonMetaData.put("lastDiviatonEy", lastDiviatonMetaData.get("diviatonEy"));
        tfa.put("diviaton", diviatonMetaData);
        
        H10000.put("tfa", tfa);
        //--------------------↑H10000.tfa↑--------------------
        
        //--------------------↓H10000.mac↓--------------------
        //--------------------20220615新增macbond--------------------
        Map<String,Object> mac = new HashMap<>();
        
        Map<String,Object> item8 = new HashMap<>();
        Map<String,Object> item9 = new HashMap<>();
        Map<String,Object> item10 = new HashMap<>();
        Map<String,Object> item11 = new HashMap<>();
        Map<String,Object> item12 = new HashMap<>();
        Map<String,Object> item13 = new HashMap<>();
        
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> H10000MacBondDataList = (List<Map<String,Object>>)this.dao.findForList("HExportMapper.selectH10000MACData", queryParam);
        if(H10000MacBondDataList == null) {
            H10000MacBondDataList = new ArrayList<>(); 
        }
        for(Map<String,Object> map : H10000MacBondDataList) {
            if("交易所".equals(map.get("subItem"))) {
                item8 = map;
            }else if("银行间".equals(map.get("subItem"))){
                item9 = map;
            }
        }
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> H10000MacABSDataList = (List<Map<String,Object>>)this.dao.findForList("HExportMapper.selectH10000MacABSDataForReport", queryParam);
        if(H10000MacABSDataList == null) {
            H10000MacABSDataList = new ArrayList<>(); 
        }
        @SuppressWarnings("unchecked")
        Map<String,Object> H10000MacBondSumData = (Map<String,Object>)this.dao.findForObject("HExportMapper.selectH10000MacBondSumDataForReport", queryParam);
        if(H10000MacBondSumData == null) {
            H10000MacBondSumData = new HashMap<>();
        }
        @SuppressWarnings("unchecked")
        Map<String,Object> H10000MacSumData = (Map<String,Object>)this.dao.findForObject("HExportMapper.selectH10000MacSumDataForReport", queryParam);
        if(H10000MacSumData == null) {
            H10000MacSumData = new HashMap<>();
        }
        for(Map<String,Object> map : H10000MacABSDataList) {
            if("资产支持证券".equals(map.get("item"))) {
                item10 = map;
            }else if("其他".equals(map.get("item"))){
                item11 = map;
            }
        }
        mac.put("item8", item8);
        mac.put("item9", item9);
        mac.put("item10", item10);
        mac.put("item11", item11);
        mac.put("sumBond", H10000MacBondSumData);
        mac.put("sum", H10000MacSumData);
        mac.put("MacbondCount", H10000MacBondDataList.size());
        mac.put("MacABSCount", H10000MacABSDataList.size());

        Object macdataSumCheck = this.dao.findForObject("HExportMapper.checkIfH10000MACHasDataForReport", queryParam);
        mac.put("macdataSumCheck", macdataSumCheck == null ? 0d : macdataSumCheck);

        Map<String,Object> bondAndABSSum = new HashMap<>();
            bondAndABSSum.put("cost", this.addNumber(item10.get("cost"), H10000MacBondSumData.get("cost")));
            bondAndABSSum.put("bookValue", this.addNumber(item10.get("bookValue"), H10000MacBondSumData.get("bookValue")));
            bondAndABSSum.put("interestADJ", this.addNumber(item10.get("interestADJ"), H10000MacBondSumData.get("interestADJ")));
            bondAndABSSum.put("interestAR", this.addNumber(item10.get("interestAR"), H10000MacBondSumData.get("interestAR")));
            bondAndABSSum.put("impairment", this.addNumber(item10.get("impairment"), H10000MacBondSumData.get("impairment")));
            bondAndABSSum.put("appreciation", this.addNumber(item10.get("appreciation"), H10000MacBondSumData.get("appreciation")));
            bondAndABSSum.put("costLast", this.addNumber(item10.get("costLast"), H10000MacBondSumData.get("costLast")));
            bondAndABSSum.put("bookValueLast", this.addNumber(item10.get("bookValueLast"), H10000MacBondSumData.get("bookValueLast")));
            bondAndABSSum.put("interestADJLast", this.addNumber(item10.get("interestADJLast"), H10000MacBondSumData.get("interestADJLast")));
            bondAndABSSum.put("interestARLast", this.addNumber(item10.get("interestARLast"), H10000MacBondSumData.get("interestARLast")));
            bondAndABSSum.put("impairmentLast", this.addNumber(item10.get("impairmentLast"), H10000MacBondSumData.get("impairmentLast")));
            bondAndABSSum.put("appreciationLast", this.addNumber(item10.get("appreciationLast"), H10000MacBondSumData.get("appreciationLast")));
        mac.put("bondAndABSSum", bondAndABSSum);
        
        
        H10000.put("mac", mac);
        //--------------------↑H10000.mac↑--------------------
        
        //--------------------↓H10000.macbad↓--------------------
        Map<String, Object> MACBad = new HashMap<String,Object>();
        item1 = new HashMap<>();
        item2 = new HashMap<>();
        item3 = new HashMap<>();
        item4 = new HashMap<>();
        item5 = new HashMap<>();
        item6 = new HashMap<>();
        item7 = new HashMap<>();
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> MacBadDataList = (List<Map<String,Object>>)this.dao.findForList("HExportMapper.selectH10000MACBad", queryParam);
        if(MacBadDataList == null) {
            MacBadDataList = new ArrayList<>(); 
        }
        Map<String,Object> tempMac = new HashMap<>();
        for(Map<String,Object> map : MacBadDataList) {
            tempMac.put(String.valueOf(map.get("item")), map);
        }
        MACBad.put("item1", tempMac.get("期初余额")==null?new HashMap<String,Object>():tempMac.get("期初余额"));
        MACBad.put("item2", tempMac.get("本期从其他阶段转入")==null?new HashMap<String,Object>():tempMac.get("本期从其他阶段转入"));
        MACBad.put("item3", tempMac.get("本期转出至其他阶段")==null?new HashMap<String,Object>():tempMac.get("本期转出至其他阶段"));
        MACBad.put("item4", tempMac.get("本期新增")==null?new HashMap<String,Object>():tempMac.get("本期新增"));
        MACBad.put("item5", tempMac.get("本期转回")==null?new HashMap<String,Object>():tempMac.get("本期转回"));
        MACBad.put("item6", tempMac.get("其他变动")==null?new HashMap<String,Object>():tempMac.get("其他变动"));
        MACBad.put("item7", tempMac.get("期末余额")==null?new HashMap<String,Object>():tempMac.get("期末余额"));

        Object macbaddataSumCheck = this.dao.findForObject("HExportMapper.checkIfH10000MACBadHasDataForReport", queryParam);
        MACBad.put("macbaddataSumCheck", macbaddataSumCheck == null ? 0d : macbaddataSumCheck);

        H10000.put("macBad", MACBad);
        
        //--------------------↑H10000.macbad↑--------------------
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

        @SuppressWarnings("unchecked")
        Map<String,Object> H10000DerivativeSumDataForTotal = (Map<String,Object>)this.dao.findForObject("HExportMapper.selectH10000DerivativeSumData", queryParam);
        if(H10000DerivativeSumDataForTotal == null) {
            H10000DerivativeSumDataForTotal = new HashMap<>(); 
        }
        
        derivative.put("item1", item1);
        derivative.put("item2", item2);
        derivative.put("item3", item3);
        derivative.put("item4", item4);
        derivative.put("sum", H10000DerivativeSumData);
        derivative.put("count", H10000DerivativeDataList.size());
        derivative.put("list", H10000DerivativeDataList);
        derivative.put("total", H10000DerivativeSumDataForTotal);
        dataSumCheck = this.dao.findForObject("HExportMapper.checkIfH10000DerivativeHasDataForReport", queryParam);
        derivative.put("dataSumCheck", dataSumCheck == null ? 0d : dataSumCheck);
        H10000.put("derivative", derivative);
        
        //--------------------↑H10000.derivative↑--------------------
        //--------------------↓H10000.derivative_note(目前只有专户有)↓--------------------

        Map<String, Object> derivative_note = new HashMap<>();

        Object derivativeNote = this.dao.findForObject("HExportMapper.selectH10000DerivativeNote", queryParam);

        derivative_note.put("note", derivativeNote);
        H10000.put("derivative_note", derivative_note);
        
        //--------------------↑H10000.derivative_note(目前只有专户有)↑--------------------
        //--------------------↓H10000.futures↓--------------------
        Map<String,Object> futures = new HashMap<>();
        Map<String,Object> goldfutures = new HashMap<>();
        
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> H10000futuresDataList = (List<Map<String,Object>>)this.dao.findForList("HExportMapper.selectH10000FuturesData", queryParam);
        if(H10000futuresDataList == null) {
            H10000futuresDataList = new ArrayList<>(); 
        }
        
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> H10000futuresGoldDataList = (List<Map<String,Object>>)this.dao.findForList("HExportMapper.selectH10000FuturesGoldData", queryParam);
        if(H10000futuresGoldDataList == null) {
            H10000futuresGoldDataList = new ArrayList<>(); 
        }
        
        @SuppressWarnings("unchecked")
        Map<String,Object> H10000futuresSumData = (Map<String,Object>)this.dao.findForObject("HExportMapper.selectH10000FuturesSumDataForReport", queryParam);
        if(H10000futuresSumData == null) {
            H10000futuresSumData = new HashMap<>(); 
        }
        @SuppressWarnings("unchecked")
        Map<String,Object> H10000futuresGoldSumData = (Map<String,Object>)this.dao.findForObject("HExportMapper.selectH10000FuturesGoldSumDataForReport", queryParam);
        if(H10000futuresGoldSumData == null) {
            H10000futuresGoldSumData = new HashMap<>(); 
        }
        
        Object futuresdataSumCheck = this.dao.findForObject("HExportMapper.checkIfH10000FuturesHasDataForReport", queryParam);
        Object goldfuturesdataSumCheck = this.dao.findForObject("HExportMapper.checkIfH10000FuturesGoldHasDataForReport", queryParam);
        
        futures.put("list", H10000futuresDataList);
        futures.put("sum", H10000futuresSumData);
        futures.put("dataSumCheck", futuresdataSumCheck == null ? 0d : futuresdataSumCheck);
        goldfutures.put("list", H10000futuresGoldDataList);
        goldfutures.put("sum", H10000futuresGoldSumData);
        goldfutures.put("dataSumCheck", goldfuturesdataSumCheck == null ? 0d : goldfuturesdataSumCheck);
        H10000.put("futures", futures);
        H10000.put("goldfutures", goldfutures);
        //--------------------↑H10000.futures↑--------------------
        //--------------------↓H10000.rmcfs↓--------------------
        Map<String, Object> rmcfs = new HashMap<>();
        item1 = new HashMap<>();
        item2 = new HashMap<>();
        
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
        Map<String, Object> fairValues = new HashMap<>();
        
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
        
        
        //--------------------↓H10000.three_level_change↓--------------------  
        /*
            * 第三层次公允价值余额及变动情况
            * @author chenhy irenewu
            *20220623
        */ 

        //20220628 irenewu修改  

        Map<String, Object> threelevelchange = new HashMap<>();
        Map<String,Object> BOND = new HashMap<>();
        Map<String,Object> STOCK = new HashMap<>();
        Map<String,Object> REPO = new HashMap<>();
        Map<String,Object> WARRANT = new HashMap<>();
        Map<String,Object> FUND = new HashMap<>();
        Map<String,Object> TOTAL = new HashMap<>();
        item1 = new HashMap<>();
        item2 = new HashMap<>();
        item3 = new HashMap<>();
        item4 = new HashMap<>();
        item5 = new HashMap<>();
        item6 = new HashMap<>();
        item7 = new HashMap<>();
        item8 = new HashMap<>();
        item9 = new HashMap<>();
        item10 = new HashMap<>();

        @SuppressWarnings("unchecked")
        List<Map<String,Object>> H10000ThreeLevelChangeDataList = (List<Map<String,Object>>)this.dao.findForList("HExportMapper.selectH10000ThreeLevelChangeData", queryParam);
        if(H10000ThreeLevelChangeDataList == null) {
            H10000ThreeLevelChangeDataList = new ArrayList<>();
        }     
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> H10000ThreeLevelChangeDataTypeHasDataList = (List<Map<String,Object>>)this.dao.findForList("HExportMapper.checkIfH10000ThreeLevelChangeHasDataForWP", queryParam);
        if(H10000ThreeLevelChangeDataTypeHasDataList == null) {
            H10000ThreeLevelChangeDataTypeHasDataList = new ArrayList<>();
        }

        for(Map<String,Object> map : H10000ThreeLevelChangeDataTypeHasDataList) {
            if("BOND".equals(map.get("TYPE"))) {
                item1 = map;
            }else if("STOCK".equals(map.get("TYPE"))){
                item2 = map;
            }else if("REPO".equals(map.get("TYPE"))){
                item3 = map;
            }else if("WARRANT".equals(map.get("TYPE"))){
                item4 = map;
            }else if("FUND".equals(map.get("TYPE"))){
                item5 = map;
            }else if("TOTAL".equals(map.get("TYPE"))){
                item6 = map;
            }
        }
        BOND.put("sum",item1);
        STOCK.put("sum", item2);
        REPO.put("sum", item3);
        WARRANT.put("sum", item4);
        FUND.put("sum", item5);
        TOTAL.put("sum", item6);
        
        //20220628 irenewu修改  
        item1 = new HashMap<>();
        item2 = new HashMap<>();
        item3 = new HashMap<>();
        item4 = new HashMap<>();
        item5 = new HashMap<>();
        item6 = new HashMap<>();
        item7 = new HashMap<>();
        item8 = new HashMap<>();
        item9 = new HashMap<>();
        item10 = new HashMap<>();
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> H10000ThreeLevelChangeBondDataList = (List<Map<String,Object>>)this.dao.findForList("HExportMapper.selectH10000ThreeLevelChangeBondData", queryParam);
        if(H10000ThreeLevelChangeBondDataList == null) {
            H10000ThreeLevelChangeBondDataList = new ArrayList<>();
        }     
        for(Map<String,Object> map : H10000ThreeLevelChangeBondDataList) {
            if("1".equals(map.get("SORT"))) {
                item1 = map;
            }else if("2".equals(map.get("SORT"))){
                item2 = map;
            }else if("3".equals(map.get("SORT"))){
                item3 = map;
            }else if("4".equals(map.get("SORT"))){
                item4 = map;
            }else if("5".equals(map.get("SORT"))){
                item5 = map;
            }else if("6".equals(map.get("SORT"))){
                item6 = map;
            }else if("7".equals(map.get("SORT"))){
                item7 = map;
            }else if("8".equals(map.get("SORT"))){
                item8 = map;
            }else if("9".equals(map.get("SORT"))){
                item9 = map;
            }else if("10".equals(map.get("SORT"))){
                item10 = map;
            }
        }
        BOND.put("item1",item1);
        BOND.put("item2",item2);
        BOND.put("item3",item3);
        BOND.put("item4",item4);
        BOND.put("item5",item5);
        BOND.put("item6",item6);
        BOND.put("item7",item7);
        BOND.put("item8",item8);
        BOND.put("item9",item9);
        BOND.put("item10",item10);
        
        item1 = new HashMap<>();
        item2 = new HashMap<>();
        item3 = new HashMap<>();
        item4 = new HashMap<>();
        item5 = new HashMap<>();
        item6 = new HashMap<>();
        item7 = new HashMap<>();
        item8 = new HashMap<>();
        item9 = new HashMap<>();
        item10 = new HashMap<>();
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> H10000ThreeLevelChangeStockDataList = (List<Map<String,Object>>)this.dao.findForList("HExportMapper.selectH10000ThreeLevelChangeStockData", queryParam);
        if(H10000ThreeLevelChangeStockDataList == null) {
            H10000ThreeLevelChangeStockDataList = new ArrayList<>();
        }     
        for(Map<String,Object> map : H10000ThreeLevelChangeStockDataList) {
            if("1".equals(map.get("SORT"))) {
                item1 = map;
            }else if("2".equals(map.get("SORT"))){
                item2 = map;
            }else if("3".equals(map.get("SORT"))){
                item3 = map;
            }else if("4".equals(map.get("SORT"))){
                item4 = map;
            }else if("5".equals(map.get("SORT"))){
                item5 = map;
            }else if("6".equals(map.get("SORT"))){
                item6 = map;
            }else if("7".equals(map.get("SORT"))){
                item7 = map;
            }else if("8".equals(map.get("SORT"))){
                item8 = map;
            }else if("9".equals(map.get("SORT"))){
                item9 = map;
            }else if("10".equals(map.get("SORT"))){
                item10 = map;
            }
        }
        STOCK.put("item1",item1);
        STOCK.put("item2",item2);
        STOCK.put("item3",item3);
        STOCK.put("item4",item4);
        STOCK.put("item5",item5);
        STOCK.put("item6",item6);
        STOCK.put("item7",item7);
        STOCK.put("item8",item8);
        STOCK.put("item9",item9);
        STOCK.put("item10",item10);
        
        item1 = new HashMap<>();
        item2 = new HashMap<>();
        item3 = new HashMap<>();
        item4 = new HashMap<>();
        item5 = new HashMap<>();
        item6 = new HashMap<>();
        item7 = new HashMap<>();
        item8 = new HashMap<>();
        item9 = new HashMap<>();
        item10 = new HashMap<>();
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> H10000ThreeLevelChangeRepoDataList = (List<Map<String,Object>>)this.dao.findForList("HExportMapper.selectH10000ThreeLevelChangeRepoData", queryParam);
        if(H10000ThreeLevelChangeRepoDataList == null) {
            H10000ThreeLevelChangeRepoDataList = new ArrayList<>();
        }     
        for(Map<String,Object> map : H10000ThreeLevelChangeRepoDataList) {
            if("1".equals(map.get("SORT"))) {
                item1 = map;
            }else if("2".equals(map.get("SORT"))){
                item2 = map;
            }else if("3".equals(map.get("SORT"))){
                item3 = map;
            }else if("4".equals(map.get("SORT"))){
                item4 = map;
            }else if("5".equals(map.get("SORT"))){
                item5 = map;
            }else if("6".equals(map.get("SORT"))){
                item6 = map;
            }else if("7".equals(map.get("SORT"))){
                item7 = map;
            }else if("8".equals(map.get("SORT"))){
                item8 = map;
            }else if("9".equals(map.get("SORT"))){
                item9 = map;
            }else if("10".equals(map.get("SORT"))){
                item10 = map;
            }
        }
        REPO.put("item1",item1);
        REPO.put("item2",item2);
        REPO.put("item3",item3);
        REPO.put("item4",item4);
        REPO.put("item5",item5);
        REPO.put("item6",item6);
        REPO.put("item7",item7);
        REPO.put("item8",item8);
        REPO.put("item9",item9);
        REPO.put("item10",item10);

        item1 = new HashMap<>();
        item2 = new HashMap<>();
        item3 = new HashMap<>();
        item4 = new HashMap<>();
        item5 = new HashMap<>();
        item6 = new HashMap<>();
        item7 = new HashMap<>();
        item8 = new HashMap<>();
        item9 = new HashMap<>();
        item10 = new HashMap<>();
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> H10000ThreeLevelChangeWarrantDataList = (List<Map<String,Object>>)this.dao.findForList("HExportMapper.selectH10000ThreeLevelChangeWarrantData", queryParam);
        if(H10000ThreeLevelChangeWarrantDataList == null) {
            H10000ThreeLevelChangeWarrantDataList = new ArrayList<>();
        }     
        for(Map<String,Object> map : H10000ThreeLevelChangeWarrantDataList) {
            if("1".equals(map.get("SORT"))) {
                item1 = map;
            }else if("2".equals(map.get("SORT"))){
                item2 = map;
            }else if("3".equals(map.get("SORT"))){
                item3 = map;
            }else if("4".equals(map.get("SORT"))){
                item4 = map;
            }else if("5".equals(map.get("SORT"))){
                item5 = map;
            }else if("6".equals(map.get("SORT"))){
                item6 = map;
            }else if("7".equals(map.get("SORT"))){
                item7 = map;
            }else if("8".equals(map.get("SORT"))){
                item8 = map;
            }else if("9".equals(map.get("SORT"))){
                item9 = map;
            }else if("10".equals(map.get("SORT"))){
                item10 = map;
            }
        }
        WARRANT.put("item1",item1);
        WARRANT.put("item2",item2);
        WARRANT.put("item3",item3);
        WARRANT.put("item4",item4);
        WARRANT.put("item5",item5);
        WARRANT.put("item6",item6);
        WARRANT.put("item7",item7);
        WARRANT.put("item8",item8);
        WARRANT.put("item9",item9);
        WARRANT.put("item10",item10);
        
        item1 = new HashMap<>();
        item2 = new HashMap<>();
        item3 = new HashMap<>();
        item4 = new HashMap<>();
        item5 = new HashMap<>();
        item6 = new HashMap<>();
        item7 = new HashMap<>();
        item8 = new HashMap<>();
        item9 = new HashMap<>();
        item10 = new HashMap<>();
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> H10000ThreeLevelChangeFundDataList = (List<Map<String,Object>>)this.dao.findForList("HExportMapper.selectH10000ThreeLevelChangeFundData", queryParam);
        if(H10000ThreeLevelChangeFundDataList == null) {
            H10000ThreeLevelChangeFundDataList = new ArrayList<>();
        }     
        for(Map<String,Object> map : H10000ThreeLevelChangeFundDataList) {
            if("1".equals(map.get("SORT"))) {
                item1 = map;
            }else if("2".equals(map.get("SORT"))){
                item2 = map;
            }else if("3".equals(map.get("SORT"))){
                item3 = map;
            }else if("4".equals(map.get("SORT"))){
                item4 = map;
            }else if("5".equals(map.get("SORT"))){
                item5 = map;
            }else if("6".equals(map.get("SORT"))){
                item6 = map;
            }else if("7".equals(map.get("SORT"))){
                item7 = map;
            }else if("8".equals(map.get("SORT"))){
                item8 = map;
            }else if("9".equals(map.get("SORT"))){
                item9 = map;
            }else if("10".equals(map.get("SORT"))){
                item10 = map;
            }
        }
        FUND.put("item1",item1);
        FUND.put("item2",item2);
        FUND.put("item3",item3);
        FUND.put("item4",item4);
        FUND.put("item5",item5);
        FUND.put("item6",item6);
        FUND.put("item7",item7);
        FUND.put("item8",item8);
        FUND.put("item9",item9);
        FUND.put("item10",item10);

        item1 = new HashMap<>();
        item2 = new HashMap<>();
        item3 = new HashMap<>();
        item4 = new HashMap<>();
        item5 = new HashMap<>();
        item6 = new HashMap<>();
        item7 = new HashMap<>();
        item8 = new HashMap<>();
        item9 = new HashMap<>();
        item10 = new HashMap<>();
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> H10000ThreeLevelChangeTotalDataList = (List<Map<String,Object>>)this.dao.findForList("HExportMapper.selectH10000ThreeLevelChangeTotalData", queryParam);
        if(H10000ThreeLevelChangeTotalDataList == null) {
            H10000ThreeLevelChangeTotalDataList = new ArrayList<>();
        }     
        for(Map<String,Object> map : H10000ThreeLevelChangeTotalDataList) {
            if("1".equals(map.get("SORT"))) {
                item1 = map;
            }else if("2".equals(map.get("SORT"))){
                item2 = map;
            }else if("3".equals(map.get("SORT"))){
                item3 = map;
            }else if("4".equals(map.get("SORT"))){
                item4 = map;
            }else if("5".equals(map.get("SORT"))){
                item5 = map;
            }else if("6".equals(map.get("SORT"))){
                item6 = map;
            }else if("7".equals(map.get("SORT"))){
                item7 = map;
            }else if("8".equals(map.get("SORT"))){
                item8 = map;
            }else if("9".equals(map.get("SORT"))){
                item9 = map;
            }else if("10".equals(map.get("SORT"))){
                item10 = map;
            }
        }
        TOTAL.put("item1",item1);
        TOTAL.put("item2",item2);
        TOTAL.put("item3",item3);
        TOTAL.put("item4",item4);
        TOTAL.put("item5",item5);
        TOTAL.put("item6",item6);
        TOTAL.put("item7",item7);
        TOTAL.put("item8",item8);
        TOTAL.put("item9",item9);
        TOTAL.put("item10",item10);

        @SuppressWarnings("unchecked")
        Map<String,Object> H10000ThreeLevelChangeSumData = (Map<String,Object>)this.dao.findForObject("HExportMapper.selectH10000ThreeLevelChangeSumDataForReport", queryParam);
        if(H10000ThreeLevelChangeSumData == null) {
            H10000ThreeLevelChangeSumData = new HashMap<>(); 
        }
        
        threelevelchange.put("list", H10000ThreeLevelChangeDataList);
        threelevelchange.put("count", H10000ThreeLevelChangeDataList.size());
        threelevelchange.put("sum", H10000ThreeLevelChangeSumData);
        threelevelchange.put("BOND",BOND);
        threelevelchange.put("STOCK", STOCK);
        threelevelchange.put("REPO", REPO);
        threelevelchange.put("WARRANT", WARRANT);
        threelevelchange.put("FUND", FUND);
        threelevelchange.put("TOTAL", TOTAL);
        dataSumCheck = this.dao.findForObject("HExportMapper.checkIfH10000ThreeLevelChangeHasDataForReport", queryParam);
        threelevelchange.put("dataSumCheck", dataSumCheck == null ? 0d : dataSumCheck);
        H10000.put("threelevelchange", threelevelchange);

        //--------------------↑H10000.three_level_change↑--------------------
        //--------------------↓H10000.three_level_measurement_of_unobservable_input_values↓-------------------- 
        Map<String, Object> threeLevelMeasure0fUnobservableInput = new HashMap<>();

        Object weightedAvg = this.dao.findForObject("HExportMapper.selecteylomdthreeLevelWeightValues", queryParam);
        Object weightedAvgLast = this.dao.findForObject("HExportMapper.selecteylomdthreeLevelWeightValuesLast", queryParamLast);

        threeLevelMeasure0fUnobservableInput.put("weightedAvg", weightedAvg);
        threeLevelMeasure0fUnobservableInput.put("weightedAvgLast", weightedAvgLast);

        H10000.put("threeLevelMeasure0fUnobservableInput", threeLevelMeasure0fUnobservableInput);

        //--------------------↑H10000.three_level_measurement_of_unobservable_input_values↑--------------------  
        //--------------------↓H10000.fi_not_meaure_with_fv↓-------------------- 
        Map<String, Object> fiNotMeasureWithFv = new HashMap<>();

        Object fiNotMeasureWithFvNoteLast = this.dao.findForObject("HExportMapper.selectfiNotMeasureWithFv", queryParamLast);

        fiNotMeasureWithFv.put("lastNote", fiNotMeasureWithFvNoteLast);

        H10000.put("fiNotMeasureWithFv", fiNotMeasureWithFv);

        //--------------------↑H10000.three_level_measurement_of_unobservable_input_values↑--------------------  
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
        //--------------------↓H800.Main↓--------------------
        Map<String,Object> Main = new HashMap<>();
        item1 = new HashMap<String,Object>();
        item2 = new HashMap<String,Object>();
        item3 = new HashMap<String,Object>();
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> H800MainDataList = (List<Map<String,Object>>)this.dao.findForList("HExportMapper.selectH800MainData", queryParam);
        if(H800MainDataList == null) {
            H800MainDataList = new ArrayList<>();
        }
        for(Map<String,Object> map : H800MainDataList) {
            if("上交所".equals(map.get("market"))) {
                item1 = map;
            }else if("深交所".equals(map.get("market"))) {
                item2 = map;
            }else if("银行间".equals(map.get("market"))) {
                item3 = map;
            }
        }
        @SuppressWarnings("unchecked")
        Map<String,Object> H800MainSumData = (Map<String,Object>)this.dao.findForObject("HExportMapper.selectH800MainSumData", queryParam);
        if(H800MainSumData == null) {
            H800MainSumData = new HashMap<>(); 
        }

        Map<String,Object> exchange = new HashMap<>();
        exchange.put("endBalance", this.addNumber(item1.get("endBalance"), item2.get("endBalance")));
        exchange.put("beginBalance", this.addNumber(item1.get("beginBalance"), item2.get("beginBalance")));
        Main.put("exchange", exchange);
        Main.put("bank", item3);
        Main.put("sum", H800MainSumData);
        H800.put("Main", Main);
        //--------------------↑H800.Main↑--------------------
        //====================↑H800↑====================
        
        //====================↓E300↓====================
        Map<String, Object> E300 = new HashMap<>();
        //--------------------↓E300.disc↓--------------------
        Map<String, Object> disc = new HashMap<>();
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

            //chenhy,20240621,判断是否为新增分级
            Map<String,Object> levelQueryMap2 = new HashMap<>();
            levelQueryMap2.put("fundId", queryParam.get("fundId"));
            levelQueryMap2.put("level", levelName);
            levelQueryMap2.put("period", queryParam.get("period"));
            @SuppressWarnings("unchecked")
            List<Map<String,Object>> newLevelList = (List<Map<String,Object>>)this.dao.findForList("TExportMapper.selectNewLevelData", levelQueryMap2);
            if(newLevelList == null) {
                newLevelList = new ArrayList<>();
            }
            level.put("newLevelList", newLevelList);
            level.put("newLevelCount", newLevelList.size());

            levels1.add(level);

        }
        T10000.put("levels", levels1);
        T10000.put("levelCount", levels1.size());
        //====================↑T10000↑====================
        
        //====================20221220irene新增====================
        //====================↓T10000forP↓====================
        Map<String, Object> T10000forP = new HashMap<>();
        Map<String, Object> Current = new HashMap<>();
        Map<String, Object> Last = new HashMap<>();
        item1 = new HashMap<String,Object>();
        item2 = new HashMap<String,Object>();
        item3 = new HashMap<String,Object>();
        item4 = new HashMap<String,Object>();
        
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> T10000forPCurrentDataList = (List<Map<String,Object>>)this.dao.findForList("TExportMapper.selectT10000DataForReport", queryParam);
        if(T10000DataList == null) {
            T10000DataList = new ArrayList<>();
        }
        for(Map<String,Object> map : T10000forPCurrentDataList) {
            if("上年度末".equals(map.get("item"))) {
                item1 = map;
            }else if("本期申购".equals(map.get("item"))) {
                item2 = map;
            }else if("本期赎回（以\"-\"填列）".equals(map.get("item"))) {
                item3 = map;
            }else if("本期末".equals(map.get("item"))) {
                item4 = map;
            }
        }
        Current.put("item1", item1);
        Current.put("item2", item2);
        Current.put("item3", item3);
        Current.put("item4", item4);

        item1 = new HashMap<String,Object>();
        item2 = new HashMap<String,Object>();
        item3 = new HashMap<String,Object>();
        item4 = new HashMap<String,Object>();

        @SuppressWarnings("unchecked")
        List<Map<String,Object>> T10000forPLastDataList = (List<Map<String,Object>>)this.dao.findForList("TExportMapper.selectT10000DataForReport", queryParamLast);
        if(T10000DataList == null) {
            T10000DataList = new ArrayList<>();
        }
        for(Map<String,Object> map : T10000forPLastDataList) {
            if("上年度末".equals(map.get("item"))) {
                item1 = map;
            }else if("本期申购".equals(map.get("item"))) {
                item2 = map;
            }else if("本期赎回（以\"-\"填列）".equals(map.get("item"))) {
                item3 = map;
            }else if("本期末".equals(map.get("item"))) {
                item4 = map;
            }
        }
        Last.put("item1", item1);
        Last.put("item2", item2);
        Last.put("item3", item3);
        Last.put("item4", item4);

        T10000forP.put("Current", Current);
        T10000forP.put("Last", Last);
        //====================↑T10000↑====================

        //====================↓T10000_Note↓====================
        // yury,20200907,新增实收基金下注释
        Map<String, Object> T10000Note = new HashMap<>();
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> t10000NoteList = (List<Map<String, Object>>)this.dao.findForList("TExportMapper.selectT10000Note", queryParam);
        if (t10000NoteList == null){
            t10000NoteList = new ArrayList<>();
        }
        int count = t10000NoteList.size();
        T10000Note.put("count", count);
        T10000Note.put("list", t10000NoteList);
        T10000.put("T10000Note", T10000Note);
        //====================↑T10000_Note↑====================

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
        //====================20221220irene新增====================
        //====================↓T10000forP↓====================
        Map<String, Object> T11000forP = new HashMap<>();
        Current = new HashMap<>();
        Last = new HashMap<>();
        
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> T11000forPCurrentDataList = (List<Map<String,Object>>)this.dao.findForList("TExportMapper.selectT11000P4104Data", queryParam);
        if(T11000forPCurrentDataList == null) {
            T11000forPCurrentDataList = new ArrayList<>();
        }
        for(Map<String,Object> map : T11000forPCurrentDataList) {
            if("未分配利润合计".equals(map.get("type"))) {
                Current = map;
            }
        }

        @SuppressWarnings("unchecked")
        List<Map<String,Object>> T11000forPLastDataList = (List<Map<String,Object>>)this.dao.findForList("TExportMapper.selectT11000P4104Data", queryParamLast);
        if(T11000forPLastDataList == null) {
            T11000forPLastDataList = new ArrayList<>();
        }
        for(Map<String,Object> map : T11000forPLastDataList) {
            if("未分配利润合计".equals(map.get("type"))) {
                Last = map;
            }
        }

        T11000forP.put("Current", Current);
        T11000forP.put("Last", Last);
        //====================↑T10000↑====================
        
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
        
		Map<String, Object> STOCKS_ALL = new HashMap<>();
        Map<String, Object> STOCKS = new HashMap<>();
        Map<String, Object> STOCKS_BS = new HashMap<>();
        Map<String, Object> STOCKS_R = new HashMap<>();
        Map<String, Object> STOCKS_P = new HashMap<>();
        Map<String, Object> STOCKS_L = new HashMap<>(); // yury，20200907，新增报告7.4.7.12.5证券出借差价收入
        FUND = new HashMap<>();
		Map<String, Object> BOND_ALL = new HashMap<>();
        BOND = new HashMap<>();
        Map<String, Object> BOND_BS = new HashMap<>();
        Map<String, Object> BOND_R = new HashMap<>();
        Map<String, Object> BOND_P = new HashMap<>();
		Map<String, Object> ABS_ALL = new HashMap<>();
		Map<String, Object> ABS = new HashMap<>();
        Map<String, Object> ABS_BS = new HashMap<>();
		Map<String, Object> ABS_R = new HashMap<>();
		Map<String, Object> ABS_P = new HashMap<>();
		Map<String, Object> GOLD_ALL = new HashMap<>();
		Map<String, Object> GOLD = new HashMap<>();
        Map<String, Object> GOLD_BS = new HashMap<>();
        Map<String, Object> GOLD_R = new HashMap<>();
        Map<String, Object> GOLD_P = new HashMap<>();
        Map<String, Object> DI_WARRANT = new HashMap<>();
        Map<String, Object> DI_OTHER = new HashMap<>();
        
        queryParam.put("reportFlag", "Y");
		
		queryParam.put("type", "STOCKS_ALL");
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> U10000StocksDataList = (List<Map<String,Object>>)this.dao.findForList("UExportMapper.selectU10000SumImportData", queryParam);
        if(U10000StocksDataList == null) {
            U10000StocksDataList = new ArrayList<>(); 
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
		
        queryParam.put("type", "STOCKS_BS");
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> U10000StocksBsDataList = (List<Map<String,Object>>)this.dao.findForList("UExportMapper.selectU10000ImportData", queryParam);
        if(U10000StocksBsDataList == null) {
            U10000StocksBsDataList = new ArrayList<>(); 
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
		
        queryParam.put("type", "STOCKS_L");
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> U10000StocksLDataList = (List<Map<String,Object>>)this.dao.findForList("UExportMapper.selectU10000ImportData", queryParam);
        if(U10000StocksLDataList == null) {
            U10000StocksLDataList = new ArrayList<>();
        }

        queryParam.put("type", "FUND");
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> U10000FUNDDataList = (List<Map<String,Object>>)this.dao.findForList("UExportMapper.selectU10000ImportData", queryParam);
        if(U10000FUNDDataList == null) {
            U10000FUNDDataList = new ArrayList<>(); 
        }

		queryParam.put("type", "BOND_ALL");
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> U10000BondDataList = (List<Map<String,Object>>)this.dao.findForList("UExportMapper.selectU10000SumImportData", queryParam);
        if(U10000BondDataList == null) {
            U10000BondDataList = new ArrayList<>(); 
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
		
        queryParam.put("type", "BOND_BS");
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> U10000BondBsDataList = (List<Map<String,Object>>)this.dao.findForList("UExportMapper.selectU10000ImportData", queryParam);
        if(U10000BondBsDataList == null) {
            U10000BondBsDataList = new ArrayList<>(); 
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


		queryParam.put("type", "ABS_ALL");
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> U10000AbsDataList = (List<Map<String,Object>>)this.dao.findForList("UExportMapper.selectU10000SumImportData", queryParam);
        if(U10000AbsDataList == null) {
            U10000AbsDataList = new ArrayList<>(); 
        }

        queryParam.put("type", "ABS");
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> U10000ABSSummaryDataList = (List<Map<String,Object>>)this.dao.findForList("UExportMapper.selectU10000ImportSummaryData", queryParam);
        if(U10000ABSSummaryDataList == null) {
            U10000ABSSummaryDataList = new ArrayList<>(); 
        }
		if(U10000ABSSummaryDataList.size() != 0) {
            @SuppressWarnings("unchecked")
            Map<String,Object> U10000ABSSummarySumData = (Map<String,Object>)this.dao.findForObject("UExportMapper.selectU10000ImportSummarySumDataForReport", queryParam);
            if(U10000ABSSummarySumData == null) {
                U10000ABSSummarySumData = new HashMap<>();
            }
            U10000ABSSummaryDataList.add(U10000ABSSummarySumData);
        }

		queryParam.put("type", "ABS_BS");
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> U10000ABSBSDataList = (List<Map<String,Object>>)this.dao.findForList("UExportMapper.selectU10000ImportData", queryParam);
        if(U10000ABSBSDataList == null) {
            U10000ABSBSDataList = new ArrayList<>(); 
        }

		queryParam.put("type", "ABS_R");
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> U10000ABSRDataList = (List<Map<String,Object>>)this.dao.findForList("UExportMapper.selectU10000ImportData", queryParam);
        if(U10000ABSRDataList == null) {
            U10000ABSRDataList = new ArrayList<>(); 
        }

		queryParam.put("type", "ABS_P");
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> U10000ABSPDataList = (List<Map<String,Object>>)this.dao.findForList("UExportMapper.selectU10000ImportData", queryParam);
        if(U10000ABSPDataList == null) {
            U10000ABSPDataList = new ArrayList<>(); 
        }

		queryParam.put("type", "GOLD_ALL");
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> U10000GoldDataList = (List<Map<String,Object>>)this.dao.findForList("UExportMapper.selectU10000SumImportData", queryParam);
        if(U10000GoldDataList == null) {
            U10000GoldDataList = new ArrayList<>(); 
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
		
        queryParam.put("type", "GOLD_BS");
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> U10000GoldBsDataList = (List<Map<String,Object>>)this.dao.findForList("UExportMapper.selectU10000ImportData", queryParam);
        if(U10000GoldBsDataList == null) {
            U10000GoldBsDataList = new ArrayList<>(); 
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

		
        STOCKS_ALL.put("list", U10000StocksDataList);
        STOCKS_ALL.put("count", U10000StocksDataList.size());
		
        STOCKS.put("list", U10000StocksSummaryDataList);
        STOCKS.put("count", U10000StocksSummaryDataList.size());
      
        STOCKS_BS.put("list", U10000StocksBsDataList);
        STOCKS_BS.put("count", U10000StocksBsDataList.size());
        
        STOCKS_R.put("list", U10000StocksRDataList);
        STOCKS_R.put("count", U10000StocksRDataList.size());
        
        STOCKS_P.put("list", U10000StocksPDataList);
        STOCKS_P.put("count", U10000StocksPDataList.size());

        STOCKS_L.put("list", U10000StocksLDataList);
        STOCKS_L.put("count", U10000StocksLDataList.size());

        FUND.put("list", U10000FUNDDataList);
        FUND.put("count", U10000FUNDDataList.size());
		
        BOND_ALL.put("list", U10000BondDataList);
        BOND_ALL.put("count", U10000BondDataList.size());
		
        BOND.put("list", U10000BondSummaryDataList);
        BOND.put("count", U10000BondSummaryDataList.size());

        BOND_BS.put("list", U10000BondBsDataList);
        BOND_BS.put("count", U10000BondBsDataList.size());
        
        BOND_R.put("list", U10000BondRDataList);
        BOND_R.put("count", U10000BondRDataList.size());
        
        BOND_P.put("list", U10000BondPDataList);
        BOND_P.put("count", U10000BondPDataList.size());
		
        ABS_ALL.put("list", U10000AbsDataList);
        ABS_ALL.put("count", U10000AbsDataList.size());
        
        ABS.put("list", U10000ABSSummaryDataList);
        ABS.put("count", U10000ABSSummaryDataList.size());
		
		ABS_BS.put("list", U10000ABSBSDataList);
        ABS_BS.put("count", U10000ABSBSDataList.size());
		
		ABS_R.put("list", U10000ABSRDataList);
        ABS_R.put("count", U10000ABSRDataList.size());
		
		ABS_P.put("list", U10000ABSPDataList);
        ABS_P.put("count", U10000ABSPDataList.size());
		
        GOLD_ALL.put("list", U10000GoldDataList);
        GOLD_ALL.put("count", U10000GoldDataList.size());
		
        GOLD.put("list", U10000GoldSummaryDataList);
        GOLD.put("count", U10000GoldSummaryDataList.size());
		
        GOLD_BS.put("list", U10000GoldBsDataList);
        GOLD_BS.put("count", U10000GoldBsDataList.size());
        
        GOLD_R.put("list", U10000GoldRDataList);
        GOLD_R.put("count", U10000GoldRDataList.size());
        
        GOLD_P.put("list", U10000GoldPDataList);
        GOLD_P.put("count", U10000GoldPDataList.size());
        
        DI_WARRANT.put("list", U10000diWarrantDataList);
        DI_WARRANT.put("count", U10000diWarrantDataList.size());
        
        DI_OTHER.put("list", U10000diOtherDataList);
        DI_OTHER.put("count", U10000diOtherDataList.size());

        importData.put("STOCKS_ALL", STOCKS_ALL); 
		importData.put("STOCKS", STOCKS);         
        importData.put("STOCKS_BS", STOCKS_BS);
        importData.put("STOCKS_R", STOCKS_R);
        importData.put("STOCKS_P", STOCKS_P);
        importData.put("STOCKS_L", STOCKS_L);
        importData.put("FUND", FUND);
        importData.put("BOND_ALL", BOND_ALL);		
		importData.put("BOND", BOND);  
        importData.put("BOND_BS", BOND_BS);
        importData.put("BOND_R", BOND_R);
        importData.put("BOND_P", BOND_P);
        importData.put("ABS_ALL", ABS_ALL);
		importData.put("ABS", ABS);  
		importData.put("ABS_BS", ABS_BS);
		importData.put("ABS_R", ABS_R);
		importData.put("ABS_P", ABS_P);
        importData.put("GOLD_ALL", GOLD_ALL);
		importData.put("GOLD", GOLD);  
        importData.put("GOLD_BS", GOLD_BS);
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
        List<Map<String,Object>> U10000TrxFeeMetaDataList = (List<Map<String,Object>>)this.dao.findForList("UExportMapper.selectU10000TrxFeeData", queryParam);
        if(U10000TrxFeeMetaDataList == null) {
        	U10000TrxFeeMetaDataList = new ArrayList<>(); 
        }
        
        @SuppressWarnings("unchecked")
        Map<String,Object> U10000TrxFeeSumData = (Map<String,Object>)this.dao.findForObject("UExportMapper.selectU10000TrxFeeSumDataForReport", queryParam);
        if(U10000TrxFeeSumData == null) {
            U10000TrxFeeSumData = new HashMap<>();
        }
        U10000TrxFeeMetaDataList.add(U10000TrxFeeSumData);
        trxFee.put("list", U10000TrxFeeMetaDataList);
        trxFee.put("count", U10000TrxFeeMetaDataList.size());
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
		//--------------------↓U10000.credit↓--------------------
        Map<String,Object> credit = new HashMap<>();
        credit.put("S1", new HashMap<>());
        credit.put("S2", new HashMap<>());
        credit.put("S3", new HashMap<>());
		credit.put("S4", new HashMap<>());
		credit.put("S5", new HashMap<>());
        
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> U10000CreditDataList = (List<Map<String,Object>>)this.dao.findForList("UExportMapper.selectU10000CreditData", queryParam);
        if(U10000CreditDataList == null) {
            U10000CreditDataList = new ArrayList<>(); 
        }

        for(Map<String,Object> map : U10000CreditDataList) {
            if("银行存款".equals(map.get("item"))) {
                credit.put("S1", map);
            }else if("买入返售金融资产".equals(map.get("item"))) {
                credit.put("S2", map);
            }else if("债权投资".equals(map.get("item"))) {
                credit.put("S3", map);
			}else if("其他债权投资".equals(map.get("item"))) {
                credit.put("S4", map);
			}else if("其他".equals(map.get("item"))) {
                credit.put("S5", map);
			}
		}

        
        @SuppressWarnings("unchecked")
        Map<String,Object> U10000CreditSumData = (Map<String,Object>)this.dao.findForObject("UExportMapper.selectU10000CreditSumData", queryParam);
        if(U10000CreditSumData == null) {
            U10000CreditSumData = new HashMap<>();
        }
        U10000CreditDataList.add(U10000CreditSumData);
        credit.put("list", U10000CreditDataList); 
        credit.put("count", U10000CreditDataList.size());
        dataSumCheck = this.dao.findForObject("UExportMapper.checkIfU10000CreditHasDataForReport", queryParam);
        credit.put("dataSumCheck", dataSumCheck == null ? 0d : dataSumCheck);
        //--------------------↑U10000.credit↑-------------------- 
        //--------------------↓U10000.credit_note↓--------------------
        Map<String,Object> credit_note = new HashMap<>();


        @SuppressWarnings("unchecked")
        Map<String,Object> U10000CreditNoteData = (Map<String,Object>)this.dao.findForObject("UExportMapper.selectU10000CreditNoteData", queryParamLast);
        if(U10000CreditNoteData == null) {
            U10000CreditNoteData = new HashMap<>(); 
        }

        credit_note.put("credit_note", U10000CreditNoteData);
        dataSumCheck = this.dao.findForObject("UExportMapper.checkIfU10000CreditNoteDataForReport", queryParamLast);
        credit_note.put("dataSumCheck",dataSumCheck);

        //--------------------↑U10000.credit_note↑--------------------       
        U10000.put("interest", interest);
        U10000.put("importData", importData);
        U10000.put("dividend", dividend);
        U10000.put("other_r", other_r);
        U10000.put("trxFee", trxFee);
        U10000.put("other_c", other_c);
		U10000.put("credit", credit);
        U10000.put("credit_note", credit_note);
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
        result.put("T10000forP", T10000forP);
        result.put("T11000forP", T11000forP);
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


        //--------------------↓T11000Words↓--------------------
        // yury，20200908，新增日后事项文字段
        Map<String, Object> T11000Words = new HashMap<>();
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> T11000WordsList = (List<Map<String, Object>>)this.dao.findForList("TExportMapper.selectT11000Words", queryParam);
        if (T11000WordsList == null){
            T11000WordsList = new ArrayList<>();
        }
        T11000Words.put("count", T11000WordsList.size());
        T11000Words.put("list", T11000WordsList);
        T11000.put("T11000Words", T11000Words);
        //--------------------↑T11000Words↑--------------------

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
        Map<String, Object> transaction = new HashMap<>();
        Map<String, Object> stock = new HashMap<>();
        Map<String, Object> bond = new HashMap<>();
        Map<String, Object> warrant = new HashMap<>();
        Map<String, Object> repo = new HashMap<>();
        Map<String, Object> fund = new HashMap<>();
        
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
        transaction.put("count", transaction.size());  // 20230815,chenhy,北京-若所有明细节点均无数，则删除所有明细节点
        transaction.put("fundCount", this.dao.findForObject("IExportMapper.selectITransactionFundCountData", queryParam)); //chenhy,20240621,新增小FOF的判断
        
        Map<String, Object> related = new HashMap<>();
        Map<String, Object> current = new HashMap<>();
        Map<String, Object> last = new HashMap<>();
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
        Map<String, Object> manageFee = new HashMap<>();
        
        List<Map<String, Object>> manage = new ArrayList<>();
        List<Map<String, Object>> trustee = new ArrayList<>();
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> manageFeeMetaDataList = (List<Map<String,Object>>)this.dao.findForList("IExportMapper.selectIManageFeeData", queryParam);
        if(manageFeeMetaDataList == null) {
            manageFeeMetaDataList = new ArrayList<>();
        }
        for(Map<String,Object> map : manageFeeMetaDataList) {
            if("MANAGE".equals(map.get("tpye")) ) {
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
        Map<String, Object> salesFee = new HashMap<>();
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

          //chenhy,231121,北京新增基金简称（3.0表)
        List<Map<String, Object>> levelFullNames = new ArrayList<>();
        for(String levelName : levelNames) {

            Map<String,Object> levelFullName = new HashMap<>();
            Map<String,Object> levelQueryMap = new HashMap<>();
            levelQueryMap.put("fundId", queryParam.get("fundId"));
            levelQueryMap.put("level", levelName);
            levelFullName.put("levelFullName", this.dao.findForObject("FundStructuredMapper.selectLevelNameData", levelQueryMap));
            levelFullNames.add(levelFullName);
            }


        Map<String, Map<String, List<Map<String, Object>>>> groups = salesFeeMetaDataList.stream().collect(Collectors.groupingBy(item -> {
            Map<String, Object> map = (Map<String, Object>)item;
            return String.valueOf(map.get("partyShortName"));
        }, Collectors.groupingBy(item -> {
            Map<String, Object> map = (Map<String, Object>)item;
            return String.valueOf(map.get("level"));
        })));
        for(Entry<String, Map<String, List<Map<String, Object>>>> entry : groups.entrySet()) {
            Map<String, Object> middleMap = new HashMap<>();
            List<Map<String,Object>> levelDataList = new ArrayList<>();

            String partyShortName = entry.getKey();
            Map<String, List<Map<String, Object>>> levelMap = entry.getValue();
            
            Double salesCommisionAmtSum = Double.valueOf(0d);
            Double salesCommisionAmtLastSum = Double.valueOf(0d);
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
            
            Double salesCommisionAmtSum = Double.valueOf(0d);
            Double salesCommisionAmtLastSum = Double.valueOf(0d);
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
            sumMap.put("shortName", one.get("shortName"));
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
        salesFee.put("levelFullNames", levelFullNames);
        salesFee.put("levelFullNamesCount", levelFullNames.size());
        
        //--------------------↑I.salesFee↑--------------------

        //--------------------↓I.consultFee↓(专户),20240221,chenhy--------------------
        Map<String, Object> consultFee = new HashMap<>();
        
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> consultFeeMetaDataList = (List<Map<String,Object>>)this.dao.findForList("ReportMapper.selectPlData", queryParam);
        if(consultFeeMetaDataList == null) {
            consultFeeMetaDataList = new ArrayList<>();
        }

        Map<String,Object> consultFeeData = new HashMap<>();
        for(Map<String,Object> map : consultFeeMetaDataList) {
            if ("E04".equals(map.get("plCode"))) {
                consultFeeData = map;
            }
        }

        consultFee.put("consultFee", consultFeeData);
        //--------------------↑I.consultFee↑(专户),20240221,chenhy--------------------

        //--------------------↓I.bankThx↓--------------------
        Map<String, Object> bankThx = new HashMap<>();
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> bankThxDataList = (List<Map<String,Object>>)this.dao.findForList("IExportMapper.selectIBankThxData", queryParam);
        if(bankThxDataList == null) {
            bankThxDataList = new ArrayList<>();
        }
        bankThx.put("list", bankThxDataList);
        bankThx.put("count", bankThxDataList.size());
        //--------------------↑I.bankThx↑--------------------
        //--------------------↓I.mgerHoldFund↓--------------------
        Map<String, Object> mgerHoldFund = new HashMap<>();
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
            Map<String, Object> level = new HashMap<>();
            List<Map<String, Object>> levelList = groups2.get(levelName);
            if(levelList == null) {
                levelList = new ArrayList<>();
            }
            
            Map<String,Object> levelQueryMap = new HashMap<>();
            levelQueryMap.put("fundId", queryParam.get("fundId"));
            levelQueryMap.put("level", levelName);
            levelQueryMap.put("period", queryParam.get("period"));
            level.put("levelFullName", this.dao.findForObject("FundStructuredMapper.selectLevelNameData", levelQueryMap));
            level.put("levelFakeFullName", this.dao.findForObject("FundStructuredMapper.selectFakeLevelNameData", levelQueryMap));
            level.put("dataSumCheckByLevel", this.dao.findForObject("IExportMapper.checkIfIMgerHoldFundHasDataForReportByLevel", levelQueryMap));
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
        Map<String, Object> unmgerHoldFund = new HashMap<>();
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
            result2.put("levelName", levelName);
            Map<String,Object> levelQueryMap = new HashMap<>();
            levelQueryMap.put("fundId", queryParam.get("fundId"));
            levelQueryMap.put("level", levelName);
            result2.put("levelFullName", this.dao.findForObject("FundStructuredMapper.selectLevelNameData", levelQueryMap));
            result2.put("levelFakeFullName", this.dao.findForObject("FundStructuredMapper.selectFakeLevelNameData", levelQueryMap));
            resultList.add(result2);
        }
        unmgerHoldFund.put("levels", resultList);
        unmgerHoldFund.put("levelsCount", resultList.size());
        unmgerHoldFund.put("RowsCount", unmgerHoldFundMetaDataList.size());
        //--------------------↑I.unmgerHoldFund↑--------------------
        //--------------------↓I.bank↓--------------------
        Map<String, Object> bank = new HashMap<>();
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> IBankDataList = (List<Map<String,Object>>)this.dao.findForList("IExportMapper.selectIBankData", queryParam);
        if(IBankDataList == null) {
            IBankDataList = new ArrayList<>();
        }
        bank.put("list", IBankDataList);
        bank.put("count", IBankDataList.size());
        //--------------------↑I.bank↑--------------------
        //--------------------↓I.underWrite↓--------------------
        Map<String, Object> underWrite = new HashMap<>();
        Map<String, Object> current2 = new HashMap<>();
        Map<String, Object> last2 = new HashMap<>();
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

        Map<String, Object> othernote = new HashMap<>();
        List<Map<String, Object>> othernoteList = (List<Map<String, Object>>)this.dao.findForList("IExportMapper.selectIothernoteData", queryParam);
        if (othernoteList == null)
        othernoteList = new ArrayList<>(); 
        int count = othernoteList.size();
        othernote.put("list", othernoteList);
        othernote.put("count", Integer.valueOf(othernoteList.size()));
        Map<String, Object> other = new HashMap<>();
        Map<String, Object> current3 = new HashMap<>();
        Map<String, Object> last3 = new HashMap<>();
        List<Map<String, Object>> otherDataList = (List<Map<String, Object>>)this.dao.findForList("IExportMapper.selectIotherData", queryParam);
        if (otherDataList == null)
        otherDataList = new ArrayList<>(); 
        current3.put("list", otherDataList);
        current3.put("count", Integer.valueOf(otherDataList.size()));
        List<Map<String, Object>> otherlastDataList = (List<Map<String, Object>>)this.dao.findForList("IExportMapper.selectIotherData", queryParamLast);
        if (otherlastDataList == null)
        otherlastDataList = new ArrayList<>(); 
        last3.put("list", otherlastDataList);
        last3.put("count", Integer.valueOf(otherlastDataList.size()));
        other.put("current", current3);
        other.put("last", last3);
        //--------------------↑I.otherData↑--------------------
        I.put("transaction", transaction);
        I.put("manageFee", manageFee);
        I.put("salesFee", salesFee);
        I.put("consultFee", consultFee); // chenhy,240221，新增投资顾问费
        I.put("bankThx", bankThx);
        I.put("mgerHoldFund", mgerHoldFund);
        I.put("unmgerHoldFund", unmgerHoldFund);
        I.put("bank", bank);
        I.put("underWrite", underWrite);
        I.put("othernote", othernote);
        I.put("other", other);
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
        String FundId = String.valueOf(queryParam.get("fundId"));
        String Period = String.valueOf(queryParam.get("period"));
        String PeriodLast = (Integer.parseInt(Period.substring(0, 4)) - 1) + "1231";
        Map<String, Object> queryParamLast = this.createBaseQueryMap(FundId, PeriodLast);
        //====================↓H11000↓====================
        Map<String,Object> H11000 = new HashMap<>();
        //--------------------↓H11000.additian↓--------------------
        Map<String,Object> additian = new HashMap<>();
        List<Map<String,Object>> stocksList = new ArrayList<>();
        List<Map<String,Object>> bondList = new ArrayList<>();
        List<Map<String, Object>> absList = new ArrayList<>();
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> additianDataList = (List<Map<String,Object>>)this.dao.findForList("HExportMapper.selectH11000AdditianDataForReport", queryParam);
        if(additianDataList == null) {
            additianDataList = new ArrayList<>();
        }
        for(Map<String,Object> map : additianDataList) {
            if("股票".equals(map.get("type"))) {
                stocksList.add(map);
            }else if(("债券".equals(map.get("type")) || "资产支持证券".equals(map.get("type"))) && !"YY".equals(map.get("firm_code"))) {
                bondList.add(map);
            }else if("资产支持证券".equals(map.get("type")) && "YY".equals(map.get("firm_code"))) {
                absList.add(map);
            }
        }
        additian.put("stocksList", stocksList);
        additian.put("stocksCount", stocksList.size());
        additian.put("bondList", bondList);
        additian.put("bondCount", bondList.size());
        additian.put("absList", absList);
        additian.put("absCount", absList.size());

        @SuppressWarnings("unchecked")
        Map<String,Object> additianSumData = (Map<String,Object>)this.dao.findForObject("HExportMapper.selectH11000AdditianSumDataForReport", queryParam);
        if(additianSumData == null) {
            additianSumData = new HashMap<>(); 
        }
        additian.put("sum", additianSumData);

        @SuppressWarnings("unchecked")
        Map<String,Object> additianSumLastData = (Map<String,Object>)this.dao.findForObject("HExportMapper.selectH11000AdditianSumDataForReport", queryParamLast);
        if(additianSumLastData == null) {
            additianSumLastData = new HashMap<>(); 
        }
        additian.put("sumLast", additianSumLastData);
        //--------------------↑H11000.additian↑--------------------
        //--------------------↓H11000.suspension↓--------------------
        Map<String, Object> suspension = new HashMap<>();
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> suspensionMetaDataList = (List<Map<String,Object>>)this.dao.findForList("HExportMapper.selectH11000SuspensionDataForReport", queryParam);
        if(suspensionMetaDataList == null) {
            suspensionMetaDataList = new ArrayList<>();
        }
        suspension.put("list", suspensionMetaDataList);
        suspension.put("count", suspensionMetaDataList.size());
        //--------------------↑H11000.suspension↑--------------------
        //--------------------↓H11000.saleIn↓--------------------
        Map<String, Object> saleIn = new HashMap<>();
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
        
        @SuppressWarnings("unchecked")
        Map<String,Object> P600BankSumForP = (Map<String,Object>)this.dao.findForObject("PExportMapper.selectP600BankSumDataForP", queryParam);
        if(P600BankSumForP == null) {
            P600BankSumForP = new HashMap<>();
        }
        @SuppressWarnings("unchecked")
        Map<String,Object> P600ExchangSumForP = (Map<String,Object>)this.dao.findForObject("PExportMapper.selectP600ExchangSumDataForP", queryParam);
        if(P600ExchangSumForP == null) {
            P600ExchangSumForP = new HashMap<>();
        }
        @SuppressWarnings("unchecked")
        Map<String,Object> P600SumForP = (Map<String,Object>)this.dao.findForObject("PExportMapper.selectP600SumDataForP", queryParam);
        if(P600SumForP == null) {
            P600SumForP = new HashMap<>();
        }
        saleIn.put("P600BankSumForP", P600BankSumForP);
        saleIn.put("P600ExchangSumForP", P600ExchangSumForP);
        saleIn.put("P600SumForP", P600SumForP);
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
        SimpleDateFormat sdfout = new SimpleDateFormat("yyyy年M月d日");
        List<String> distinctNoteDateList = noteDataList.stream().filter(item -> {
            return item != null && !StringUtils.EMPTY.equals(item);
        }).map(item -> {
            try {
                return sdfout.format(sdfin.parse(item));
            } catch (ParseException e) {
                return StringUtils.EMPTY;
            }
        }).distinct().collect(Collectors.toList());
        String noteDates = distinctNoteDateList.stream().collect(Collectors.joining("、"));
        noteData.put("noteDates", noteDates);
        noteData.put("noteDatesCount", distinctNoteDateList.size());
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
    
    // /**
    //  * 处理Part4
    //  * @author Dai Zong 2017年12月22日
    //  * 
    //  * @param exportParam
    //  * @param partName
    //  * @return
    //  * @throws IOException
    //  */
    // private String processP4(Map<String,Object> exportParam, Map<String,Object> partName) throws IOException{
    //     String xml2003Content = DocUtil.getXml2003Content(String.valueOf(exportParam.get("reportTempRootPath")) + String.valueOf(partName.get("P4")), "<w:body><wx:sect>.*?<wx:sub-section>(.*)</wx:sub-section>", 1);
    //     if(StringUtils.isEmpty(xml2003Content)) {
    //         throw new IOException("Can not get content from P4 template");
    //     }
    //     return xml2003Content;
    // }
    
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
        
        // yury,20201112,V200按自定义形式披露信用风险评级
        //====================↓V200↓====================
        Map<String,Object> V200 = new HashMap<>();
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> V200CreditRiskRating = (List<Map<String,Object>>)this.dao.findForList("VExportMapper.selectV200CreditRiskRating", queryParam);
        if(V200CreditRiskRating == null) {
            V200CreditRiskRating = new ArrayList<>(); 
        }
        List<Map<String,Object>> ShortBondRatingList = new ArrayList<>(); 
        List<Map<String,Object>> ShortABSRatingList = new ArrayList<>(); 
        List<Map<String,Object>> ShortNCDRatingList = new ArrayList<>(); 
        List<Map<String,Object>> LongBondRatingList = new ArrayList<>(); 
        List<Map<String,Object>> LongABSRatingList = new ArrayList<>(); 
        List<Map<String,Object>> LongNCDRatingList = new ArrayList<>(); 
        Double ShortBondRatingSumCurrent = 0d;
        Double ShortABSRatingSumCurrent = 0d;
        Double ShortNCDRatingSumCurrent = 0d;
        Double LongBondRatingSumCurrent = 0d;
        Double LongABSRatingSumCurrent = 0d;
        Double LongNCDRatingSumCurrent = 0d;
        Double ShortBondRatingSumLast = 0d;
        Double ShortABSRatingSumLast = 0d;
        Double ShortNCDRatingSumLast = 0d;
        Double LongBondRatingSumLast = 0d;
        Double LongABSRatingSumLast = 0d;
        Double LongNCDRatingSumLast = 0d;
        for (Map<String, Object> map: V200CreditRiskRating){
            if (((String)map.get("invest_type")).contains("债券") && "短期".equals((String)map.get("duration"))){
                ShortBondRatingList.add(map);
                ShortBondRatingSumCurrent += ((BigDecimal)map.get("amount")).doubleValue();
                ShortBondRatingSumLast += ((BigDecimal)map.get("amount_last")).doubleValue();
            }else if (((String)map.get("invest_type")).contains("资产支持证券") && "短期".equals((String)map.get("duration"))){
                ShortABSRatingList.add(map);
                ShortABSRatingSumCurrent += ((BigDecimal)map.get("amount")).doubleValue();
                ShortABSRatingSumLast += ((BigDecimal)map.get("amount_last")).doubleValue();
            }else if (((String)map.get("invest_type")).contains("同业存单") && "短期".equals((String)map.get("duration"))){
                ShortNCDRatingList.add(map);
                ShortNCDRatingSumCurrent += ((BigDecimal)map.get("amount")).doubleValue();
                ShortNCDRatingSumLast += ((BigDecimal)map.get("amount_last")).doubleValue();
            }else if (((String)map.get("invest_type")).contains("债券") && "长期".equals((String)map.get("duration"))){
                LongBondRatingList.add(map);
                LongBondRatingSumCurrent += ((BigDecimal)map.get("amount")).doubleValue();
                LongBondRatingSumLast += ((BigDecimal)map.get("amount_last")).doubleValue();
            }else if (((String)map.get("invest_type")).contains("资产支持证券") && "长期".equals((String)map.get("duration"))){
                LongABSRatingList.add(map);
                LongABSRatingSumCurrent += ((BigDecimal)map.get("amount")).doubleValue();
                LongABSRatingSumLast += ((BigDecimal)map.get("amount_last")).doubleValue();
            }else if (((String)map.get("invest_type")).contains("同业存单") && "长期".equals((String)map.get("duration"))){
                LongNCDRatingList.add(map);
                LongNCDRatingSumCurrent += ((BigDecimal)map.get("amount")).doubleValue();
                LongNCDRatingSumLast += ((BigDecimal)map.get("amount_last")).doubleValue();
            }
        }
        V200.put("ShortBondRatingList", ShortBondRatingList);
        V200.put("ShortABSRatingList", ShortABSRatingList);
        V200.put("ShortNCDRatingList", ShortNCDRatingList);
        V200.put("LongBondRatingList", LongBondRatingList);
        V200.put("LongABSRatingList", LongABSRatingList);
        V200.put("LongNCDRatingList", LongNCDRatingList);

        V200.put("ShortBondRatingCount", ShortBondRatingList.size());
        V200.put("ShortABSRatingCount", ShortABSRatingList.size());
        V200.put("ShortNCDRatingCount", ShortNCDRatingList.size());
        V200.put("LongBondRatingCount", LongBondRatingList.size());
        V200.put("LongABSRatingCount", LongABSRatingList.size());
        V200.put("LongNCDRatingCount", LongNCDRatingList.size());

        V200.put("ShortBondRatingSumCurrent", ShortBondRatingSumCurrent);
        V200.put("ShortABSRatingSumCurrent", ShortABSRatingSumCurrent);
        V200.put("ShortNCDRatingSumCurrent", ShortNCDRatingSumCurrent);
        V200.put("LongBondRatingSumCurrent", LongBondRatingSumCurrent);
        V200.put("LongABSRatingSumCurrent", LongABSRatingSumCurrent);
        V200.put("LongNCDRatingSumCurrent", LongNCDRatingSumCurrent);
        V200.put("ShortBondRatingSumLast", ShortBondRatingSumLast);
        V200.put("ShortABSRatingSumLast", ShortABSRatingSumLast);
        V200.put("ShortNCDRatingSumLast", ShortNCDRatingSumLast);
        V200.put("LongBondRatingSumLast", LongBondRatingSumLast);
        V200.put("LongABSRatingSumLast", LongABSRatingSumLast);
        V200.put("LongNCDRatingSumLast", LongNCDRatingSumLast);

        @SuppressWarnings("unchecked")
        List<Map<String,Object>> V200CreditRiskNote = (List<Map<String,Object>>)this.dao.findForList("VExportMapper.selectV200CreditRiskNote", queryParam);
        if(V200CreditRiskNote == null) {
            V200CreditRiskNote = new ArrayList<>();
        }
        Map<String,Object> ShortBondNote = new HashMap<>(); 
        Map<String,Object> ShortABSNote = new HashMap<>(); 
        Map<String,Object> ShortNCDNote = new HashMap<>(); 
        Map<String,Object> LongBondNote = new HashMap<>(); 
        Map<String,Object> LongABSNote = new HashMap<>(); 
        Map<String,Object> LongNCDNote = new HashMap<>();
        ShortBondNote.put("note", "");
        ShortABSNote.put("note", "");
        ShortNCDNote.put("note", "");
        LongBondNote.put("note", "");
        LongABSNote.put("note", "");
        LongNCDNote.put("note", "");
        for (Map<String, Object> map: V200CreditRiskNote){
            if (((String)map.get("invest_type")).contains("债券") && "短期".equals((String)map.get("duration"))){
                ShortBondNote = map;
            }else if (((String)map.get("invest_type")).contains("资产支持证券") && "短期".equals((String)map.get("duration"))){
                ShortABSNote = map;
            }else if (((String)map.get("invest_type")).contains("同业存单") && "短期".equals((String)map.get("duration"))){
                ShortNCDNote = map;
            }else if (((String)map.get("invest_type")).contains("债券") && "长期".equals((String)map.get("duration"))){
                LongBondNote = map;
            }else if (((String)map.get("invest_type")).contains("资产支持证券") && "长期".equals((String)map.get("duration"))){
                LongABSNote = map;
            }else if (((String)map.get("invest_type")).contains("同业存单") && "长期".equals((String)map.get("duration"))){
                LongNCDNote = map;
            }
        }
        V200.put("ShortBondNote", ShortBondNote);
        V200.put("ShortABSNote", ShortABSNote);
        V200.put("ShortNCDNote", ShortNCDNote);
        V200.put("LongBondNote", LongBondNote);
        V200.put("LongABSNote", LongABSNote);
        V200.put("LongNCDNote", LongNCDNote);

        //====================↑V200↑====================

        //====================↓V300↓====================
        Map<String,Object> V300 = new HashMap<>();
        @SuppressWarnings("unchecked")
        List<String> V300IntRistPeriodsDataList = (List<String>)this.dao.findForList("VExportMapper.selectV300IntRiskPeriodData", queryParam);
        if(V300IntRistPeriodsDataList == null) {
            V300IntRistPeriodsDataList = new ArrayList<>(); 
        }
        
        // -----------------irene20230904新增 应交税费/应付税费-----------------------
        @SuppressWarnings("unchecked")
        List<String> V300TaxPayableNameDataList = (List<String>) this.dao
                .findForList("VExportMapper.selectV300IntRiskTaxPayableNameData", queryParam);
        if (V300TaxPayableNameDataList == null) {
            V300TaxPayableNameDataList = new ArrayList<>();
        }
        // -----------------irene20230904新增 应交税费/应付税费 结束-----------------------
        List<Integer> emptyList = Lists.newArrayList();
        for(int i=0 ; i<V300IntRistPeriodsDataList.size(); i++) {
            emptyList.add(0);
        }
        
        Map<String, Object> detail = new HashMap<>();
        
        Map<String, Object> attr1 = new HashMap<>();
        attr1.put("list", emptyList);
        attr1.put("count", 0);
        Map<String, Object> attr2 = new HashMap<>();
        attr2.put("list", emptyList);
        attr2.put("count", 0);
        Map<String, Object> attr3 = new HashMap<>();
        attr3.put("list", emptyList);
        attr3.put("count", 0);
        Map<String, Object> attr4 = new HashMap<>();
        attr4.put("list", emptyList);
        attr4.put("count", 0);
        Map<String, Object> attr5 = new HashMap<>();
        attr5.put("list", emptyList);
        attr5.put("count", 0);
        Map<String, Object> attr6 = new HashMap<>();
        attr6.put("list", emptyList);
        attr6.put("count", 0);
        Map<String, Object> attr7 = new HashMap<>();
        attr7.put("list", emptyList);
        attr7.put("count", 0);
        Map<String, Object> attr8 = new HashMap<>();
        attr8.put("list", emptyList);
        attr8.put("count", 0);
        Map<String, Object> attr9 = new HashMap<>();
        attr9.put("list", emptyList);
        attr9.put("count", 0);
        Map<String, Object> attr10 = new HashMap<>();
        attr10.put("list", emptyList);
        attr10.put("count", 0);
        Map<String, Object> attr11 = new HashMap<>();
        attr11.put("list", emptyList);
        attr11.put("count", 0);
        Map<String, Object> attr12 = new HashMap<>();
        attr12.put("list", emptyList);
        attr12.put("count", 0);
        Map<String, Object> attr13 = new HashMap<>();
        attr13.put("list", emptyList);
        attr13.put("count", 0);
        Map<String, Object> attr14 = new HashMap<>();
        attr14.put("list", emptyList);
        attr14.put("count", 0);
        Map<String, Object> attr15 = new HashMap<>();
        attr15.put("list", emptyList);
        attr15.put("count", 0);
        Map<String, Object> attr16 = new HashMap<>();
        attr16.put("list", emptyList);
        attr16.put("count", 0);
        Map<String, Object> attr17 = new HashMap<>();
        attr17.put("list", emptyList);
        attr17.put("count", 0);
        Map<String, Object> attr18 = new HashMap<>();
        attr18.put("list", emptyList);
        attr18.put("count", 0);
        Map<String, Object> attr19 = new HashMap<>();
        attr19.put("list", emptyList);
        attr19.put("count", 0);
        Map<String, Object> attr20 = new HashMap<>();
        attr20.put("list", emptyList);
        attr20.put("count", 0);
        Map<String, Object> attr21 = new HashMap<>();
        attr21.put("list", emptyList);
        attr21.put("count", 0);
        Map<String, Object> attr22 = new HashMap<>();
        attr22.put("list", emptyList);
        attr22.put("count", 0);
        Map<String, Object> attr23 = new HashMap<>();
        attr23.put("list", emptyList);
        attr23.put("count", 0);
        Map<String, Object> attr24 = new HashMap<>();
        attr24.put("list", emptyList);
        attr24.put("count", 0);
        Map<String, Object> attr25 = new HashMap<>();
        attr25.put("list", emptyList);
        attr25.put("count", 0);
        Map<String, Object> attr26 = new HashMap<>();
        attr26.put("list", emptyList);
        attr26.put("count", 0);
        Map<String, Object> attr27 = new HashMap<>();
        attr27.put("list", emptyList);
        attr27.put("count", 0);
        Map<String, Object> sum1 = new HashMap<>();
        sum1.put("list", emptyList);
        sum1.put("count", 0);
        Map<String, Object> sum2 = new HashMap<>();
        sum2.put("list", emptyList);
        sum2.put("count", 0);
        Map<String, Object> sum = new HashMap<>();
        
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> V300MetaDataList = (List<Map<String,Object>>)this.dao.findForList("VExportMapper.selectV300Data", queryParam);
        if(V300MetaDataList == null) {
            V300MetaDataList = new ArrayList<>(); 
        }
        
        Map<String, List<Map<String, Object>>> groups = V300MetaDataList.parallelStream().collect(Collectors.groupingBy(item ->
            String.valueOf(item.get("type"))
        ));
        
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
                case "货币资金":
                    attr1.put("list", list);
                    attr1.put("count", count);
                    lineAmountSum = Double.valueOf(0d);
                    lineAmountLastSum = Double.valueOf(0d);
                    for(int i = 0 ; i < list.size() ; i++) {
                        Map<String,Object> map = list.get(i);
                        Map<String, Double> sumMap = sum1List.get(i);
                        Double temp1 = map.get("amount") == null ? 0 : Double.parseDouble(String.valueOf(map.get("amount")));
                        Double temp2 = map.get("amountLast") == null ? 0 : Double.parseDouble(String.valueOf(map.get("amountLast")));
                        lineAmountSum += temp1;
                        lineAmountLastSum +=temp2;
                        sumMap.put("amount", (sumMap.get("amount") == null ? 0 : sumMap.get("amount")) + temp1);
                        sumMap.put("amountLast", (sumMap.get("amountLast") == null ? 0 : sumMap.get("amountLast")) + temp2);
                    }
                    attr1.put("lineAmountSum", lineAmountSum == 0 ? null : lineAmountSum);
                    attr1.put("lineAmountLastSum", lineAmountLastSum == 0 ? null : lineAmountLastSum);
                    break;
                case "结算备付金":
                    attr2.put("list", list);
                    attr2.put("count", count);
                    lineAmountSum = Double.valueOf(0d);
                    lineAmountLastSum = Double.valueOf(0d);
                    for(int i = 0 ; i < list.size() ; i++) {
                        Map<String,Object> map = list.get(i);
                        Map<String, Double> sumMap = sum1List.get(i);
                        Double temp1 = map.get("amount") == null ? 0 : Double.parseDouble(String.valueOf(map.get("amount")));
                        Double temp2 = map.get("amountLast") == null ? 0 : Double.parseDouble(String.valueOf(map.get("amountLast")));
                        lineAmountSum += temp1;
                        lineAmountLastSum +=temp2;
                        sumMap.put("amount", (sumMap.get("amount") == null ? 0 : sumMap.get("amount")) + temp1);
                        sumMap.put("amountLast", (sumMap.get("amountLast") == null ? 0 : sumMap.get("amountLast")) + temp2);
                    }
                    attr2.put("lineAmountSum", lineAmountSum == 0 ? null : lineAmountSum);
                    attr2.put("lineAmountLastSum", lineAmountLastSum == 0 ? null : lineAmountLastSum);
                    break;
                case "存出保证金":
                    attr3.put("list", list);
                    attr3.put("count", count);
                    lineAmountSum = Double.valueOf(0d);
                    lineAmountLastSum = Double.valueOf(0d);
                    for(int i = 0 ; i < list.size() ; i++) {
                        Map<String,Object> map = list.get(i);
                        Map<String, Double> sumMap = sum1List.get(i);
                        Double temp1 = map.get("amount") == null ? 0 : Double.parseDouble(String.valueOf(map.get("amount")));
                        Double temp2 = map.get("amountLast") == null ? 0 : Double.parseDouble(String.valueOf(map.get("amountLast")));
                        lineAmountSum += temp1;
                        lineAmountLastSum +=temp2;
                        sumMap.put("amount", (sumMap.get("amount") == null ? 0 : sumMap.get("amount")) + temp1);
                        sumMap.put("amountLast", (sumMap.get("amountLast") == null ? 0 : sumMap.get("amountLast")) + temp2);
                    }
                    attr3.put("lineAmountSum", lineAmountSum == 0 ? null : lineAmountSum);
                    attr3.put("lineAmountLastSum", lineAmountLastSum == 0 ? null : lineAmountLastSum);
                    break;
                case "交易性金融资产":
                    attr4.put("list", list);
                    attr4.put("count", count);
                    lineAmountSum = Double.valueOf(0d);
                    lineAmountLastSum = Double.valueOf(0d);
                    for(int i = 0 ; i < list.size() ; i++) {
                        Map<String,Object> map = list.get(i);
                        Map<String, Double> sumMap = sum1List.get(i);
                        Double temp1 = map.get("amount") == null ? 0 : Double.parseDouble(String.valueOf(map.get("amount")));
                        Double temp2 = map.get("amountLast") == null ? 0 : Double.parseDouble(String.valueOf(map.get("amountLast")));
                        lineAmountSum += temp1;
                        lineAmountLastSum +=temp2;
                        sumMap.put("amount", (sumMap.get("amount") == null ? 0 : sumMap.get("amount")) + temp1);
                        sumMap.put("amountLast", (sumMap.get("amountLast") == null ? 0 : sumMap.get("amountLast")) + temp2);
                    }
                    attr4.put("lineAmountSum", lineAmountSum == 0 ? null : lineAmountSum);
                    attr4.put("lineAmountLastSum", lineAmountLastSum == 0 ? null : lineAmountLastSum);
                    break;
                case "衍生金融资产":
                    attr5.put("list", list);
                    attr5.put("count", count);
                    lineAmountSum = Double.valueOf(0d);
                    lineAmountLastSum = Double.valueOf(0d);
                    for(int i = 0 ; i < list.size() ; i++) {
                        Map<String,Object> map = list.get(i);
                        Map<String, Double> sumMap = sum1List.get(i);
                        Double temp1 = map.get("amount") == null ? 0 : Double.parseDouble(String.valueOf(map.get("amount")));
                        Double temp2 = map.get("amountLast") == null ? 0 : Double.parseDouble(String.valueOf(map.get("amountLast")));
                        lineAmountSum += temp1;
                        lineAmountLastSum +=temp2;
                        sumMap.put("amount", (sumMap.get("amount") == null ? 0 : sumMap.get("amount")) + temp1);
                        sumMap.put("amountLast", (sumMap.get("amountLast") == null ? 0 : sumMap.get("amountLast")) + temp2);
                    }
                    attr5.put("lineAmountSum", lineAmountSum == 0 ? null : lineAmountSum);
                    attr5.put("lineAmountLastSum", lineAmountLastSum == 0 ? null : lineAmountLastSum);
                    break;
                case "买入返售金融资产":
                    attr6.put("list", list);
                    attr6.put("count", count);
                    lineAmountSum = Double.valueOf(0d);
                    lineAmountLastSum = Double.valueOf(0d);
                    for(int i = 0 ; i < list.size() ; i++) {
                        Map<String,Object> map = list.get(i);
                        Map<String, Double> sumMap = sum1List.get(i);
                        Double temp1 = map.get("amount") == null ? 0 : Double.parseDouble(String.valueOf(map.get("amount")));
                        Double temp2 = map.get("amountLast") == null ? 0 : Double.parseDouble(String.valueOf(map.get("amountLast")));
                        lineAmountSum += temp1;
                        lineAmountLastSum +=temp2;
                        sumMap.put("amount", (sumMap.get("amount") == null ? 0 : sumMap.get("amount")) + temp1);
                        sumMap.put("amountLast", (sumMap.get("amountLast") == null ? 0 : sumMap.get("amountLast")) + temp2);
                    }
                    attr6.put("lineAmountSum", lineAmountSum == 0 ? null : lineAmountSum);
                    attr6.put("lineAmountLastSum", lineAmountLastSum == 0 ? null : lineAmountLastSum);
                    break;
                case "应收证券清算款":
                    attr7.put("list", list);
                    attr7.put("count", count);
                    lineAmountSum = Double.valueOf(0d);
                    lineAmountLastSum = Double.valueOf(0d);
                    for(int i = 0 ; i < list.size() ; i++) {
                        Map<String,Object> map = list.get(i);
                        Map<String, Double> sumMap = sum1List.get(i);
                        Double temp1 = map.get("amount") == null ? 0 : Double.parseDouble(String.valueOf(map.get("amount")));
                        Double temp2 = map.get("amountLast") == null ? 0 : Double.parseDouble(String.valueOf(map.get("amountLast")));
                        lineAmountSum += temp1;
                        lineAmountLastSum +=temp2;
                        sumMap.put("amount", (sumMap.get("amount") == null ? 0 : sumMap.get("amount")) + temp1);
                        sumMap.put("amountLast", (sumMap.get("amountLast") == null ? 0 : sumMap.get("amountLast")) + temp2);
                    }
                    attr7.put("lineAmountSum", lineAmountSum == 0 ? null : lineAmountSum);
                    attr7.put("lineAmountLastSum", lineAmountLastSum == 0 ? null : lineAmountLastSum);
                    break;
                case "应收利息":
                    attr8.put("list", list);
                    attr8.put("count", count);
                    lineAmountSum = Double.valueOf(0d);
                    lineAmountLastSum = Double.valueOf(0d);
                    for(int i = 0 ; i < list.size() ; i++) {
                        Map<String,Object> map = list.get(i);
                        Map<String, Double> sumMap = sum1List.get(i);
                        Double temp1 = map.get("amount") == null ? 0 : Double.parseDouble(String.valueOf(map.get("amount")));
                        Double temp2 = map.get("amountLast") == null ? 0 : Double.parseDouble(String.valueOf(map.get("amountLast")));
                        lineAmountSum += temp1;
                        lineAmountLastSum +=temp2;
                        sumMap.put("amount", (sumMap.get("amount") == null ? 0 : sumMap.get("amount")) + temp1);
                        sumMap.put("amountLast", (sumMap.get("amountLast") == null ? 0 : sumMap.get("amountLast")) + temp2);
                    }
                    attr8.put("lineAmountSum", lineAmountSum == 0 ? null : lineAmountSum);
                    attr8.put("lineAmountLastSum", lineAmountLastSum == 0 ? null : lineAmountLastSum);
                    break;
                case "应收股利":
                    attr9.put("list", list);
                    attr9.put("count", count);
                    lineAmountSum = Double.valueOf(0d);
                    lineAmountLastSum = Double.valueOf(0d);
                    for(int i = 0 ; i < list.size() ; i++) {
                        Map<String,Object> map = list.get(i);
                        Map<String, Double> sumMap = sum1List.get(i);
                        Double temp1 = map.get("amount") == null ? 0 : Double.parseDouble(String.valueOf(map.get("amount")));
                        Double temp2 = map.get("amountLast") == null ? 0 : Double.parseDouble(String.valueOf(map.get("amountLast")));
                        lineAmountSum += temp1;
                        lineAmountLastSum +=temp2;
                        sumMap.put("amount", (sumMap.get("amount") == null ? 0 : sumMap.get("amount")) + temp1);
                        sumMap.put("amountLast", (sumMap.get("amountLast") == null ? 0 : sumMap.get("amountLast")) + temp2);
                    }
                    attr9.put("lineAmountSum", lineAmountSum == 0 ? null : lineAmountSum);
                    attr9.put("lineAmountLastSum", lineAmountLastSum == 0 ? null : lineAmountLastSum);
                    break;
                case "应收申购款":
                    attr10.put("list", list);
                    attr10.put("count", count);
                    lineAmountSum = Double.valueOf(0d);
                    lineAmountLastSum = Double.valueOf(0d);
                    for(int i = 0 ; i < list.size() ; i++) {
                        Map<String,Object> map = list.get(i);
                        Map<String, Double> sumMap = sum1List.get(i);
                        Double temp1 = map.get("amount") == null ? 0 : Double.parseDouble(String.valueOf(map.get("amount")));
                        Double temp2 = map.get("amountLast") == null ? 0 : Double.parseDouble(String.valueOf(map.get("amountLast")));
                        lineAmountSum += temp1;
                        lineAmountLastSum +=temp2;
                        sumMap.put("amount", (sumMap.get("amount") == null ? 0 : sumMap.get("amount")) + temp1);
                        sumMap.put("amountLast", (sumMap.get("amountLast") == null ? 0 : sumMap.get("amountLast")) + temp2);
                    }
                    attr10.put("lineAmountSum", lineAmountSum == 0 ? null : lineAmountSum);
                    attr10.put("lineAmountLastSum", lineAmountLastSum == 0 ? null : lineAmountLastSum);
                    break;
                case "其他资产":
                    attr11.put("list", list);
                    attr11.put("count", count);
                    lineAmountSum = Double.valueOf(0d);
                    lineAmountLastSum = Double.valueOf(0d);
                    for(int i = 0 ; i < list.size() ; i++) {
                        Map<String,Object> map = list.get(i);
                        Map<String, Double> sumMap = sum1List.get(i);
                        Double temp1 = map.get("amount") == null ? 0 : Double.parseDouble(String.valueOf(map.get("amount")));
                        Double temp2 = map.get("amountLast") == null ? 0 : Double.parseDouble(String.valueOf(map.get("amountLast")));
                        lineAmountSum += temp1;
                        lineAmountLastSum +=temp2;
                        sumMap.put("amount", (sumMap.get("amount") == null ? 0 : sumMap.get("amount")) + temp1);
                        sumMap.put("amountLast", (sumMap.get("amountLast") == null ? 0 : sumMap.get("amountLast")) + temp2);
                    }
                    attr11.put("lineAmountSum", lineAmountSum == 0 ? null : lineAmountSum);
                    attr11.put("lineAmountLastSum", lineAmountLastSum == 0 ? null : lineAmountLastSum);
                    break;
                case "短期借款":
                    attr12.put("list", list);
                    attr12.put("count", count);
                    lineAmountSum = Double.valueOf(0d);
                    lineAmountLastSum = Double.valueOf(0d);
                    for(int i = 0 ; i < list.size() ; i++) {
                        Map<String,Object> map = list.get(i);
                        Map<String, Double> sumMap = sum2List.get(i);
                        Double temp1 = map.get("amount") == null ? 0 : Double.parseDouble(String.valueOf(map.get("amount")));
                        Double temp2 = map.get("amountLast") == null ? 0 : Double.parseDouble(String.valueOf(map.get("amountLast")));
                        lineAmountSum += temp1;
                        lineAmountLastSum +=temp2;
                        sumMap.put("amount", (sumMap.get("amount") == null ? 0 : sumMap.get("amount")) + temp1);
                        sumMap.put("amountLast", (sumMap.get("amountLast") == null ? 0 : sumMap.get("amountLast")) + temp2);
                    }
                    attr12.put("lineAmountSum", lineAmountSum == 0 ? null : lineAmountSum);
                    attr12.put("lineAmountLastSum", lineAmountLastSum == 0 ? null : lineAmountLastSum);
                    break;
                case "交易性金融负债":
                    attr13.put("list", list);
                    attr13.put("count", count);
                    lineAmountSum = Double.valueOf(0d);
                    lineAmountLastSum = Double.valueOf(0d);
                    for(int i = 0 ; i < list.size() ; i++) {
                        Map<String,Object> map = list.get(i);
                        Map<String, Double> sumMap = sum2List.get(i);
                        Double temp1 = map.get("amount") == null ? 0 : Double.parseDouble(String.valueOf(map.get("amount")));
                        Double temp2 = map.get("amountLast") == null ? 0 : Double.parseDouble(String.valueOf(map.get("amountLast")));
                        lineAmountSum += temp1;
                        lineAmountLastSum +=temp2;
                        sumMap.put("amount", (sumMap.get("amount") == null ? 0 : sumMap.get("amount")) + temp1);
                        sumMap.put("amountLast", (sumMap.get("amountLast") == null ? 0 : sumMap.get("amountLast")) + temp2);
                    }
                    attr13.put("lineAmountSum", lineAmountSum == 0 ? null : lineAmountSum);
                    attr13.put("lineAmountLastSum", lineAmountLastSum == 0 ? null : lineAmountLastSum);
                    break;
                case "衍生金融负债":
                    attr14.put("list", list);
                    attr14.put("count", count);
                    lineAmountSum = Double.valueOf(0d);
                    lineAmountLastSum = Double.valueOf(0d);
                    for(int i = 0 ; i < list.size() ; i++) {
                        Map<String,Object> map = list.get(i);
                        Map<String, Double> sumMap = sum2List.get(i);
                        Double temp1 = map.get("amount") == null ? 0 : Double.parseDouble(String.valueOf(map.get("amount")));
                        Double temp2 = map.get("amountLast") == null ? 0 : Double.parseDouble(String.valueOf(map.get("amountLast")));
                        lineAmountSum += temp1;
                        lineAmountLastSum +=temp2;
                        sumMap.put("amount", (sumMap.get("amount") == null ? 0 : sumMap.get("amount")) + temp1);
                        sumMap.put("amountLast", (sumMap.get("amountLast") == null ? 0 : sumMap.get("amountLast")) + temp2);
                    }
                    attr14.put("lineAmountSum", lineAmountSum == 0 ? null : lineAmountSum);
                    attr14.put("lineAmountLastSum", lineAmountLastSum == 0 ? null : lineAmountLastSum);
                    break;
                case "卖出回购金融资产款":
                    attr15.put("list", list);
                    attr15.put("count", count);
                    lineAmountSum = Double.valueOf(0d);
                    lineAmountLastSum = Double.valueOf(0d);
                    for(int i = 0 ; i < list.size() ; i++) {
                        Map<String,Object> map = list.get(i);
                        Map<String, Double> sumMap = sum2List.get(i);
                        Double temp1 = map.get("amount") == null ? 0 : Double.parseDouble(String.valueOf(map.get("amount")));
                        Double temp2 = map.get("amountLast") == null ? 0 : Double.parseDouble(String.valueOf(map.get("amountLast")));
                        lineAmountSum += temp1;
                        lineAmountLastSum +=temp2;
                        sumMap.put("amount", (sumMap.get("amount") == null ? 0 : sumMap.get("amount")) + temp1);
                        sumMap.put("amountLast", (sumMap.get("amountLast") == null ? 0 : sumMap.get("amountLast")) + temp2);
                    }
                    attr15.put("lineAmountSum", lineAmountSum == 0 ? null : lineAmountSum);
                    attr15.put("lineAmountLastSum", lineAmountLastSum == 0 ? null : lineAmountLastSum);
                    break;
                case "应付证券清算款":
                    attr16.put("list", list);
                    attr16.put("count", count);
                    lineAmountSum = Double.valueOf(0d);
                    lineAmountLastSum = Double.valueOf(0d);
                    for(int i = 0 ; i < list.size() ; i++) {
                        Map<String,Object> map = list.get(i);
                        Map<String, Double> sumMap = sum2List.get(i);
                        Double temp1 = map.get("amount") == null ? 0 : Double.parseDouble(String.valueOf(map.get("amount")));
                        Double temp2 = map.get("amountLast") == null ? 0 : Double.parseDouble(String.valueOf(map.get("amountLast")));
                        lineAmountSum += temp1;
                        lineAmountLastSum +=temp2;
                        sumMap.put("amount", (sumMap.get("amount") == null ? 0 : sumMap.get("amount")) + temp1);
                        sumMap.put("amountLast", (sumMap.get("amountLast") == null ? 0 : sumMap.get("amountLast")) + temp2);
                    }
                    attr16.put("lineAmountSum", lineAmountSum == 0 ? null : lineAmountSum);
                    attr16.put("lineAmountLastSum", lineAmountLastSum == 0 ? null : lineAmountLastSum);
                    break;
                case "应付赎回款":
                    attr17.put("list", list);
                    attr17.put("count", count);
                    lineAmountSum = Double.valueOf(0d);
                    lineAmountLastSum = Double.valueOf(0d);
                    for(int i = 0 ; i < list.size() ; i++) {
                        Map<String,Object> map = list.get(i);
                        Map<String, Double> sumMap = sum2List.get(i);
                        Double temp1 = map.get("amount") == null ? 0 : Double.parseDouble(String.valueOf(map.get("amount")));
                        Double temp2 = map.get("amountLast") == null ? 0 : Double.parseDouble(String.valueOf(map.get("amountLast")));
                        lineAmountSum += temp1;
                        lineAmountLastSum +=temp2;
                        sumMap.put("amount", (sumMap.get("amount") == null ? 0 : sumMap.get("amount")) + temp1);
                        sumMap.put("amountLast", (sumMap.get("amountLast") == null ? 0 : sumMap.get("amountLast")) + temp2);
                    }
                    attr17.put("lineAmountSum", lineAmountSum == 0 ? null : lineAmountSum);
                    attr17.put("lineAmountLastSum", lineAmountLastSum == 0 ? null : lineAmountLastSum);
                    break;
                case "应付管理人报酬":
                    attr18.put("list", list);
                    attr18.put("count", count);
                    lineAmountSum = Double.valueOf(0d);
                    lineAmountLastSum = Double.valueOf(0d);
                    for(int i = 0 ; i < list.size() ; i++) {
                        Map<String,Object> map = list.get(i);
                        Map<String, Double> sumMap = sum2List.get(i);
                        Double temp1 = map.get("amount") == null ? 0 : Double.parseDouble(String.valueOf(map.get("amount")));
                        Double temp2 = map.get("amountLast") == null ? 0 : Double.parseDouble(String.valueOf(map.get("amountLast")));
                        lineAmountSum += temp1;
                        lineAmountLastSum +=temp2;
                        sumMap.put("amount", (sumMap.get("amount") == null ? 0 : sumMap.get("amount")) + temp1);
                        sumMap.put("amountLast", (sumMap.get("amountLast") == null ? 0 : sumMap.get("amountLast")) + temp2);
                    }
                    attr18.put("lineAmountSum", lineAmountSum == 0 ? null : lineAmountSum);
                    attr18.put("lineAmountLastSum", lineAmountLastSum == 0 ? null : lineAmountLastSum);
                    break;
                case "应付托管费":
                    attr19.put("list", list);
                    attr19.put("count", count);
                    lineAmountSum = Double.valueOf(0d);
                    lineAmountLastSum = Double.valueOf(0d);
                    for(int i = 0 ; i < list.size() ; i++) {
                        Map<String,Object> map = list.get(i);
                        Map<String, Double> sumMap = sum2List.get(i);
                        Double temp1 = map.get("amount") == null ? 0 : Double.parseDouble(String.valueOf(map.get("amount")));
                        Double temp2 = map.get("amountLast") == null ? 0 : Double.parseDouble(String.valueOf(map.get("amountLast")));
                        lineAmountSum += temp1;
                        lineAmountLastSum +=temp2;
                        sumMap.put("amount", (sumMap.get("amount") == null ? 0 : sumMap.get("amount")) + temp1);
                        sumMap.put("amountLast", (sumMap.get("amountLast") == null ? 0 : sumMap.get("amountLast")) + temp2);
                    }
                    attr19.put("lineAmountSum", lineAmountSum == 0 ? null : lineAmountSum);
                    attr19.put("lineAmountLastSum", lineAmountLastSum == 0 ? null : lineAmountLastSum);
                    break;
                case "应付销售服务费":
                    attr20.put("list", list);
                    attr20.put("count", count);
                    lineAmountSum = Double.valueOf(0d);
                    lineAmountLastSum = Double.valueOf(0d);
                    for(int i = 0 ; i < list.size() ; i++) {
                        Map<String,Object> map = list.get(i);
                        Map<String, Double> sumMap = sum2List.get(i);
                        Double temp1 = map.get("amount") == null ? 0 : Double.parseDouble(String.valueOf(map.get("amount")));
                        Double temp2 = map.get("amountLast") == null ? 0 : Double.parseDouble(String.valueOf(map.get("amountLast")));
                        lineAmountSum += temp1;
                        lineAmountLastSum +=temp2;
                        sumMap.put("amount", (sumMap.get("amount") == null ? 0 : sumMap.get("amount")) + temp1);
                        sumMap.put("amountLast", (sumMap.get("amountLast") == null ? 0 : sumMap.get("amountLast")) + temp2);
                    }
                    attr20.put("lineAmountSum", lineAmountSum == 0 ? null : lineAmountSum);
                    attr20.put("lineAmountLastSum", lineAmountLastSum == 0 ? null : lineAmountLastSum);
                    break;
                case "应付交易费用":
                    attr21.put("list", list);
                    attr21.put("count", count);
                    lineAmountSum = Double.valueOf(0d);
                    lineAmountLastSum = Double.valueOf(0d);
                    for(int i = 0 ; i < list.size() ; i++) {
                        Map<String,Object> map = list.get(i);
                        Map<String, Double> sumMap = sum2List.get(i);
                        Double temp1 = map.get("amount") == null ? 0 : Double.parseDouble(String.valueOf(map.get("amount")));
                        Double temp2 = map.get("amountLast") == null ? 0 : Double.parseDouble(String.valueOf(map.get("amountLast")));
                        lineAmountSum += temp1;
                        lineAmountLastSum +=temp2;
                        sumMap.put("amount", (sumMap.get("amount") == null ? 0 : sumMap.get("amount")) + temp1);
                        sumMap.put("amountLast", (sumMap.get("amountLast") == null ? 0 : sumMap.get("amountLast")) + temp2);
                    }
                    attr21.put("lineAmountSum", lineAmountSum == 0 ? null : lineAmountSum);
                    attr21.put("lineAmountLastSum", lineAmountLastSum == 0 ? null : lineAmountLastSum);
                    break;
                case "应付税费":
                    attr22.put("list", list);
                    attr22.put("count", count);
                    lineAmountSum = Double.valueOf(0d);
                    lineAmountLastSum = Double.valueOf(0d);
                    for(int i = 0 ; i < list.size() ; i++) {
                        Map<String,Object> map = list.get(i);
                        Map<String, Double> sumMap = sum2List.get(i);
                        Double temp1 = map.get("amount") == null ? 0 : Double.parseDouble(String.valueOf(map.get("amount")));
                        Double temp2 = map.get("amountLast") == null ? 0 : Double.parseDouble(String.valueOf(map.get("amountLast")));
                        lineAmountSum += temp1;
                        lineAmountLastSum +=temp2;
                        sumMap.put("amount", (sumMap.get("amount") == null ? 0 : sumMap.get("amount")) + temp1);
                        sumMap.put("amountLast", (sumMap.get("amountLast") == null ? 0 : sumMap.get("amountLast")) + temp2);
                    }
                    attr22.put("lineAmountSum", lineAmountSum == 0 ? null : lineAmountSum);
                    attr22.put("lineAmountLastSum", lineAmountLastSum == 0 ? null : lineAmountLastSum);
                    break;
                case "应付利息":
                    attr23.put("list", list);
                    attr23.put("count", count);
                    lineAmountSum = Double.valueOf(0d);
                    lineAmountLastSum = Double.valueOf(0d);
                    for(int i = 0 ; i < list.size() ; i++) {
                        Map<String,Object> map = list.get(i);
                        Map<String, Double> sumMap = sum2List.get(i);
                        Double temp1 = map.get("amount") == null ? 0 : Double.parseDouble(String.valueOf(map.get("amount")));
                        Double temp2 = map.get("amountLast") == null ? 0 : Double.parseDouble(String.valueOf(map.get("amountLast")));
                        lineAmountSum += temp1;
                        lineAmountLastSum +=temp2;
                        sumMap.put("amount", (sumMap.get("amount") == null ? 0 : sumMap.get("amount")) + temp1);
                        sumMap.put("amountLast", (sumMap.get("amountLast") == null ? 0 : sumMap.get("amountLast")) + temp2);
                    }
                    attr23.put("lineAmountSum", lineAmountSum == 0 ? null : lineAmountSum);
                    attr23.put("lineAmountLastSum", lineAmountLastSum == 0 ? null : lineAmountLastSum);
                    break;
                case "应付利润":
                    attr24.put("list", list);
                    attr24.put("count", count);
                    lineAmountSum = Double.valueOf(0d);
                    lineAmountLastSum = Double.valueOf(0d);
                    for(int i = 0 ; i < list.size() ; i++) {
                        Map<String,Object> map = list.get(i);
                        Map<String, Double> sumMap = sum2List.get(i);
                        Double temp1 = map.get("amount") == null ? 0 : Double.parseDouble(String.valueOf(map.get("amount")));
                        Double temp2 = map.get("amountLast") == null ? 0 : Double.parseDouble(String.valueOf(map.get("amountLast")));
                        lineAmountSum += temp1;
                        lineAmountLastSum +=temp2;
                        sumMap.put("amount", (sumMap.get("amount") == null ? 0 : sumMap.get("amount")) + temp1);
                        sumMap.put("amountLast", (sumMap.get("amountLast") == null ? 0 : sumMap.get("amountLast")) + temp2);
                    }
                    attr24.put("lineAmountSum", lineAmountSum == 0 ? null : lineAmountSum);
                    attr24.put("lineAmountLastSum", lineAmountLastSum == 0 ? null : lineAmountLastSum);
                    break;
                case "其他负债":
                    attr25.put("list", list);
                    attr25.put("count", count);
                    lineAmountSum = Double.valueOf(0d);
                    lineAmountLastSum = Double.valueOf(0d);
                    for(int i = 0 ; i < list.size() ; i++) {
                        Map<String,Object> map = list.get(i);
                        Map<String, Double> sumMap = sum2List.get(i);
                        Double temp1 = map.get("amount") == null ? 0 : Double.parseDouble(String.valueOf(map.get("amount")));
                        Double temp2 = map.get("amountLast") == null ? 0 : Double.parseDouble(String.valueOf(map.get("amountLast")));
                        lineAmountSum += temp1;
                        lineAmountLastSum +=temp2;
                        sumMap.put("amount", (sumMap.get("amount") == null ? 0 : sumMap.get("amount")) + temp1);
                        sumMap.put("amountLast", (sumMap.get("amountLast") == null ? 0 : sumMap.get("amountLast")) + temp2);
                    }
                    attr25.put("lineAmountSum", lineAmountSum == 0 ? null : lineAmountSum);
                    attr25.put("lineAmountLastSum", lineAmountLastSum == 0 ? null : lineAmountLastSum);
                    break;
                case "债权投资":
                    attr26.put("list", list);
                    attr26.put("count", count);
                    lineAmountSum = Double.valueOf(0d);
                    lineAmountLastSum = Double.valueOf(0d);
                    for(int i = 0 ; i < list.size() ; i++) {
                        Map<String,Object> map = list.get(i);
                        Map<String, Double> sumMap = sum1List.get(i);
                        Double temp1 = map.get("amount") == null ? 0 : Double.parseDouble(String.valueOf(map.get("amount")));
                        Double temp2 = map.get("amountLast") == null ? 0 : Double.parseDouble(String.valueOf(map.get("amountLast")));
                        lineAmountSum += temp1;
                        lineAmountLastSum +=temp2;
                        sumMap.put("amount", (sumMap.get("amount") == null ? 0 : sumMap.get("amount")) + temp1);
                        sumMap.put("amountLast", (sumMap.get("amountLast") == null ? 0 : sumMap.get("amountLast")) + temp2);
                    }
                    attr26.put("lineAmountSum", lineAmountSum == 0 ? null : lineAmountSum);
                    attr26.put("lineAmountLastSum", lineAmountLastSum == 0 ? null : lineAmountLastSum);
                    break;
                    case "应付投资顾问费":
                        attr27.put("list", list);
                        attr27.put("count", count);
                        lineAmountSum = Double.valueOf(0d);
                        lineAmountLastSum = Double.valueOf(0d);
                        for(int i = 0 ; i < list.size() ; i++) {
                            Map<String,Object> map = list.get(i);
                            Map<String, Double> sumMap = sum2List.get(i);
                            Double temp1 = map.get("amount") == null ? 0 : Double.parseDouble(String.valueOf(map.get("amount")));
                            Double temp2 = map.get("amountLast") == null ? 0 : Double.parseDouble(String.valueOf(map.get("amountLast")));
                            lineAmountSum += temp1;
                            lineAmountLastSum +=temp2;
                            sumMap.put("amount", (sumMap.get("amount") == null ? 0 : sumMap.get("amount")) + temp1);
                            sumMap.put("amountLast", (sumMap.get("amountLast") == null ? 0 : sumMap.get("amountLast")) + temp2);
                        }
                        attr27.put("lineAmountSum", lineAmountSum == 0 ? null : lineAmountSum);
                        attr27.put("lineAmountLastSum", lineAmountLastSum == 0 ? null : lineAmountLastSum);
                        break;
                default:
                    break;
            }
        }
        
        Double sum1LineAmountSum = Double.valueOf(0d);
        Double sum1LineAmountLastSum = Double.valueOf(0d);
        for(Map<String,Double> sum1Map : sum1List) {
            sum1LineAmountSum += (sum1Map.get("amount") == null ? 0 : sum1Map.get("amount"));
            sum1LineAmountLastSum += (sum1Map.get("amountLast") == null ? 0 : sum1Map.get("amountLast"));
        }
        Double sum2LineAmountSum = Double.valueOf(0d);
        Double sum2LineAmountLastSum = Double.valueOf(0d);
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
        detail.put("attr26", attr26);
        detail.put("attr27", attr27);
        detail.put("sum1", sum1);
        detail.put("sum2", sum2);
        detail.put("sum", sum);
        
        V300.put("intRistPeriods", V300IntRistPeriodsDataList);
        V300.put("intRistPeriodsCount", V300IntRistPeriodsDataList.size());
        V300.put("detail", detail);
        V300.put("TaxPayableName", V300TaxPayableNameDataList);

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
        Map<String, Object> V500 = new HashMap<>();
        
        Map<String, Object> riskExposure = new HashMap<>();
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

        //====================↓V600↓====================
        // yury，20200909，新增外汇风险敞口及敏感性分析
        Map<String, Object> V600 = new HashMap<>();

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> V600ExposureCurrentList = (List<Map<String,Object>>)this.dao.findForList("VExportMapper.selectV600ExposureData", queryParam);
        if (V600ExposureCurrentList == null){
            V600ExposureCurrentList = new ArrayList<>();
        }
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> V600ExposureLastList = (List<Map<String,Object>>)this.dao.findForList("VExportMapper.selectV600ExposureData", queryParamLast);
        if (V600ExposureLastList == null){
            V600ExposureLastList = new ArrayList<>();
        }
        V600.put("Currentcount", V600ExposureCurrentList.size());
        V600.put("Currentlist", V600ExposureCurrentList);

        V600.put("Lastcount", V600ExposureLastList.size());
        V600.put("Lastlist", V600ExposureLastList);

        float Currentinfluence = 0;
        if (V600ExposureCurrentList != null){
            for (Map<String, Object> o: V600ExposureCurrentList){
                if (((String) o.get("item")).equals("资产负债表外汇风险敞口净额")){
                    Currentinfluence = ((BigDecimal) o.get("amount_hk")).floatValue();
                    break;
                }
            }
        }
        float Lastinfluence = 0;
        if (V600ExposureLastList != null){
            for (Map<String, Object> o: V600ExposureLastList){
                if (((String) o.get("item")).equals("资产负债表外汇风险敞口净额")){
                    Lastinfluence = ((BigDecimal) o.get("amount_hk")).floatValue();
                    break;
                }
            }
        }

        V600.put("Currentinfluence", Currentinfluence);
        V600.put("Lastinfluence", Lastinfluence);

        @SuppressWarnings("unchecked")
        List<Map<String,Object>> V600HypothesisDataList = (List<Map<String,Object>>)this.dao.findForList("VExportMapper.selectV600HypothesisDataForReport", queryParam);
        if(V600HypothesisDataList == null) {
            V600HypothesisDataList = new ArrayList<>();
        }
        V600.put("hypothesis", V600HypothesisDataList);
        V600.put("hypothesisCount", V600HypothesisDataList.size());

        // yury，20201119，v600新增敏感性风险变量导入
        List<Map<String,Object>> V600TestList = (List<Map<String,Object>>)this.dao.findForList("VExportMapper.selectV600Test", queryParam);
        if(V600TestList == null) {
            V600TestList = new ArrayList<>();
        }
        Map<String, Object> V600TestUP = new HashMap<>();
        Map<String, Object> V600TestDOWN = new HashMap<>();
        V600TestUP.put("infl_words", "");
        V600TestUP.put("infl_amount", 0);
        V600TestUP.put("infl_amount_last", 0);
        V600TestDOWN.put("infl_words", "");
        V600TestDOWN.put("infl_amount", 0);
        V600TestDOWN.put("infl_amount_last", 0);

        for (Map<String, Object> map: V600TestList){
            if ((Integer)map.get("seq") == 10){
                V600TestUP.put("infl_words", (String)map.get("infl_words"));
                V600TestUP.put("infl_amount", (BigDecimal)map.get("infl_amount"));
                V600TestUP.put("infl_amount_last", (BigDecimal)map.get("infl_amount_last"));
            }else if ((Integer)map.get("seq") == 20){
                V600TestDOWN.put("infl_words", (String)map.get("infl_words"));
                V600TestDOWN.put("infl_amount", (BigDecimal)map.get("infl_amount"));
                V600TestDOWN.put("infl_amount_last", (BigDecimal)map.get("infl_amount_last"));
            }
        }
        V600.put("V600TestUP", V600TestUP);
        V600.put("V600TestDOWN", V600TestDOWN);

        //chenhy,20240308,新增专户其他价格敏感性分析
        @SuppressWarnings("unchecked")
        Map<String,Object> V600OtherPriceSumData = (Map<String,Object>)this.dao.findForObject("VExportMapper.selectV600OtherPriceSumData", queryParam);
        if(V600OtherPriceSumData == null) {
            V600OtherPriceSumData = new HashMap<>(); 
        }

        //@SuppressWarnings("unchecked")
        //Map<String,Object> V600OtherPriceLastSumData = (Map<String,Object>)this.dao.findForObject("VExportMapper.selectV600OtherPriceSumData", queryParamLast);
        //if(V600OtherPriceLastSumData == null) {
        //    V600OtherPriceLastSumData = new HashMap<>(); 
        //}

        V600.put("otherPriceSumData",V600OtherPriceSumData);
        //V600.put("otherPriceLastSumData",V600OtherPriceLastSumData);

        //chenhy,20240308,新增专户外汇风险敏感性分析
        Object foreignRiskAmount = this.dao.findForObject("VExportMapper.selectForeignRiskAmount", queryParam);
        Object foreignRiskAmountlast = this.dao.findForObject("VExportMapper.selectForeignRiskAmount", queryParamLast);

        V600.put("foreignRiskAmount", foreignRiskAmount == null ? 0d : foreignRiskAmount);
        V600.put("foreignRiskAmountlast", foreignRiskAmountlast == null ? 0d : foreignRiskAmountlast);

        
        //====================↑V600↑====================

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
        
        P5.put("V200", V200); // yury,20201112,新增投资的长短期信用评级
        P5.put("V300", V300);
        P5.put("V400", V400);
        P5.put("V500", V500);
        P5.put("V600", V600); // yury，20200909，新增外汇风险敞口及敏感性分析
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
        Map<String, Object> res = new HashMap<>();
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
    
    /**
     * 计算map中两个对象的和<br/>
     * 计算时null会记为0<br/>
     * 计算后0会记为null
     * 
     * @param o1 对象1
     * @param o2 对象2
     * @return 结果
     */
    private BigDecimal addNumber(Object o1, Object o2) {
    	BigDecimal d1 = null, d2 = null;
    	String str1 = (o1 == null ?null : String.valueOf(o1)), str2 = (o2 == null ? null : String.valueOf(o2));
    	if(NumberUtils.isParsable(str1)) {
    		d1 = new BigDecimal(str1);
    	}
    	if(NumberUtils.isParsable(str2)) {
    		d2 = new BigDecimal(str2);
    	}
    	BigDecimal res = ObjectUtils.defaultIfNull(d1, ZERO).add(ObjectUtils.defaultIfNull(d2, ZERO));
    	if(res.equals(ZERO)) {
    		res = null;
    	}
    	return res;
    }

    /**
     * 处理转融通业务
     * @author 吴老师 2021年08月04日
     * 
     * @param queryParam
     * @return
     * @throws Exception
     */
    private Map<String,Object> processP3Sec7(Map<String,Object> queryParam) throws Exception{
        Map<String,Object> result = new HashMap<>();
        //--------------------selectIRefinancingData↓--------------------
        Map<String, Object> IRefinancingData = new HashMap<>();
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> IRefinancingDataList = (List<Map<String,Object>>)this.dao.findForList("HExportMapper.selectIRefinancingData", queryParam);
        if(IRefinancingDataList == null) {
            IRefinancingDataList = new ArrayList<>(); 
        }
        
        IRefinancingData.put("list", IRefinancingDataList);
        IRefinancingData.put("Count", IRefinancingDataList.size());
        //--------------------selectIRefinancingData↑--------------------
        result.put("IRefinancingData", IRefinancingData);
        return result;
    }
    
   
    
}