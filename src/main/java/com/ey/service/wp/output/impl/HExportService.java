package com.ey.service.wp.output.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
//        dataMap.put("G10000", this.getG10000Data(fundId, periodStr));
        
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
        
        H400.put("list", H400MetaDataList);
        H400.put("count", H400MetaDataList.size());
        H400.put("noteFlag", H400MetaDataList.stream().filter(item -> {return "期货".equals(item.get("type"));}).count()==0 ? "N" : "Y");
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
        
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> metaDataList = (List<Map<String,Object>>)this.dao.findForList("HExportMapper.selectH400OptionData", queryMap);
        if(metaDataList == null) {
            metaDataList = new ArrayList<>();
        }
        
        result.put("list", metaDataList);
        result.put("count", metaDataList.size());
        return result;
    }
    
}