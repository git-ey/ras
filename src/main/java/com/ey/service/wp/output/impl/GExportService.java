package com.ey.service.wp.output.impl;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
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

	@Override
    public boolean doExport(HttpServletRequest request, HttpServletResponse response, String fundId, Long period) throws Exception {
        Map<String, Object> dataMap = new HashMap<String, Object>();
        
        dataMap.put("period", period);
        dataMap.put("fundId", fundId);
        
        dataMap.put("G", this.getGData(fundId, period));
        dataMap.put("G300", this.getG300Data(fundId, period));
        dataMap.put("G10000", this.getG10000Data(fundId, period));
        
        String xmlStr = FreeMarkerUtils.processTemplateToString(dataMap, Constants.EXPORT_TEMPLATE_FOLDER_PATH, Constants.EXPORT_TEMPLATE_FILE_NAME_G);
        FileExportUtils.writeFileToHttpResponse(request, response, Constants.EXPORT_AIM_FILE_NAME_G, xmlStr);
        
        return true;
    }
    
	/**
	 * 处理sheet页G的数据
	 * @author Dai Zong 2017年9月8日
	 * 
	 * @param fundId
	 * @param period
	 * @return
	 * @throws Exception
	 */
    private Map<String,Object> getGData(String fundId, Long period) throws Exception{
        Map<String, Object> queryMap = this.createBaseQueryMap(fundId, period);
        Map<String, Object> result = new HashMap<String,Object>();
        
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> GMetaDataList = (List<Map<String,Object>>)this.dao.findForList("GExportMapper.selectGData", queryMap);
        if(CollectionUtils.isEmpty(GMetaDataList)) {return result;}
        
        Map<String,Object> KM1221 = new HashMap<>();
        Map<String,Object> KM1501 = new HashMap<>();
        
        for(Map<String,Object> map : GMetaDataList) {
            if("1221".equals(map.get("accountNum"))) {
                KM1221 = map;
            }else if("1501".equals(map.get("accountNum"))) {
                KM1501 = map;
            }
        }
        
        result.put("KM1221", KM1221);
        result.put("KM1501", KM1501);
        
        return result;
    }
    
    /**
     * 处理sheet页G300的数据
     * @author Dai Zong 2017年9月8日
     * 
     * @param fundId
     * @param period
     * @return
     * @throws Exception
     */
    private Map<String,Object> getG300Data(String fundId, Long period) throws Exception{
        Map<String, Object> queryMap = this.createBaseQueryMap(fundId, period);
        Map<String, Object> result = new HashMap<String,Object>();
        
        List<Map<String, Object>> KM1221 = new ArrayList<>();
        List<Map<String, Object>> KM1501 = new ArrayList<>(); 
        
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> metaDataList = (List<Map<String,Object>>)this.dao.findForList("GExportMapper.selectG300Data", queryMap);
        if(CollectionUtils.isEmpty(metaDataList)) {return result;}
        
        for(Map<String,Object> map : metaDataList) {
            if("1221".equals(map.get("accountNum"))) {
                KM1221.add(map);
            }
            if("1501".equals(map.get("accountNum"))) {
                KM1501.add(map);
            }
        }
        
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
     * @param period
     * @return
     * @throws Exception
     */
    private Map<String,Object> getG10000Data(String fundId, Long period) throws Exception{
        Map<String, Object> queryMap = this.createBaseQueryMap(fundId, period);
        Map<String, Object> result = new HashMap<String,Object>();
        
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> metaDataList = (List<Map<String,Object>>)this.dao.findForList("GExportMapper.selectG10000Data", queryMap);
        if(CollectionUtils.isEmpty(metaDataList)) {return result;}
        
        result.put("list", metaDataList);
        result.put("count", metaDataList.size());
        
        return result;
    }

    @Override
    public boolean doExport(String folederName, String fileName, String fundId, Long period) throws Exception {
Map<String, Object> dataMap = new HashMap<String, Object>();
        
        dataMap.put("period", period);
        dataMap.put("fundId", fundId);
        
        dataMap.put("G", this.getGData(fundId, period));
        dataMap.put("G300", this.getG300Data(fundId, period));
        dataMap.put("G10000", this.getG10000Data(fundId, period));
        
        String xmlStr = FreeMarkerUtils.processTemplateToString(dataMap, Constants.EXPORT_TEMPLATE_FOLDER_PATH, Constants.EXPORT_TEMPLATE_FILE_NAME_G);
        FileExportUtils.writeFileToDisk(folederName, fileName, new BufferedInputStream(new ByteArrayInputStream(xmlStr.getBytes("UTF-8")), 1024));
        
        return true;
    }
}