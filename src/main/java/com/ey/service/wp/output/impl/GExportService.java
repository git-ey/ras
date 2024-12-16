package com.ey.service.wp.output.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import com.ey.service.wp.output.GExportManager;
import com.ey.util.fileexport.Constants;
import com.ey.util.fileexport.FileExportUtils;
import com.ey.util.fileexport.FreeMarkerUtils;

/**
 * @name GExportService
 * @description 底稿输出服务--G
 * @author Dai Zong	2017年9月6日
 */
@Service("gExportService")
public class GExportService extends BaseExportService implements GExportManager{

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
        
        dataMap.put("G", this.getGData(fundId, periodStr));
        dataMap.put("G300", this.getG300Data(fundId, periodStr));
        dataMap.put("G10000", this.getG10000Data(fundId, periodStr));
        
        return FreeMarkerUtils.processTemplateToString(dataMap, Constants.EXPORT_TEMPLATE_FOLDER_PATH, Constants.EXPORT_TEMPLATE_FILE_NAME_G);
    }
    
	@Override
    public boolean doExport(HttpServletRequest request, HttpServletResponse response, String fundId, String periodStr) throws Exception {
	    Map<String, String> fundInfo = this.selectFundInfo(fundId);
        fundInfo.put("periodStr", periodStr);
        String xmlStr = this.generateFileContent(fundId, periodStr, fundInfo);
        FileExportUtils.writeFileToHttpResponse(request, response, FreeMarkerUtils.simpleReplace(Constants.EXPORT_AIM_FILE_NAME_G, fundInfo), xmlStr);
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
	 * 处理sheet页G的数据
	 * @author Dai Zong 2017年9月8日
	 * 
	 * @param fundId
	 * @param periodStr
	 * @return
	 * @throws Exception
	 */
    private Map<String,Object> getGData(String fundId, String periodStr) throws Exception{
        Map<String, Object> queryMap = this.createBaseQueryMap(fundId, periodStr);
        Map<String, Object> result = new HashMap<String,Object>();
        
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> GMetaDataList = (List<Map<String,Object>>)this.dao.findForList("GExportMapper.selectGData", queryMap);
        if(GMetaDataList == null) {
            GMetaDataList = new ArrayList<>();
        }
		
        /*20221011,chenhy,新增应收利息和应计利息*/
        Map<String,Object> KM1221 = new HashMap<>();
		Map<String,Object> KM1221_yslx = new HashMap<>();
		Map<String,Object> KM1221_yjlx = new HashMap<>();
        Map<String,Object> KM1501 = new HashMap<>();
        
        for(Map<String,Object> map : GMetaDataList) {
            if("1221".equals(map.get("accountNum")) && "应收利息".equals(map.get("attr5"))){
                KM1221_yslx = map;
            }else if("1221".equals(map.get("accountNum")) && (!"应收利息".equals(map.get("detail"))) && ("应收黄金合约拆借孳息".equals(map.get("attr5")) || "应收出借证券利息".equals(map.get("attr5")))){
                KM1221_yjlx = map;
            }else if("1221".equals(map.get("accountNum"))) {
                KM1221 = map;
            }else if("1501".equals(map.get("accountNum"))) {
                KM1501 = map;
            }
        }
        
        result.put("KM1221", KM1221);
		result.put("KM1221_yslx", KM1221_yslx);
		result.put("KM1221_yjlx", KM1221_yjlx);
        result.put("KM1501", KM1501);
        
        return result;
    }
    
    /**
     * 处理sheet页G300的数据
     * @author Dai Zong 2017年9月8日
     * 
     * @param fundId
     * @param periodStr
     * @return
     * @throws Exception
     */
    private Map<String,Object> getG300Data(String fundId, String periodStr) throws Exception{
        Map<String, Object> queryMap = this.createBaseQueryMap(fundId, periodStr);
        Map<String, Object> result = new HashMap<String,Object>();
        
        /*20220929,chenhy,新增应收利息和应计利息明细项*/
        List<Map<String, Object>> KM1221 = new ArrayList<>();
        List<Map<String, Object>> KM1221_1 = new ArrayList<>();
        List<Map<String, Object>> KM1221_2 = new ArrayList<>();
        List<Map<String, Object>> KM1221_3 = new ArrayList<>();
        List<Map<String, Object>> KM1221_4 = new ArrayList<>();
        List<Map<String, Object>> KM1501 = new ArrayList<>(); 
        
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> metaDataList = (List<Map<String,Object>>)this.dao.findForList("GExportMapper.selectG300Data", queryMap);
        if(CollectionUtils.isEmpty(metaDataList)) {
            metaDataList = new ArrayList<>();
        }

        for(Map<String,Object> map : metaDataList) {
            if("1221".equals(map.get("accountNum")) && "应收黄金合约拆借孳息".equals(map.get("detail")) && "应收利息".equals(map.get("attr5"))) {
                KM1221_1.add(map);
            }else if("1221".equals(map.get("accountNum")) && "应收出借证券利息".equals(map.get("detail")) && "应收利息".equals(map.get("attr5"))) {
                KM1221_2.add(map);
            }else if("1221".equals(map.get("accountNum")) && "应收黄金合约拆借孳息".equals(map.get("detail"))) {
                KM1221_3.add(map);
            }else if("1221".equals(map.get("accountNum")) && "应收出借证券利息".equals(map.get("detail"))) {
                KM1221_4.add(map);
            }else if("1221".equals(map.get("accountNum"))) {
                KM1221.add(map);
            }else if("1501".equals(map.get("accountNum"))) {
                KM1501.add(map);
            }               
        }
        result.put("KM1221_1", KM1221_1);
        result.put("KM1221_1Count", KM1221_1.size());
        result.put("KM1221_2", KM1221_2);
        result.put("KM1221_2Count", KM1221_2.size());
        result.put("KM1221_3", KM1221_3);
        result.put("KM1221_3Count", KM1221_3.size());
        result.put("KM1221_4", KM1221_4);
        result.put("KM1221_4Count", KM1221_4.size());
        result.put("KM1221", KM1221);
        result.put("KM1221Count", KM1221.size());
        result.put("KM1501", KM1501);
        result.put("KM1501Count", KM1501.size());
        
        return result;
    }
    
    /**
     * 处理sheet页G10000的数据
     * @author Dai Zong 2017年9月8日
     * 
     * @param fundId
     * @param periodStr
     * @return
     * @throws Exception
     */
    private Map<String,Object> getG10000Data(String fundId, String periodStr) throws Exception{
        Map<String, Object> queryMap = this.createBaseQueryMap(fundId, periodStr);
        Map<String, Object> result = new HashMap<String,Object>();
        
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> metaDataList = (List<Map<String,Object>>)this.dao.findForList("GExportMapper.selectG10000Data", queryMap);
        if(metaDataList == null) {
            metaDataList = new ArrayList<>();
        }
        
        result.put("list", metaDataList);
        result.put("count", metaDataList.size());
        
        return result;
    }
}