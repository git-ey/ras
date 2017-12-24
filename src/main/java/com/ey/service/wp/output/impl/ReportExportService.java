package com.ey.service.wp.output.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.ey.dao.DaoSupport;
import com.ey.service.wp.output.ReportExportManager;
import com.ey.util.DocUtil;
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
        
        exportParam.put("reportExtendInfo", reportExtendInfo);
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

        Double sum1current = 0d;
        Double sum1last = 0d;
        Double sum2current = 0d;
        Double sum2last = 0d;
        Double sum3current = 0d;
        Double sum3last = 0d;
        
        for(Map<String,Object> map : BsMetaDataList) {
            if ("A01".equals(map.get("bsCode"))) {
                A01 = map;
                sum1current += Double.parseDouble(String.valueOf(A01.get("beginBalance")==null?0:A01.get("beginBalance")));
                sum1last += Double.parseDouble(String.valueOf(A01.get("endBalance")==null?0:A01.get("endBalance")));
            } else if ("A02".equals(map.get("bsCode"))) {
                A02 = map;
                sum1current += Double.parseDouble(String.valueOf(A02.get("beginBalance")==null?0:A02.get("beginBalance")));
                sum1last += Double.parseDouble(String.valueOf(A02.get("endBalance")==null?0:A02.get("endBalance")));
            } else if ("A03".equals(map.get("bsCode"))) {
                A03 = map;
                sum1current += Double.parseDouble(String.valueOf(A03.get("beginBalance")==null?0:A03.get("beginBalance")));
                sum1last += Double.parseDouble(String.valueOf(A03.get("endBalance")==null?0:A03.get("endBalance")));
            } else if ("A04".equals(map.get("bsCode"))) {
                A04 = map;
            } else if ("A05".equals(map.get("bsCode"))) {
                A05 = map;
                sum1current += Double.parseDouble(String.valueOf(A05.get("beginBalance")==null?0:A05.get("beginBalance")));
                sum1last += Double.parseDouble(String.valueOf(A05.get("endBalance")==null?0:A05.get("endBalance")));
            } else if ("A06".equals(map.get("bsCode"))) {
                A06 = map;
                sum1current += Double.parseDouble(String.valueOf(A06.get("beginBalance")==null?0:A06.get("beginBalance")));
                sum1last += Double.parseDouble(String.valueOf(A06.get("endBalance")==null?0:A06.get("endBalance")));
            } else if ("A07".equals(map.get("bsCode"))) {
                A07 = map;
                sum1current += Double.parseDouble(String.valueOf(A07.get("beginBalance")==null?0:A07.get("beginBalance")));
                sum1last += Double.parseDouble(String.valueOf(A07.get("endBalance")==null?0:A07.get("endBalance")));
            } else if ("A08".equals(map.get("bsCode"))) {
                A08 = map;
                sum1current += Double.parseDouble(String.valueOf(A08.get("beginBalance")==null?0:A08.get("beginBalance")));
                sum1last += Double.parseDouble(String.valueOf(A08.get("endBalance")==null?0:A08.get("endBalance")));
            } else if ("A09".equals(map.get("bsCode"))) {
                A09 = map;
                sum1current += Double.parseDouble(String.valueOf(A09.get("beginBalance")==null?0:A09.get("beginBalance")));
                sum1last += Double.parseDouble(String.valueOf(A09.get("endBalance")==null?0:A09.get("endBalance")));
            } else if ("A10".equals(map.get("bsCode"))) {
                A10 = map;
                sum1current += Double.parseDouble(String.valueOf(A10.get("beginBalance")==null?0:A10.get("beginBalance")));
                sum1last += Double.parseDouble(String.valueOf(A10.get("endBalance")==null?0:A10.get("endBalance")));
            } else if ("A11".equals(map.get("bsCode"))) {
                A11 = map;
                sum1current += Double.parseDouble(String.valueOf(A11.get("beginBalance")==null?0:A11.get("beginBalance")));
                sum1last += Double.parseDouble(String.valueOf(A11.get("endBalance")==null?0:A11.get("endBalance")));
            } else if ("A12".equals(map.get("bsCode"))) {
                A12 = map;
                sum1current += Double.parseDouble(String.valueOf(A12.get("beginBalance")==null?0:A12.get("beginBalance")));
                sum1last += Double.parseDouble(String.valueOf(A12.get("endBalance")==null?0:A12.get("endBalance")));
            } else if ("A13".equals(map.get("bsCode"))) {
                A13 = map;
                sum1current += Double.parseDouble(String.valueOf(A13.get("beginBalance")==null?0:A13.get("beginBalance")));
                sum1last += Double.parseDouble(String.valueOf(A13.get("endBalance")==null?0:A13.get("endBalance")));
            } else if ("A14".equals(map.get("bsCode"))) {
                A14 = map;
                sum1current += Double.parseDouble(String.valueOf(A14.get("beginBalance")==null?0:A14.get("beginBalance")));
                sum1last += Double.parseDouble(String.valueOf(A14.get("endBalance")==null?0:A14.get("endBalance")));
            } else if ("A15".equals(map.get("bsCode"))) {
                A15 = map;
                sum1current += Double.parseDouble(String.valueOf(A15.get("beginBalance")==null?0:A15.get("beginBalance")));
                sum1last += Double.parseDouble(String.valueOf(A15.get("endBalance")==null?0:A15.get("endBalance")));
            } else if ("A17".equals(map.get("bsCode"))) {
                A17 = map;
                sum1current += Double.parseDouble(String.valueOf(A17.get("beginBalance")==null?0:A17.get("beginBalance")));
                sum1last += Double.parseDouble(String.valueOf(A17.get("endBalance")==null?0:A17.get("endBalance")));
            } else if ("B01".equals(map.get("bsCode"))) {
                B01 = map;
                sum2current += Double.parseDouble(String.valueOf(B01.get("beginBalance")==null?0:B01.get("beginBalance")));
                sum2last += Double.parseDouble(String.valueOf(B01.get("endBalance")==null?0:B01.get("endBalance")));
            } else if ("B02".equals(map.get("bsCode"))) {
                B02 = map;
                sum2current += Double.parseDouble(String.valueOf(B02.get("beginBalance")==null?0:B02.get("beginBalance")));
                sum2last += Double.parseDouble(String.valueOf(B02.get("endBalance")==null?0:B02.get("endBalance")));
            } else if ("B03".equals(map.get("bsCode"))) {
                B03 = map;
                sum2current += Double.parseDouble(String.valueOf(B03.get("beginBalance")==null?0:B03.get("beginBalance")));
                sum2last += Double.parseDouble(String.valueOf(B03.get("endBalance")==null?0:B03.get("endBalance")));
            } else if ("B04".equals(map.get("bsCode"))) {
                B04 = map;
                sum2current += Double.parseDouble(String.valueOf(B04.get("beginBalance")==null?0:B04.get("beginBalance")));
                sum2last += Double.parseDouble(String.valueOf(B04.get("endBalance")==null?0:B04.get("endBalance")));
            } else if ("B05".equals(map.get("bsCode"))) {
                B05 = map;
                sum2current += Double.parseDouble(String.valueOf(B05.get("beginBalance")==null?0:B05.get("beginBalance")));
                sum2last += Double.parseDouble(String.valueOf(B05.get("endBalance")==null?0:B05.get("endBalance")));
            } else if ("B06".equals(map.get("bsCode"))) {
                B06 = map;
                sum2current += Double.parseDouble(String.valueOf(B06.get("beginBalance")==null?0:B06.get("beginBalance")));
                sum2last += Double.parseDouble(String.valueOf(B06.get("endBalance")==null?0:B06.get("endBalance")));
            } else if ("B07".equals(map.get("bsCode"))) {
                B07 = map;
                sum2current += Double.parseDouble(String.valueOf(B07.get("beginBalance")==null?0:B07.get("beginBalance")));
                sum2last += Double.parseDouble(String.valueOf(B07.get("endBalance")==null?0:B07.get("endBalance")));
            } else if ("B08".equals(map.get("bsCode"))) {
                B08 = map;
                sum2current += Double.parseDouble(String.valueOf(B08.get("beginBalance")==null?0:B08.get("beginBalance")));
                sum2last += Double.parseDouble(String.valueOf(B08.get("endBalance")==null?0:B08.get("endBalance")));
            } else if ("B09".equals(map.get("bsCode"))) {
                B09 = map;
                sum2current += Double.parseDouble(String.valueOf(B09.get("beginBalance")==null?0:B09.get("beginBalance")));
                sum2last += Double.parseDouble(String.valueOf(B09.get("endBalance")==null?0:B09.get("endBalance")));
            } else if ("B10".equals(map.get("bsCode"))) {
                B10 = map;
                sum2current += Double.parseDouble(String.valueOf(B10.get("beginBalance")==null?0:B10.get("beginBalance")));
                sum2last += Double.parseDouble(String.valueOf(B10.get("endBalance")==null?0:B10.get("endBalance")));
            } else if ("B11".equals(map.get("bsCode"))) {
                B11 = map;
                sum2current += Double.parseDouble(String.valueOf(B11.get("beginBalance")==null?0:B11.get("beginBalance")));
                sum2last += Double.parseDouble(String.valueOf(B11.get("endBalance")==null?0:B11.get("endBalance")));
            } else if ("B12".equals(map.get("bsCode"))) {
                B12 = map;
                sum2current += Double.parseDouble(String.valueOf(B12.get("beginBalance")==null?0:B12.get("beginBalance")));
                sum2last += Double.parseDouble(String.valueOf(B12.get("endBalance")==null?0:B12.get("endBalance")));
            } else if ("B13".equals(map.get("bsCode"))) {
                B13 = map;
                sum2current += Double.parseDouble(String.valueOf(B13.get("beginBalance")==null?0:B13.get("beginBalance")));
                sum2last += Double.parseDouble(String.valueOf(B13.get("endBalance")==null?0:B13.get("endBalance")));
            } else if ("B15".equals(map.get("bsCode"))) {
                B15 = map;
                sum2current += Double.parseDouble(String.valueOf(B15.get("beginBalance")==null?0:B15.get("beginBalance")));
                sum2last += Double.parseDouble(String.valueOf(B15.get("endBalance")==null?0:B15.get("endBalance")));
            } else if ("C01".equals(map.get("bsCode"))) {
                C01 = map;
                sum3current += Double.parseDouble(String.valueOf(C01.get("beginBalance")==null?0:C01.get("beginBalance")));
                sum3last += Double.parseDouble(String.valueOf(C01.get("endBalance")==null?0:C01.get("endBalance")));
            } else if ("C02".equals(map.get("bsCode"))) {
                C02 = map;
                sum3current += Double.parseDouble(String.valueOf(C02.get("beginBalance")==null?0:C02.get("beginBalance")));
                sum3last += Double.parseDouble(String.valueOf(C02.get("endBalance")==null?0:C02.get("endBalance")));
            }
        }
        SUM1.put("beginBalance", sum1current);
        SUM1.put("endBalance", sum1last);
        SUM2.put("beginBalance", sum2current);
        SUM2.put("endBalance", sum2last);
        SUM3.put("beginBalance", sum3current);
        SUM3.put("endBalance", sum3last);
        SUM4.put("beginBalance", sum2current + sum3current);
        SUM4.put("endBalance", sum2last + sum3last);
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
        Map<String,Object> D18 = new HashMap<>();
        Map<String,Object> D19 = new HashMap<>();
        Map<String,Object> D20 = new HashMap<>();
        Map<String,Object> D21 = new HashMap<>();
        Map<String,Object> D22 = new HashMap<>();
        Map<String,Object> D23 = new HashMap<>();
        Map<String,Object> D24 = new HashMap<>();
        SUM1 = new HashMap<>();
        SUM2 = new HashMap<>();
        
        sum1current = new Double(0d);
        sum1last = new Double(0d);
        sum2current = new Double(0d);
        sum2last = new Double(0d);
        
        for(Map<String,Object> map : PlMetaDataList) {
            if ("D01".equals(map.get("plCode"))) {
                D01 = map;
                sum1current += Double.parseDouble(String.valueOf(D01.get("periodAcct")==null?0:D01.get("periodAcct")));
                sum1last += Double.parseDouble(String.valueOf(D01.get("lastPeriodAcct")==null?0:D01.get("lastPeriodAcct")));
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
                sum1current += Double.parseDouble(String.valueOf(D07.get("periodAcct")==null?0:D07.get("periodAcct")));
                sum1last += Double.parseDouble(String.valueOf(D07.get("lastPeriodAcct")==null?0:D07.get("lastPeriodAcct")));
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
                sum1current += Double.parseDouble(String.valueOf(D15.get("periodAcct")==null?0:D15.get("periodAcct")));
                sum1last += Double.parseDouble(String.valueOf(D15.get("lastPeriodAcct")==null?0:D15.get("lastPeriodAcct")));
            } else if ("D16".equals(map.get("plCode"))) {
                D16 = map;
                sum1current += Double.parseDouble(String.valueOf(D16.get("periodAcct")==null?0:D16.get("periodAcct")));
                sum1last += Double.parseDouble(String.valueOf(D16.get("lastPeriodAcct")==null?0:D16.get("lastPeriodAcct")));
            } else if ("D17".equals(map.get("plCode"))) {
                D17 = map;
                sum1current += Double.parseDouble(String.valueOf(D17.get("periodAcct")==null?0:D17.get("periodAcct")));
                sum1last += Double.parseDouble(String.valueOf(D17.get("lastPeriodAcct")==null?0:D17.get("lastPeriodAcct")));
            } else if ("D18".equals(map.get("plCode"))) {
                D18 = map;
                sum2current += Double.parseDouble(String.valueOf(D18.get("periodAcct")==null?0:D18.get("periodAcct")));
                sum2last += Double.parseDouble(String.valueOf(D18.get("lastPeriodAcct")==null?0:D18.get("lastPeriodAcct")));
            } else if ("D19".equals(map.get("plCode"))) {
                D19 = map;
                sum2current += Double.parseDouble(String.valueOf(D19.get("periodAcct")==null?0:D19.get("periodAcct")));
                sum2last += Double.parseDouble(String.valueOf(D19.get("lastPeriodAcct")==null?0:D19.get("lastPeriodAcct")));
            } else if ("D20".equals(map.get("plCode"))) {
                D20 = map;
                sum2current += Double.parseDouble(String.valueOf(D20.get("periodAcct")==null?0:D20.get("periodAcct")));
                sum2last += Double.parseDouble(String.valueOf(D20.get("lastPeriodAcct")==null?0:D20.get("lastPeriodAcct")));
            } else if ("D21".equals(map.get("plCode"))) {
                D21 = map;
                sum2current += Double.parseDouble(String.valueOf(D21.get("periodAcct")==null?0:D21.get("periodAcct")));
                sum2last += Double.parseDouble(String.valueOf(D21.get("lastPeriodAcct")==null?0:D21.get("lastPeriodAcct")));
            } else if ("D22".equals(map.get("plCode"))) {
                D22 = map;
                sum2current += Double.parseDouble(String.valueOf(D22.get("periodAcct")==null?0:D22.get("periodAcct")));
                sum2last += Double.parseDouble(String.valueOf(D22.get("lastPeriodAcct")==null?0:D22.get("lastPeriodAcct")));
            } else if ("D23".equals(map.get("plCode"))) {
                D23 = map;
                sum2current += Double.parseDouble(String.valueOf(D23.get("periodAcct")==null?0:D23.get("periodAcct")));
                sum2last += Double.parseDouble(String.valueOf(D23.get("lastPeriodAcct")==null?0:D23.get("lastPeriodAcct")));
            } else if ("D24".equals(map.get("plCode"))) {
                D24 = map;
                sum2current += Double.parseDouble(String.valueOf(D24.get("periodAcct")==null?0:D24.get("periodAcct")));
                sum2last += Double.parseDouble(String.valueOf(D24.get("lastPeriodAcct")==null?0:D24.get("lastPeriodAcct")));
            }
        }
        
        SUM1.put("periodAcct", sum1current);
        SUM1.put("lastPeriodAcct", sum1last);
        SUM2.put("periodAcct", sum2current);
        SUM2.put("lastPeriodAcct", sum2last);
        
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
        PL.put("D21", D21);
        PL.put("D22", D22);
        PL.put("D23", D23);
        PL.put("D24", D24);
        PL.put("SUM1", SUM1);
        PL.put("SUM2", SUM2);

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
            if("股指期货".equals(map.get("item"))) {
                item1 = map;
            }else if("国债期货".equals(map.get("item"))){
                item2 = map;
            }else if("黄金延期合约".equals(map.get("item"))){
                item3 = map;
            }
        }
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> H10000futuresLastDataList = (List<Map<String,Object>>)this.dao.findForList("HExportMapper.selectH10000FuturesData", queryParamLast);
        if(H10000futuresLastDataList == null) {
            H10000futuresLastDataList = new ArrayList<>(); 
        }
        for(Map<String,Object> map : H10000futuresLastDataList) {
            if("股指期货".equals(map.get("item"))) {
                item4 = map;
            }else if("国债期货".equals(map.get("item"))){
                item5 = map;
            }else if("黄金延期合约".equals(map.get("item"))){
                item6 = map;
            }
        }
        @SuppressWarnings("unchecked")
        Map<String,Object> H10000futuresSumData = (Map<String,Object>)this.dao.findForObject("HExportMapper.selectH10000FuturesSumDataForReport", queryParam);
        if(H10000futuresSumData == null) {
            H10000futuresSumData = new HashMap<>(); 
        }
        @SuppressWarnings("unchecked")
        Map<String,Object> H10000futuresSumLastData = (Map<String,Object>)this.dao.findForObject("HExportMapper.selectH10000FuturesSumDataForReport", queryParam);
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
        H10000.put("rmcfs", rmcfs);
        //--------------------↑H10000.rmcfs↑--------------------
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
        //====================↑G10000↑====================
        
        result.put("C10000", C10000);
        result.put("H10000", H10000);
        result.put("H800", H800);
        result.put("E300", E300);
        result.put("G10000", G10000);
        result.put("N10000", N10000);
        result.put("P10000", P10000);
        return result;
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
            sum1LineAmountSum += sum1Map.get("amount");
            sum1LineAmountLastSum += sum1Map.get("amountLast");
        }
        Double sum2LineAmountSum = new Double(0d);
        Double sum2LineAmountLastSum = new Double(0d);
        for(Map<String,Double> sum2Map : sum2List) {
            sum2LineAmountSum += sum2Map.get("amount");
            sum2LineAmountLastSum += sum2Map.get("amountLast");
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
        Map<String,Object> fundInfo = (Map<String,Object>)this.dao.findForObject("VExportMapper.selectV400FundInfoData", queryParam);
        if(fundInfo == null) {
            fundInfo = new HashMap<>();
        }
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> V400HypothesisDataList = (List<Map<String,Object>>)this.dao.findForList("VExportMapper.selectV400HypothesisDataForReport", queryParam);
        if(V400HypothesisDataList == null) {
            V400HypothesisDataList = new ArrayList<>(); 
        }
        @SuppressWarnings("unchecked")
        Map<String,Object> summaryCurrent = (Map<String,Object>)this.dao.findForObject("VExportMapper.selectV400SummaryData", queryParam);
        if(summaryCurrent == null) {
            summaryCurrent = new HashMap<>(); 
        }
        @SuppressWarnings("unchecked")
        Map<String,Object> summaryLast = (Map<String,Object>)this.dao.findForObject("VExportMapper.selectV400SummaryData", queryParamLast);
        if(summaryLast == null) {
            summaryLast = new HashMap<>(); 
        }
        
        V400.put("fundInfo", fundInfo);
        V400.put("hypothesis", V400HypothesisDataList);
        V400.put("hypothesisCount", V400HypothesisDataList.size());
        V400.put("summaryCurrent", summaryCurrent);
        V400.put("summaryLast", summaryLast);
        //====================↑V400↑====================
        
        //====================↓V500↓====================
        Map<String, Object> V500 = new HashMap<String,Object>();
        
        Map<String, Object> riskExposure = new HashMap<String,Object>();
        attr1 = new HashMap<String,Object>();
        attr2 = new HashMap<String,Object>();
        attr3 = new HashMap<String,Object>();
        attr4 = new HashMap<String,Object>();
        attr5 = new HashMap<String,Object>();
        attr6 = new HashMap<String,Object>();
        sum = new HashMap<String,Object>();
        Double netValue = 0D;
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> V500riskExposureMetaDataList = (List<Map<String,Object>>)this.dao.findForList("VExportMapper.selectV500riskExposureData", queryParam);
        if(CollectionUtils.isEmpty(V500riskExposureMetaDataList)) {
            V500riskExposureMetaDataList = new ArrayList<>(); 
        }else {
            netValue = Double.parseDouble(String.valueOf(V500riskExposureMetaDataList.get(0).get("netValue")));
        }
        Map<String,Map<String,Object>> temp = new HashMap<>();
        for(Map<String,Object> map : V500riskExposureMetaDataList) {
            temp.put(String.valueOf(map.get("item")), map);
        }
        for(Entry<String, Map<String,Object>> entry : temp.entrySet()) {
            if ("交易性金融资产-股票投资".equals(entry.getKey())) {
                attr1 = entry.getValue();
            } else if ("交易性金融资产-基金投资".equals(entry.getKey())) {
                attr2 = entry.getValue();
            } else if ("交易性金融资产-债券投资".equals(entry.getKey())) {
                attr3 = entry.getValue();
            } else if ("交易性金融资产-贵金属投资".equals(entry.getKey())) {
                attr4 = entry.getValue();
            } else if ("衍生金融资产-权证投资".equals(entry.getKey())) {
                attr5 = entry.getValue();
            } else if ("其他".equals(entry.getKey())) {
                attr6 = entry.getValue();
            }
        }
        riskExposure.put("attr1", attr1);
        riskExposure.put("attr2", attr2);
        riskExposure.put("attr3", attr3);
        riskExposure.put("attr4", attr4);
        riskExposure.put("attr5", attr5);
        riskExposure.put("attr6", attr6);
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
        Map<String,Object> summaryCurrent2 = (Map<String,Object>)this.dao.findForObject("VExportMapper.selectV500SummaryData", queryParam);
        if(summaryCurrent2 == null) {
            summaryCurrent2 = new HashMap<>(); 
        }
        @SuppressWarnings("unchecked")
        Map<String,Object> summaryLast2 = (Map<String,Object>)this.dao.findForObject("VExportMapper.selectV500SummaryData", queryParamLast);
        if(summaryLast2 == null) {
            summaryLast2 = new HashMap<>(); 
        }
        
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
        List<Map<String,Object>> H10000ThreeLevelMetaDataList = (List<Map<String,Object>>)this.dao.findForList("HExportMapper.selectH10000ThreeLevelData", queryParam);
        if(H10000ThreeLevelMetaDataList == null) {
            H10000ThreeLevelMetaDataList = new ArrayList<>(); 
        }
        Map<String,Object> level1 = new HashMap<>();
        Map<String,Object> level2 = new HashMap<>();
        Map<String,Object> level3 = new HashMap<>();
        for(Map<String,Object> map : H10000ThreeLevelMetaDataList) {
            if("一层次".equals(String.valueOf(map.get("threeLevel")))) {
                level1 = map;
            }else if("二层次".equals(String.valueOf(map.get("threeLevel")))) {
                level2 = map;
            }else if("三层次".equals(String.valueOf(map.get("threeLevel")))) {
                level3 = map;
            }
        }
        threeLevel.put("level1", level1);
        threeLevel.put("level2", level2);
        threeLevel.put("level3", level3);
        
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