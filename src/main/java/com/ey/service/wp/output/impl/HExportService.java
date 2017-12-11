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
        
        dataMap.put("H", this.getHData(fundId, periodStr));
//        dataMap.put("G300", this.getG300Data(fundId, periodStr));
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
}