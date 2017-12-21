package com.ey.service.wp.output.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

import freemarker.template.TemplateException;

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
    
    private void processParts(Map<String, Object> exportParam, Map<String,Object> content, Map<String,Object> queryParam) throws Exception{
        @SuppressWarnings("unchecked")
        Map<String,Object> partName = (Map<String,Object>)exportParam.get("partName");
        content.put("P1", this.processP1(exportParam, partName, queryParam));
        content.put("P2", this.processP2(exportParam, partName));
        content.put("P3", this.processP3(exportParam, partName, queryParam));
        content.put("P4", this.processP4(exportParam, partName));
        content.put("P5", this.processP5(exportParam, partName, queryParam));
    }
    
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
    
    private String processP2(Map<String,Object> exportParam, Map<String,Object> partName) throws IOException{
        String xml2003Content = DocUtil.getXml2003Content(String.valueOf(exportParam.get("reportTempRootPath")) + String.valueOf(partName.get("P2")), "<w:body><wx:sect><wx:sub-section>(.*)</wx:sub-section>", 1);
        if(StringUtils.isEmpty(xml2003Content)) {
            throw new IOException("Can not get content from P2 template");
        }
        return xml2003Content;
    }
    
    private String processP3(Map<String,Object> exportParam, Map<String,Object> partName, Map<String,Object> queryParam) throws IOException, TemplateException{
        return FreeMarkerUtils.processTemplateToStrUseAbsPath(exportParam, String.valueOf(exportParam.get("reportTempRootPath")), String.valueOf(partName.get("P3")));
    }
    
    private String processP4(Map<String,Object> exportParam, Map<String,Object> partName) throws IOException{
        String xml2003Content = DocUtil.getXml2003Content(String.valueOf(exportParam.get("reportTempRootPath")) + String.valueOf(partName.get("P4")), "<w:body><wx:sect><wx:sub-section>(.*)</wx:sub-section>", 1);
        if(StringUtils.isEmpty(xml2003Content)) {
            throw new IOException("Can not get content from P4 template");
        }
        return xml2003Content;
    }
    
    private String processP5(Map<String,Object> exportParam, Map<String,Object> partName, Map<String,Object> queryParam) throws IOException, TemplateException{
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