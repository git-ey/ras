package com.ey.service.wp.output.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Service;

import com.ey.service.wp.output.NExportManager;
import com.ey.util.fileexport.Constants;
import com.ey.util.fileexport.FileExportUtils;
import com.ey.util.fileexport.FreeMarkerUtils;

/**
 * @name NExportService
 * @description 底稿输出服务--N
 * @author Dai Zong	2017年9月11日
 */
@Service("nExportService")
public class NExportService extends BaseExportService implements NExportManager{

	@Override
    public boolean doExport(HttpServletRequest request, HttpServletResponse response, String fundId, Long period) throws Exception {
        Map<String, Object> dataMap = new HashMap<String, Object>();
        
        dataMap.put("period", period);
        dataMap.put("fundId", fundId);
        
        dataMap.put("N", this.getNData(fundId, period));
        dataMap.put("N300", this.getN300Data(fundId, period));
        
        String xmlStr = FreeMarkerUtils.processTemplateToString(dataMap, Constants.EXPORT_TEMPLATE_FOLDER_PATH, Constants.EXPORT_TEMPLATE_FILE_NAME_N);
        FileExportUtils.writeFileToHttpResponse(request, response, Constants.EXPORT_AIM_FILE_NAME_N, xmlStr);
        
        return true;
    }
	
	/**
     * 处理sheet页N的数据
     * @author Dai Zong 2017年9月12日
     * 
     * @param fundId
     * @param period
     * @return
     * @throws Exception
     */
    private Map<String,Object> getNData(String fundId, Long period) throws Exception{
        Map<String, Object> queryMap = this.createBaseQueryMap(fundId, period);
        Map<String, Object> result = new HashMap<String,Object>();
        
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> NMetaDataList = (List<Map<String,Object>>)this.dao.findForList("NExportMapper.selectNData", queryMap);
        if(NMetaDataList == null) {
            NMetaDataList = new ArrayList<Map<String,Object>>(); 
        }
        
        Map<String,Object> temp = new HashMap<>();
        NMetaDataList.forEach(map -> {
            temp.put(String.valueOf(map.get("accountNum")), map);
        });
        
        result.put("KM2203", temp.get("2203")==null?new HashMap<String,Object>():temp.get("2203"));
        result.put("KM2206", temp.get("2206")==null?new HashMap<String,Object>():temp.get("2206"));
        result.put("KM2207", temp.get("2207")==null?new HashMap<String,Object>():temp.get("2207"));
        result.put("KM2209", temp.get("2209")==null?new HashMap<String,Object>():temp.get("2209"));
        result.put("KM3003", temp.get("3003")==null?new HashMap<String,Object>():temp.get("3003"));
        result.put("KM2208", temp.get("2208")==null?new HashMap<String,Object>():temp.get("2208"));
        result.put("KM2221", temp.get("2221")==null?new HashMap<String,Object>():temp.get("2221"));
        result.put("KM2231", temp.get("2231")==null?new HashMap<String,Object>():temp.get("2231"));
        result.put("KM2232", temp.get("2232")==null?new HashMap<String,Object>():temp.get("2232"));
        
        return result;
    }
    
    /**
     * 处理sheet页N300的数据
     * @author Dai Zong 2017年9月13日
     * 
     * @param fundId
     * @param period
     * @return
     * @throws Exception
     */
    private Map<String,Object> getN300Data(String fundId, Long period) throws Exception{
        Map<String, Object> queryMap = this.createBaseQueryMap(fundId, period);
        Map<String, Object> result = new HashMap<String,Object>();
        
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> N300MetaDataList = (List<Map<String,Object>>)this.dao.findForList("NExportMapper.selectN300Data", queryMap);
        if(N300MetaDataList == null) {
            N300MetaDataList = new ArrayList<Map<String,Object>>(); 
        }
        
        result.put("list", N300MetaDataList);
        result.put("count", N300MetaDataList.size());
        
        return result;
    }
}