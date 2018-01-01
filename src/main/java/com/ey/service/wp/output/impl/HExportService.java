package com.ey.service.wp.output.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.ey.service.wp.output.HExportManager;
import com.ey.util.fileexport.Constants;
import com.ey.util.fileexport.FileExportUtils;
import com.ey.util.fileexport.FreeMarkerUtils;

/**
 * @name HExportService
 * @description 底稿输出服务--H
 * @author Dai Zong	2017年12月11日
 */
@Service("hExportService")
public class HExportService extends BaseExportService implements HExportManager{

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
        dataMap.put("extraFundInfo", this.getExtraFundInfo(fundId, periodStr));
        
        dataMap.put("H", this.getHData(fundId, periodStr));
        dataMap.put("H300", this.getH300Data(fundId, periodStr));
        dataMap.put("H400", this.getH400Data(fundId, periodStr));
        dataMap.put("H500", this.getH500Data(fundId, periodStr));
        dataMap.put("H800", this.getH800Data(fundId, periodStr));
        dataMap.put("H10000", this.getH10000Data(fundId, periodStr));
        dataMap.put("H11000", this.getH11000Data(fundId, periodStr));
        
        return FreeMarkerUtils.processTemplateToString(dataMap, Constants.EXPORT_TEMPLATE_FOLDER_PATH, Constants.EXPORT_TEMPLATE_FILE_NAME_H);
    }
    
	@Override
    public boolean doExport(HttpServletRequest request, HttpServletResponse response, String fundId, String periodStr) throws Exception {
	    Map<String, String> fundInfo = this.selectFundInfo(fundId);
        fundInfo.put("periodStr", periodStr);
        String xmlStr = this.generateFileContent(fundId, periodStr, fundInfo);
        FileExportUtils.writeFileToHttpResponse(request, response, FreeMarkerUtils.simpleReplace(Constants.EXPORT_AIM_FILE_NAME_H, fundInfo), xmlStr);
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
     * 处理sheet页H的数据
     * @author Dai Zong 2017年12月11日
     * 
     * @param fundId
     * @param periodStr
     * @return
     * @throws Exception
     */
    private Map<String,Object> getHData(String fundId, String periodStr) throws Exception{
        Map<String, Object> queryMap = this.createBaseQueryMap(fundId, periodStr);
        Map<String, Object> result = new HashMap<String,Object>();
        
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> metaDataList = (List<Map<String,Object>>)this.dao.findForList("HExportMapper.selectHData", queryMap);
        if(metaDataList == null) {
            metaDataList = new ArrayList<>();
        }
        Map<String, Map<String, List<Map<String, Object>>>> groups = metaDataList.stream().collect(Collectors.groupingBy(item -> {
            Map<String,Object> map = (Map<String,Object>)item;
            return String.valueOf(map.get("item"));
        }, Collectors.groupingBy(item -> {
            Map<String,Object> map = (Map<String,Object>)item;
            return String.valueOf(map.get("type"));
        })));
        
        Map<String, List<Map<String, Object>>> temp = null;
        temp = groups.get("股票投资")==null ? new HashMap<>() : groups.get("股票投资");
        Map<String,Object> attr1 = CollectionUtils.isEmpty(temp.get("COST")) ? new HashMap<>() : temp.get("COST").get(0);
        Map<String,Object> attr2 = CollectionUtils.isEmpty(temp.get("MKT_VALUE")) ? new HashMap<>() : temp.get("MKT_VALUE").get(0);
        Map<String,Object> attr3 = CollectionUtils.isEmpty(temp.get("APPRECIATION")) ? new HashMap<>() : temp.get("APPRECIATION").get(0);
        temp = groups.get("债券投资")==null ? new HashMap<>() : groups.get("债券投资");
        Map<String,Object> attr4 = CollectionUtils.isEmpty(temp.get("COST")) ? new HashMap<>() : temp.get("COST").get(0);
        Map<String,Object> attr5 = CollectionUtils.isEmpty(temp.get("MKT_VALUE")) ? new HashMap<>() : temp.get("MKT_VALUE").get(0);
        Map<String,Object> attr6 = CollectionUtils.isEmpty(temp.get("APPRECIATION")) ? new HashMap<>() : temp.get("APPRECIATION").get(0);
        temp = groups.get("资产支持性证券投资")==null ? new HashMap<>() : groups.get("资产支持性证券投资");
        Map<String,Object> attr7 = CollectionUtils.isEmpty(temp.get("COST")) ? new HashMap<>() : temp.get("COST").get(0);
        Map<String,Object> attr8 = CollectionUtils.isEmpty(temp.get("MKT_VALUE")) ? new HashMap<>() : temp.get("MKT_VALUE").get(0);
        Map<String,Object> attr9 = CollectionUtils.isEmpty(temp.get("APPRECIATION")) ? new HashMap<>() : temp.get("APPRECIATION").get(0);
        temp = groups.get("基金投资")==null ? new HashMap<>() : groups.get("基金投资");
        Map<String,Object> attr10 = CollectionUtils.isEmpty(temp.get("COST")) ? new HashMap<>() : temp.get("COST").get(0);
        Map<String,Object> attr11 = CollectionUtils.isEmpty(temp.get("MKT_VALUE")) ? new HashMap<>() : temp.get("MKT_VALUE").get(0);
        Map<String,Object> attr12 = CollectionUtils.isEmpty(temp.get("APPRECIATION")) ? new HashMap<>() : temp.get("APPRECIATION").get(0);
        temp = groups.get("衍生金融工具")==null ? new HashMap<>() : groups.get("衍生金融工具");
        Map<String,Object> attr13 = CollectionUtils.isEmpty(temp.get("COST")) ? new HashMap<>() : temp.get("COST").get(0);
        Map<String,Object> attr14 = CollectionUtils.isEmpty(temp.get("MKT_VALUE")) ? new HashMap<>() : temp.get("MKT_VALUE").get(0);
        Map<String,Object> attr15 = CollectionUtils.isEmpty(temp.get("APPRECIATION")) ? new HashMap<>() : temp.get("APPRECIATION").get(0);
        temp = groups.get("贵金属投资-金交所黄金合约")==null ? new HashMap<>() : groups.get("贵金属投资-金交所黄金合约");
        Map<String,Object> attr16 = CollectionUtils.isEmpty(temp.get("COST")) ? new HashMap<>() : temp.get("COST").get(0);
        Map<String,Object> attr17 = CollectionUtils.isEmpty(temp.get("MKT_VALUE")) ? new HashMap<>() : temp.get("MKT_VALUE").get(0);
        Map<String,Object> attr18 = CollectionUtils.isEmpty(temp.get("APPRECIATION")) ? new HashMap<>() : temp.get("APPRECIATION").get(0);
        temp = groups.get("其他")==null ? new HashMap<>() : groups.get("其他");
        Map<String,Object> attr19 = CollectionUtils.isEmpty(temp.get("COST")) ? new HashMap<>() : temp.get("COST").get(0);
        Map<String,Object> attr20 = CollectionUtils.isEmpty(temp.get("MKT_VALUE")) ? new HashMap<>() : temp.get("MKT_VALUE").get(0);
        Map<String,Object> attr21 = CollectionUtils.isEmpty(temp.get("APPRECIATION")) ? new HashMap<>() : temp.get("APPRECIATION").get(0);
        temp = groups.get("买入返售金融资产 ")==null ? new HashMap<>() : groups.get("买入返售金融资产 ");
        Map<String,Object> attr22 = null;
        if(CollectionUtils.isNotEmpty(temp.get("NULL"))) {
            attr22 = temp.get("NULL").get(0);
        }else if(CollectionUtils.isNotEmpty(temp.get(StringUtils.EMPTY))) {
            attr22 = temp.get(StringUtils.EMPTY).get(0);
        }else {
            attr22 = new HashMap<>();
        }
        result.put("attr1", attr1);
        result.put("attr2", attr2);
        result.put("attr3", attr3);
        result.put("attr4", attr4);
        result.put("attr5", attr5);
        result.put("attr6", attr6);
        result.put("attr7", attr7);
        result.put("attr8", attr8);
        result.put("attr9", attr9);
        result.put("attr10", attr10);
        result.put("attr11", attr11);
        result.put("attr12", attr12);
        result.put("attr13", attr13);
        result.put("attr14", attr14);
        result.put("attr15", attr15);
        result.put("attr16", attr16);
        result.put("attr17", attr17);
        result.put("attr18", attr18);
        result.put("attr19", attr19);
        result.put("attr20", attr20);
        result.put("attr21", attr21);
        result.put("attr22", attr22);
        return result;
    }
    
    /**
     * 处理sheet页H300的数据
     * @author Dai Zong 2017年12月12日
     * 
     * @param fundId
     * @param periodStr
     * @return
     * @throws Exception
     */
    private Map<String,Object> getH300Data(String fundId, String periodStr) throws Exception{
        Map<String, Object> queryMap = this.createBaseQueryMap(fundId, periodStr);
        Map<String, Object> result = new HashMap<String,Object>();
        
        //========process dataMap for main view begin========
        Map<String, Object> main = new HashMap<>();
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> mainMetaDataList = (List<Map<String,Object>>)this.dao.findForList("HExportMapper.selectH300Data", queryMap);
        if(mainMetaDataList == null) {
            mainMetaDataList = new ArrayList<>();
        }
        Map<String, List<Map<String, Object>>> groups = mainMetaDataList.stream().collect(Collectors.groupingBy(item -> {
            Map<String,Object> map = (Map<String,Object>)item;
            return String.valueOf(map.get("type"));
        }));
        Map<String,Object> attr1 = new HashMap<>();
        Map<String,Object> attr2 = new HashMap<>();
        Map<String,Object> attr3 = new HashMap<>();
        Map<String,Object> attr4 = new HashMap<>();
        Map<String,Object> attr5 = new HashMap<>();
        Map<String,Object> attr6 = new HashMap<>();
        Map<String,Object> attr7 = new HashMap<>();
        Map<String,Object> attr8 = new HashMap<>();
        List<Map<String,Object>> tempList = null;
        tempList = groups.get("股票")==null ? new ArrayList<>() : groups.get("股票");
        attr1 = CollectionUtils.isEmpty(tempList) ? attr1 : tempList.get(0);
        tempList = groups.get("贵金属投资-金交所黄金合约")==null ? new ArrayList<>() : groups.get("贵金属投资-金交所黄金合约");
        attr2 = CollectionUtils.isEmpty(tempList) ? attr2 : tempList.get(0);
        tempList = groups.get("债券")==null ? new ArrayList<>() : groups.get("债券");
        for(Map<String,Object> map : tempList) {
            if("交易所".equals(map.get("subtype"))) {
                attr3 = map;
            }else if("银行间".equals(map.get("subtype"))) {
                attr4 = map;
            }
        }
        tempList = groups.get("资产支持性证券投资")==null ? new ArrayList<>() : groups.get("资产支持性证券投资");
        attr5 = CollectionUtils.isEmpty(tempList) ? attr5 : tempList.get(0);
        tempList = groups.get("基金投资")==null ? new ArrayList<>() : groups.get("基金投资");
        attr6 = CollectionUtils.isEmpty(tempList) ? attr6 : tempList.get(0);
        tempList = groups.get("其他")==null ? new ArrayList<>() : groups.get("其他");
        attr7 = CollectionUtils.isEmpty(tempList) ? attr7 : tempList.get(0);
        tempList = groups.get("衍生金融工具")==null ? new ArrayList<>() : groups.get("衍生金融工具");
        for(Map<String,Object> map : tempList) {
            if("期货".equals(map.get("subtype"))) {
                attr8 = map;
            }
        }
        main.put("attr1", attr1);
        main.put("attr2", attr2);
        main.put("attr3", attr3);
        main.put("attr4", attr4);
        main.put("attr5", attr5);
        main.put("attr6", attr6);
        main.put("attr7", attr7);
        main.put("attr8", attr8);
        //========process dataMap for main view end========
        //========process dataMap for interestRatePeriods view begin========
        Map<String,Object> interestRatePeriod = new HashMap<>();
        
        @SuppressWarnings("unchecked")
        List<String> periodMetaDataList = (List<String>)this.dao.findForList("HExportMapper.selectH300InterestRatePeriodsData", queryMap);
        if(periodMetaDataList == null) {
            periodMetaDataList = new ArrayList<>();
        }
        interestRatePeriod.put("list", periodMetaDataList);
        interestRatePeriod.put("count", periodMetaDataList.size());
        //========process dataMap for interestRatePeriods view end========
        //========process dataMap for related view begin========
        Map<String,Object> related = new HashMap<>();
        Map<String,Object> H400 = new HashMap<>();
        Map<String,Object> H500 = new HashMap<>();
        
        queryMap.put("related", "Y");
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> H400MetaDataList = (List<Map<String,Object>>)this.dao.findForList("HExportMapper.selectH400MainData", queryMap);
        if(H400MetaDataList == null) {
            H400MetaDataList = new ArrayList<>();
        }
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> H500MetaDataList = (List<Map<String,Object>>)this.dao.findForList("HExportMapper.selectH500MainData", queryMap);
        if(H500MetaDataList == null) {
            H500MetaDataList = new ArrayList<>();
        }
        queryMap.remove("related");
        
        H400.put("list", H400MetaDataList);
        H400.put("count", H400MetaDataList.size());
        H500.put("list", H500MetaDataList);
        H500.put("count", H500MetaDataList.size());
        related.put("H400", H400);
        related.put("H500", H500);
        //========process dataMap for related view end========
        
        result.put("main", main);
        result.put("interestRatePeriod", interestRatePeriod);
        result.put("related", related);
        return result;
    }
    
    /**
     * 处理sheet页H400的数据
     * @author Dai Zong 2017年12月13日
     * 
     * @param fundId
     * @param periodStr
     * @return
     * @throws Exception
     */
    private Map<String,Object> getH400Data(String fundId, String periodStr) throws Exception{
        Map<String, Object> queryMap = this.createBaseQueryMap(fundId, periodStr);
        Map<String, Object> result = new HashMap<String,Object>();
        
        Map<String, Object> main = new HashMap<String,Object>();
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> mainMetaDataList = (List<Map<String,Object>>)this.dao.findForList("HExportMapper.selectH400MainData", queryMap);
        if(mainMetaDataList == null) {
            mainMetaDataList = new ArrayList<>();
        }
        main.put("list", mainMetaDataList);
        main.put("count", mainMetaDataList.size());
        main.put("noteFlag", mainMetaDataList.stream().filter(item -> {return "期货".equals(item.get("type"));}).count()==0 ? "N" : "Y");
        result.put("main", main);
        
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> metaDataList = (List<Map<String,Object>>)this.dao.findForList("HExportMapper.selectH400OptionData", queryMap);
        if(metaDataList == null) {
            metaDataList = new ArrayList<>();
        }
        
        result.put("list", metaDataList);
        result.put("count", metaDataList.size());
        return result;
    }
    
    /**
     * 处理sheet页H500的数据
     * @author Dai Zong 2017年12月13日
     * 
     * @param fundId
     * @param periodStr
     * @return
     * @throws Exception
     */
    private Map<String,Object> getH500Data(String fundId, String periodStr) throws Exception{
        Map<String, Object> queryMap = this.createBaseQueryMap(fundId, periodStr);
        Map<String, Object> result = new HashMap<String,Object>();
        
        Map<String, Object> main = new HashMap<String,Object>();
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> mainMetaDataList = (List<Map<String,Object>>)this.dao.findForList("HExportMapper.selectH500MainData", queryMap);
        if(mainMetaDataList == null) {
            mainMetaDataList = new ArrayList<>();
        }
        main.put("list", mainMetaDataList);
        main.put("count", mainMetaDataList.size());
        result.put("main", main);
        
        Map<String,Object> interestRatePeriod = new HashMap<>();
        @SuppressWarnings("unchecked")
        List<String> periodMetaDataList = (List<String>)this.dao.findForList("HExportMapper.selectH300InterestRatePeriodsData", queryMap);
        if(periodMetaDataList == null) {
            periodMetaDataList = new ArrayList<>();
        }
        while(periodMetaDataList.size() < 8) {
            periodMetaDataList.add(StringUtils.EMPTY);
        }
        interestRatePeriod.put("list", periodMetaDataList);
        interestRatePeriod.put("count", periodMetaDataList.size());
        
        @SuppressWarnings("unchecked")
        Map<String,Object> diviatonMetaData = (Map<String,Object>)this.dao.findForObject("HExportMapper.selectH500DiviatonData", queryMap);
        if(diviatonMetaData == null) {
            diviatonMetaData = new HashMap<>();
        }
        
        result.put("diviaton", diviatonMetaData);
        result.put("interestRatePeriod", interestRatePeriod);
        return result;
    }
    
    /**
     * 处理sheet页H800的数据
     * @author Dai Zong 2017年12月14日
     * 
     * @param fundId
     * @param periodStr
     * @return
     * @throws Exception
     */
    private Map<String,Object> getH800Data(String fundId, String periodStr) throws Exception{
        Map<String, Object> queryMap = this.createBaseQueryMap(fundId, periodStr);
        Map<String, Object> result = new HashMap<String,Object>();
        
        //========process dataMap for main view begin========
        Map<String, Object> main = new HashMap<String,Object>();
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> mainMetaDataList = (List<Map<String,Object>>)this.dao.findForList("HExportMapper.selectH800MainData", queryMap);
        if(mainMetaDataList == null) {
            mainMetaDataList = new ArrayList<>();
        }
        main.put("list", mainMetaDataList);
        main.put("count", mainMetaDataList.size());
        //========process dataMap for main view end========
        
        //========process dataMap for interestTest view begin========
        Map<String, Object> interestTest = new HashMap<String,Object>();
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> interestTestMetaDataList = (List<Map<String,Object>>)this.dao.findForList("HExportMapper.selectH800InterestTestData", queryMap);
        if(interestTestMetaDataList == null) {
            interestTestMetaDataList = new ArrayList<>();
        }
        interestTest.put("list", interestTestMetaDataList);
        interestTest.put("count", interestTestMetaDataList.size());
        //========process dataMap for interestTest view end========
        
        //========process dataMap for intestSummary view end========
        @SuppressWarnings("unchecked")
        Map<String,Object> intestSummaryMetaData = (Map<String,Object>)this.dao.findForObject("HExportMapper.selectH800IntestSummaryData", queryMap);
        if(intestSummaryMetaData == null) {
            intestSummaryMetaData = new HashMap<>();
        }
        //========process dataMap for intestSummary view end========
        
        //========process dataMap for interestDetail view begin========
        Map<String, Object> interestDetail = new HashMap<String,Object>();
        List<Map<String, Object>> headLists = new ArrayList<>();
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> interestDetailMetaDataList = (List<Map<String,Object>>)this.dao.findForList("HExportMapper.selectH800InterestDetailData", queryMap);
        if(interestDetailMetaDataList == null) {
            interestDetailMetaDataList = new ArrayList<>();
        }
        List<String> headAccountNums = interestTestMetaDataList.stream().map(item -> {
            return String.valueOf(item.get("accountNum"));
        }).distinct().collect(Collectors.toList());
        Map<Object, List<Map<String, Object>>> groups = interestDetailMetaDataList.stream().collect(Collectors.groupingBy(item -> {
            Map<String,Object> map = (Map<String,Object>)item;
            return map.get("accountNum");
        }));
        for(String headAccountNum : headAccountNums) {
            List<Map<String, Object>> tempList = groups.get(headAccountNum);
            if(CollectionUtils.isNotEmpty(tempList)) {
                Map<String, Object> tempMap = new HashMap<String,Object>();
                tempMap.put("list", tempList);
                tempMap.put("count", tempList.size());
                headLists.add(tempMap);
            }
        }
        interestDetail.put("heads", headLists);
        interestDetail.put("headsCount", headLists.size());
        //========process dataMap for interestDetail view end========
        
        //========process dataMap for intRiskPeriod view begin========
        Map<String, Object> intRiskPeriod = new HashMap<String,Object>();
        @SuppressWarnings("unchecked")
        List<String> intRiskPeriodMetaDataList = (List<String>)this.dao.findForList("HExportMapper.selectH800IntRiskPeriodData", queryMap);
        if(intRiskPeriodMetaDataList == null) {
            intRiskPeriodMetaDataList = new ArrayList<>();
        }
        intRiskPeriod.put("list", intRiskPeriodMetaDataList);
        intRiskPeriod.put("count", intRiskPeriodMetaDataList.size());
        //========process dataMap for intRiskPeriod view end========
        
        result.put("main", main);
        result.put("interestTest", interestTest);
        result.put("intestSummary", intestSummaryMetaData);
        result.put("interestDetail", interestDetail);
        result.put("intRiskPeriod", intRiskPeriod);
        return result;
    }
    
    /**
     * 处理sheet页H10000的数据
     * @author Dai Zong 2017年12月15日
     * 
     * @param fundId
     * @param periodStr
     * @return
     * @throws Exception
     */
    private Map<String,Object> getH10000Data(String fundId, String periodStr) throws Exception{
        Map<String, Object> queryMap = this.createBaseQueryMap(fundId, periodStr);
        Map<String, Object> lastQueryMap = new HashMap<String,Object>(); 
        queryMap.forEach((k,v) -> {
            lastQueryMap.put(k, v);
        });
        lastQueryMap.put("period", (Integer.parseInt(String.valueOf(lastQueryMap.get("period")).substring(0, 4)) - 1) + "1231");
        Map<String, Object> result = new HashMap<String,Object>();
        //========process dataMap for note view begin========
        @SuppressWarnings("unchecked")
        Map<String,Object> noteMetaData = (Map<String,Object>)this.dao.findForObject("HExportMapper.selectHNoteAData", queryMap);
        if(noteMetaData == null) {
            noteMetaData = new HashMap<>();
        }
        //========process dataMap for note view end========
        
        //========process dataMap for TFA view begin========
        Map<String, Object> TFA = new HashMap<String,Object>();
        Map<String, Object> exchange = new HashMap<String,Object>();
        Map<String, Object> bank = new HashMap<String,Object>();
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> tfaMetaDataList = (List<Map<String,Object>>)this.dao.findForList("HExportMapper.selectH10000TFAData", queryMap);
        if(tfaMetaDataList == null) {
            tfaMetaDataList = new ArrayList<>();
        }
        for(Map<String,Object> map : tfaMetaDataList) {
            if("交易所".equals(map.get("subItem"))) {
                exchange = map;
            }else if("银行间".equals(map.get("subItem"))) {
                bank = map;
            }
        }
        TFA.put("exchange", exchange);
        TFA.put("bank", bank);
        Object diviatonEyCurrent = this.dao.findForObject("HExportMapper.selectH10000TFADiviatonSumData", queryMap);
        Object diviatonEyLast = this.dao.findForObject("HExportMapper.selectH10000TFADiviatonSumData", lastQueryMap);
        TFA.put("diviatonEyCurrent", diviatonEyCurrent);
        TFA.put("diviatonEyLast", diviatonEyLast);
        //========process dataMap for TFA view end========
        
        //========process dataMap for derivative view begin========
        Map<String, Object> derivative = new HashMap<String,Object>();
        Map<String, Object> item1 = new HashMap<String,Object>();
        Map<String, Object> item2 = new HashMap<String,Object>();
        Map<String, Object> item3 = new HashMap<String,Object>();
        Map<String, Object> item4 = new HashMap<String,Object>();
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> derivativeMetaDataList = (List<Map<String,Object>>)this.dao.findForList("HExportMapper.selectH10000DerivativeData", queryMap);
        if(derivativeMetaDataList == null) {
            derivativeMetaDataList = new ArrayList<>();
        }
        for(Map<String,Object> map : derivativeMetaDataList) {
            if("利率衍生工具".equals(map.get("item"))) {
                item1 = map;
            }else if("货币衍生工具".equals(map.get("item"))) {
                item2 = map;
            }else if("权益衍生工具".equals(map.get("item"))) {
                item3 = map;
            }else if("其他衍生工具".equals(map.get("item"))) {
                item4 = map;
            }
        }
        derivative.put("item1", item1);
        derivative.put("item2", item2);
        derivative.put("item3", item3);
        derivative.put("item4", item4);
        //========process dataMap for derivative view end========
        
        //========process dataMap for futures view begin========
        Map<String, Object> futures = new HashMap<String,Object>();
        item1 = new HashMap<String,Object>();
        item2 = new HashMap<String,Object>();
        item3 = new HashMap<String,Object>();
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> futuresMetaDataList = (List<Map<String,Object>>)this.dao.findForList("HExportMapper.selectH10000FuturesData", queryMap);
        if(futuresMetaDataList == null) {
            futuresMetaDataList = new ArrayList<>();
        }
        for(Map<String,Object> map : futuresMetaDataList) {
            if("股指期货合约".equals(map.get("item"))) {
                item1 = map;
            }else if("国债期货合约".equals(map.get("item"))) {
                item2 = map;
            }else if("黄金现货延期交收合约".equals(map.get("item"))) {
                item3 = map;
            }
        }
        futures.put("item1", item1);
        futures.put("item2", item2);
        futures.put("item3", item3);
        //========process dataMap for futures view end========
        
        //========process dataMap for rmcfs view begin========
        Map<String, Object> rmcfs = new HashMap<String,Object>();
        item1 = new HashMap<String,Object>();
        item2 = new HashMap<String,Object>();
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> rmcfsMetaDataList = (List<Map<String,Object>>)this.dao.findForList("HExportMapper.selectH10000RmcfsData", queryMap);
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
        rmcfs.put("item1", item1);
        rmcfs.put("item2", item2);
        //========process dataMap for rmcfs view end========
        
        //========process dataMap for interestDetail view begin========
        Map<String, Object> interestDetail = new HashMap<String,Object>();
        Map<String, Object> current = new HashMap<String,Object>();
        Map<String, Object> last = new HashMap<String,Object>();
        
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> currentInterestDetailMetaDataList = (List<Map<String,Object>>)this.dao.findForList("HExportMapper.selectH10000InterestDetailData", queryMap);
        if(currentInterestDetailMetaDataList == null) {
            currentInterestDetailMetaDataList = new ArrayList<>();
        }
        current.put("list", currentInterestDetailMetaDataList);
        current.put("count", currentInterestDetailMetaDataList.size());
        
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> lastInterestDetailMetaDataList = (List<Map<String,Object>>)this.dao.findForList("HExportMapper.selectH10000InterestDetailData", lastQueryMap);
        if(lastInterestDetailMetaDataList == null) {
            lastInterestDetailMetaDataList = new ArrayList<>();
        }
        
        SimpleDateFormat sdfin = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat sdfout = new SimpleDateFormat("yyyy年MM月dd日");
        String returnDays = currentInterestDetailMetaDataList.stream().map(item -> {
            return String.valueOf(item.get("returnDay"));
        }).filter(item -> {
            return !"null".equals(item) && !StringUtils.EMPTY.equals(item);
        }).map(item -> {
            try {
                return sdfout.format(sdfin.parse(item));
            } catch (ParseException e) {
                return StringUtils.EMPTY;
            }
        }).distinct().collect(Collectors.joining("、"));
        
        last.put("list", lastInterestDetailMetaDataList);
        last.put("count", lastInterestDetailMetaDataList.size());
        
        interestDetail.put("current", current);
        interestDetail.put("last", last);
        interestDetail.put("returnDays", returnDays);
        //========process dataMap for interestDetail view end========
        
        //========process dataMap for fairValues view begin========
        Map<String, Object> fairValues = new HashMap<String,Object>();
        item1 = new HashMap<String,Object>();
        item2 = new HashMap<String,Object>();
        item3 = new HashMap<String,Object>();
        item4 = new HashMap<String,Object>();
        Map<String,Object> item5 = new HashMap<String,Object>();
        Map<String,Object> item6 = new HashMap<String,Object>();
        Map<String,Object> item7 = new HashMap<String,Object>();
        List<Map<String,Object>> toolList = new ArrayList<>();
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> fairValuesMetaDataList = (List<Map<String,Object>>)this.dao.findForList("HExportMapper.selectH10000FairValuesData", queryMap);
        if(fairValuesMetaDataList == null) {
            fairValuesMetaDataList = new ArrayList<>();
        }
        for(Map<String,Object> map : fairValuesMetaDataList) {
            int sort = 0;
            if(String.valueOf(map.get("sort")) != null) {
                sort = Integer.parseInt(String.valueOf(map.get("sort")));
            }
//            if(Pattern.matches("^3.*$", String.valueOf(map.get("eyAccountNum")))){
            if(sort >= 22 && sort <= 29){
                //sort值在22 ~ 29的item动态输出
                toolList.add(map);
            }else {
                //其他项目静态输出
                if(sort == 11) {
                    item1 = map;
                }else if(sort == 12) {
                    item2 = map;
                }else if(sort == 13) {
                    item3 = map;
                }else if(sort == 14) {
                    item4 = map;
                }else if(sort == 15) {
                    item5 = map;
                }else if(sort == 16) {
                    item6 = map;
                }else if(sort == 21) {
                    item7 = map;
                }
            }
        }
        fairValues.put("item1", item1);
        fairValues.put("item2", item2);
        fairValues.put("item3", item3);
        fairValues.put("item4", item4);
        fairValues.put("item5", item5);
        fairValues.put("item6", item6);
        fairValues.put("item7", item7);
        fairValues.put("toolList", toolList);
        fairValues.put("toolCount", toolList.size());
        //========process dataMap for fairValues view end========
        
        result.put("note", noteMetaData);
        result.put("TFA", TFA);
        result.put("derivative", derivative);
        result.put("futures", futures);
        result.put("rmcfs", rmcfs);
        result.put("interestDetail", interestDetail);
        result.put("fairValues", fairValues);
        return result;
    }
    
    public static void main(String args[]) {
        System.out.println(Pattern.compile("^3").matcher("31234").find());
    }
    
    /**
     * 处理sheet页H11000的数据
     * @author Dai Zong 2017年12月16日
     * 
     * @param fundId
     * @param periodStr
     * @return
     * @throws Exception
     */
    private Map<String,Object> getH11000Data(String fundId, String periodStr) throws Exception{
        Map<String, Object> queryMap = this.createBaseQueryMap(fundId, periodStr);
        Map<String, Object> result = new HashMap<String,Object>();
        
        //========process dataMap for additian view begin========
        Map<String, Object> additian = new HashMap<String,Object>();
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> additianMetaDataList = (List<Map<String,Object>>)this.dao.findForList("HExportMapper.selectH11000AdditianData", queryMap);
        if(additianMetaDataList == null) {
            additianMetaDataList = new ArrayList<>();
        }
//        List<String> existCode = additianMetaDataList.stream().map(item -> {
//            return String.valueOf(item.get("type"));
//        }).distinct().collect(Collectors.toList());
//        if(!existCode.contains("股票")) {
//            Map<String, Object> temp = new HashMap<String,Object>();
//            temp.put("type", "股票");
//            additianMetaDataList.add(temp);
//        }
//        if(!existCode.contains("债券")) {
//            Map<String, Object> temp = new HashMap<String,Object>();
//            temp.put("type", "债券");
//            additianMetaDataList.add(temp);
//        }
//        if(!existCode.contains("其他")) {
//            Map<String, Object> temp = new HashMap<String,Object>();
//            temp.put("type", "其他");
//            additianMetaDataList.add(temp);
//        }
        additian.put("list", additianMetaDataList);
        additian.put("count", additianMetaDataList.size());
        //========process dataMap for additian view end========
        
        //========process dataMap for suspension view begin========
        Map<String, Object> suspension = new HashMap<String,Object>();
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> suspensionMetaDataList = (List<Map<String,Object>>)this.dao.findForList("HExportMapper.selectH11000SuspensionData", queryMap);
        if(suspensionMetaDataList == null) {
            suspensionMetaDataList = new ArrayList<>();
        }
        suspension.put("list", suspensionMetaDataList);
        suspension.put("count", suspensionMetaDataList.size());
        //========process dataMap for suspension view end========
        
        //========process dataMap for saleIn view begin========
        Map<String, Object> saleIn = new HashMap<String,Object>();
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> saleInMetaDataList = (List<Map<String,Object>>)this.dao.findForList("HExportMapper.selectH11000SaleInData", queryMap);
        if(saleInMetaDataList == null) {
            saleInMetaDataList = new ArrayList<>();
        }
        saleIn.put("list", saleInMetaDataList);
        saleIn.put("count", saleInMetaDataList.size());
        //========process dataMap for saleIn view end========
        
        //========process dataMap for noteDates view begin========
        @SuppressWarnings("unchecked")
        List<String> noteDataList = (List<String>)this.dao.findForList("HExportMapper.selectH11000NoteData", queryMap);
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
        //========process dataMap for noteDates view end========
    
        result.put("additian", additian);
        result.put("suspension", suspension);
        result.put("saleIn", saleIn);
        result.put("noteDates", noteDates);
        return result;
    }
    
}