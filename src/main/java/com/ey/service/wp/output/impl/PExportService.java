package com.ey.service.wp.output.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Service;

import com.ey.service.wp.output.PExportManager;
import com.ey.util.fileexport.Constants;
import com.ey.util.fileexport.FileExportUtils;
import com.ey.util.fileexport.FreeMarkerUtils;

/**
 * @name PExportService
 * @description 底稿输出服务--P
 * @author Dai Zong	2017年9月19日
 */
@Service("pExportService")
public class PExportService extends BaseExportService implements PExportManager{

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
    private String generateFileContent(String fundId, String periodStr, Map<String, String> fundInfo) throws Exception {
        Map<String, Object> dataMap = new HashMap<String, Object>();
        
        Long period = Long.parseLong(periodStr.substring(0, 4));
        Long month = Long.parseLong(periodStr.substring(4, 6));
        Long day = Long.parseLong(periodStr.substring(6, 8));
        
        dataMap.put("period", period);
        dataMap.put("month", month);
        dataMap.put("day", day);
        dataMap.put("fundInfo", fundInfo);
        
        dataMap.put("P", this.getPData(fundId, periodStr));
        dataMap.put("P300", this.getP300Data(fundId, periodStr));
        dataMap.put("P400", this.getP400Data(fundId, periodStr));
        dataMap.put("P500", this.getP500Data(fundId, periodStr));
        dataMap.put("P600", this.getP600Data(fundId, periodStr));
        dataMap.put("P800", this.getP800Data(fundId, periodStr));
        dataMap.put("P10000", this.getP10000Data(fundId, periodStr));
        
        return FreeMarkerUtils.processTemplateToString(dataMap, Constants.EXPORT_TEMPLATE_FOLDER_PATH, Constants.EXPORT_TEMPLATE_FILE_NAME_P);
    }
    
	@Override
    public boolean doExport(HttpServletRequest request, HttpServletResponse response, String fundId, String periodStr) throws Exception {
	    Map<String, String> fundInfo = this.selectFundInfo(fundId);
        fundInfo.put("periodStr", periodStr);
        String xmlStr = this.generateFileContent(fundId, periodStr, fundInfo);
        FileExportUtils.writeFileToHttpResponse(request, response, FreeMarkerUtils.simpleReplace(Constants.EXPORT_AIM_FILE_NAME_P, fundInfo), xmlStr);
        return true;
    }
	
	@Override
    public boolean doExport(String folederName, String fileName, String fundId, String periodStr) throws Exception {
	    Map<String, String> fundInfo = this.selectFundInfo(fundId);
        fundInfo.put("periodStr", periodStr);
	    String xmlStr = this.generateFileContent(fundId, periodStr, fundInfo);
        FileExportUtils.writeFileToDisk(folederName, FreeMarkerUtils.simpleReplace(fileName, fundInfo), xmlStr);
        return true;
    }
	
	/**
     * 处理sheet页P的数据
     * @author Dai Zong 2017年9月19日
     * 
     * @param fundId
     * @param periodStr
     * @return
     * @throws Exception
     */
    private Map<String,Object> getPData(String fundId, String periodStr) throws Exception{
        Map<String, Object> queryMap = this.createBaseQueryMap(fundId, periodStr);
        Map<String, Object> result = new HashMap<String,Object>();
        
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> PMetaDataList = (List<Map<String,Object>>)this.dao.findForList("PExportMapper.selectPData", queryMap);
        if(PMetaDataList == null) {
            PMetaDataList = new ArrayList<Map<String,Object>>(); 
        }
        
        Map<String,Object> temp = new HashMap<>();
        for(Map<String,Object> map : PMetaDataList) {
            if("3003".equals(String.valueOf(map.get("accountNum")))) {
                if("可退替代款".equals(String.valueOf(map.get("revealItem")))) {
                    temp.put(String.valueOf(map.get("accountNum")) + "c", map);
                }else if ("应退替代款".equals(String.valueOf(map.get("revealItem")))) {
                    temp.put(String.valueOf(map.get("accountNum")) + "s", map);
                }
            }else {
                temp.put(String.valueOf(map.get("accountNum")), map);
            }
        }
        
        result.put("KM2241", temp.get("2241")==null?new HashMap<String,Object>():temp.get("2241"));
        result.put("KM2501", temp.get("2501")==null?new HashMap<String,Object>():temp.get("2501"));
        result.put("KM2204", temp.get("2204")==null?new HashMap<String,Object>():temp.get("2204"));
        result.put("KM2202", temp.get("2202")==null?new HashMap<String,Object>():temp.get("2202"));
        result.put("KM3003Can", temp.get("3003c")==null?new HashMap<String,Object>():temp.get("3003c"));
        result.put("KM3003Should", temp.get("3003s")==null?new HashMap<String,Object>():temp.get("3003s"));
        
        return result;
    }
    
    /**
     * 处理sheet页P300的数据
     * @author Dai Zong 2017年9月19日
     * 
     * @param fundId
     * @param periodStr
     * @return
     * @throws Exception
     */
    private Map<String,Object> getP300Data(String fundId, String periodStr) throws Exception{
        Map<String, Object> queryMap = this.createBaseQueryMap(fundId, periodStr);
        Map<String, Object> result = new HashMap<String,Object>();
        
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> P300MetaDataList = (List<Map<String,Object>>)this.dao.findForList("PExportMapper.selectP300Data", queryMap);
        if(P300MetaDataList == null) {
            P300MetaDataList = new ArrayList<Map<String,Object>>(); 
        }
        
        for(Map<String, Object> map : P300MetaDataList) {
            if("应付深圳交易所席位保证金".equals(String.valueOf(map.get("detailName")))
                    &&(Double.parseDouble(String.valueOf(map.get("drAmount"))) != 0D || Double.parseDouble(String.valueOf(map.get("crAmount"))) != 0D)) {
                map.put("hasNote", "Y");
            }else {
                map.put("hasNote", "N");
            }
        }
        
        result.put("list", P300MetaDataList);
        result.put("count", P300MetaDataList.size());
        
        return result;
    }
    
    /**
     * 处理sheet页P400的数据
     * @author Dai Zong 2017年9月21日
     * 
     * @param fundId
     * @param periodStr
     * @return
     * @throws Exception
     */
    private Map<String,Object> getP400Data(String fundId, String periodStr) throws Exception{
        Map<String, Object> queryMap = this.createBaseQueryMap(fundId, periodStr);
        Map<String, Object> result = new HashMap<String,Object>();
        
        //========process dataMap for main view begin========
        Map<String, Object> main = new HashMap<String,Object>();
        
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> P400MainMetaDataList = (List<Map<String,Object>>)this.dao.findForList("PExportMapper.selectP400MainData", queryMap);
        if(P400MainMetaDataList == null) {
            P400MainMetaDataList = new ArrayList<Map<String,Object>>(); 
        }
        
        for(Map<String, Object> map : P400MainMetaDataList) {
            if("审计费".equals(map.get("detailName")) || "信息披露费".equals(map.get("detailName"))) {
                map.put("hasV", "Y");
            }else {
                map.put("hasV", "N");
            }
        }
        
        main.put("list", P400MainMetaDataList);
        main.put("count", P400MainMetaDataList.size());
        
        result.put("main", main);
        //========process dataMap for main view end========
        
        //========process dataMap for summary view begin========
        Map<String, Object> summary = new HashMap<String,Object>();
        
        Map<String, Object> annualFee4Listing = new HashMap<String,Object>();
        Map<String, Object> auditFee = new HashMap<String,Object>();
        Map<String, Object> subtotal = new HashMap<String,Object>();
        
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> P400SummaryMetaDataList = (List<Map<String,Object>>)this.dao.findForList("PExportMapper.selectP400SummaryData", queryMap);
        if(P400SummaryMetaDataList == null) {
            P400SummaryMetaDataList = new ArrayList<Map<String,Object>>(); 
        }
        
        for(Map<String, Object> map : P400SummaryMetaDataList) {
            if("上市年费".equals(map.get("item"))) {
                annualFee4Listing = map;
            }else if("审计费".equals(map.get("item"))) {
                auditFee = map;
            }else if ("小计".equals(map.get("item"))) {
                subtotal = map;
            }
        }
        
        summary.put("annualFee4Listing", annualFee4Listing);
        summary.put("auditFee", auditFee);
        summary.put("subtotal", subtotal);
        
        result.put("summary", summary);
        //========process dataMap for summary view end========
        //========process dataMap for detail view end========
        Map<String, Object> detail = new HashMap<String,Object>();
        
        
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> P400DetailMetaDataList = (List<Map<String,Object>>)this.dao.findForList("PExportMapper.selectP400DetailData", queryMap);
        if(P400DetailMetaDataList == null) {
            P400DetailMetaDataList = new ArrayList<Map<String,Object>>(); 
        }
        
        detail.put("list", P400DetailMetaDataList);
        detail.put("count", P400DetailMetaDataList.size());
        
        result.put("detail", detail);
        //========process dataMap for detail view end========
        
        return result;
    }
    
    /**
     * 处理sheet页P500的数据
     * @author Dai Zong 2017年9月19日
     * 
     * @param fundId
     * @param periodStr
     * @return
     * @throws Exception
     */
    private Map<String,Object> getP500Data(String fundId, String periodStr) throws Exception{
        Map<String, Object> queryMap = this.createBaseQueryMap(fundId, periodStr);
        Map<String, Object> result = new HashMap<String,Object>();
        
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> P500MetaDataList = (List<Map<String,Object>>)this.dao.findForList("PExportMapper.selectP500Data", queryMap);
        if(P500MetaDataList == null) {
            P500MetaDataList = new ArrayList<Map<String,Object>>(); 
        }
        
        result.put("list", P500MetaDataList);
        result.put("count", P500MetaDataList.size());
        
        return result;
    }
    
    /**
     * 处理sheet页P600的数据
     * @author Dai Zong 2017年9月19日
     * 
     * @param fundId
     * @param periodStr
     * @return
     * @throws Exception
     */
    private Map<String,Object> getP600Data(String fundId, String periodStr) throws Exception{
        Map<String, Object> queryMap = this.createBaseQueryMap(fundId, periodStr);
        Map<String, Object> result = new HashMap<String,Object>();
        //========process dataMap for main view begin========
        Map<String, Object> main = new HashMap<String,Object>();
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> P600MainMetaDataList = (List<Map<String,Object>>)this.dao.findForList("PExportMapper.selectP600Data", queryMap);
        if(P600MainMetaDataList == null) {
            P600MainMetaDataList = new ArrayList<Map<String,Object>>(); 
        }
        main.put("list", P600MainMetaDataList);
        main.put("count", P600MainMetaDataList.size());
        //========process dataMap for main view end========
        
        //========process dataMap for test view begin========
        Map<String, Object> test = new HashMap<String,Object>();
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> P600TestMetaDataList = (List<Map<String,Object>>)this.dao.findForList("PExportMapper.selectP600TestData", queryMap);
        if(P600TestMetaDataList == null) {
            P600TestMetaDataList = new ArrayList<Map<String,Object>>(); 
        }
        test.put("list", P600TestMetaDataList);
        test.put("count", P600TestMetaDataList.size());
        //========process dataMap for test view end========
        
        //========process dataMap for testDetail view begin========
        Map<String, Object> testDetail = new HashMap<String,Object>();
        Map<String, Object> note2 = new HashMap<String,Object>();
        Map<String, Object> note3 = new HashMap<String,Object>();
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> P600TestDetailMetaDataList = (List<Map<String,Object>>)this.dao.findForList("PExportMapper.selectP600TestDetailData", queryMap);
        if(P600TestDetailMetaDataList == null) {
            P600TestDetailMetaDataList = new ArrayList<Map<String,Object>>(); 
        }
        Map<String, List<Map<String, Object>>> groups = P600TestDetailMetaDataList.parallelStream().collect(Collectors.groupingBy(item -> {
            Map<String,Object> map = item;
            return String.valueOf(map.get("byOutBond"));
        }));
        List<Map<String,Object>> note2List = new ArrayList<>();
        List<Map<String,Object>> note3List = new ArrayList<>();
        if(groups.get("Note 2") != null) {
            note2List = groups.get("Note 2");
        }
        if(groups.get("Note 3") != null) {
            note3List = groups.get("Note 3");
        }
        note2.put("list", note2List);
        note2.put("count", note2List.size());
        note3.put("list", note3List);
        note3.put("count", note3List.size());
        testDetail.put("note2", note2);
        testDetail.put("note3", note3);
        //========process dataMap for testDetail view end========
        
        //========process dataMap for exposurePeriod view begin========
        Map<String, Object> exposurePeriod = new HashMap<String,Object>();
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> P600ExposurePeriodMetaDataList = (List<Map<String,Object>>)this.dao.findForList("PExportMapper.selectP600ExposurePeriodData", queryMap);
        if(P600ExposurePeriodMetaDataList == null) {
            P600ExposurePeriodMetaDataList = new ArrayList<Map<String,Object>>(); 
        }
        exposurePeriod.put("list", P600ExposurePeriodMetaDataList);
        exposurePeriod.put("count", P600ExposurePeriodMetaDataList.size());
        //========process dataMap for exposurePeriod view end========
        
        result.put("main", main);
        result.put("test", test);
        result.put("testDetail", testDetail);
        result.put("exposurePeriod", exposurePeriod);
        
        return result;
    }
    
    /**
     * 处理sheet页P800的数据
     * @author Dai Zong 2017年9月19日
     * 
     * @param fundId
     * @param periodStr
     * @return
     * @throws Exception
     */
    private Map<String,Object> getP800Data(String fundId, String periodStr) throws Exception{
        Map<String, Object> queryMap = this.createBaseQueryMap(fundId, periodStr);
        Map<String, Object> result = new HashMap<String,Object>();
        
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> P800MetaDataList = (List<Map<String,Object>>)this.dao.findForList("PExportMapper.selectP800Data", queryMap);
        if(P800MetaDataList == null) {
            P800MetaDataList = new ArrayList<Map<String,Object>>(); 
        }
        
        result.put("list", P800MetaDataList);
        result.put("count", P800MetaDataList.size());
        
        return result;
    }
    
    /**
     * 处理sheet页P10000的数据
     * @author Dai Zong 2017年9月19日
     * 
     * @param fundId
     * @param periodStr
     * @return
     * @throws Exception
     */
    private Map<String,Object> getP10000Data(String fundId, String periodStr) throws Exception{
        Map<String, Object> queryMap = this.createBaseQueryMap(fundId, periodStr);
        Map<String, Object> result = new HashMap<String,Object>();
        
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> P10000MetaDataList = (List<Map<String,Object>>)this.dao.findForList("PExportMapper.selectP10000Data", queryMap);
        if(P10000MetaDataList == null) {
            P10000MetaDataList = new ArrayList<Map<String,Object>>(); 
        }
        
        result.put("list", P10000MetaDataList);
        result.put("count", P10000MetaDataList.size());
        
        return result;
    }
}