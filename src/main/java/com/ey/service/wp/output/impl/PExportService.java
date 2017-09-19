package com.ey.service.wp.output.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

	@Override
    public boolean doExport(HttpServletRequest request, HttpServletResponse response, String fundId, Long period) throws Exception {
        Map<String, Object> dataMap = new HashMap<String, Object>();
        
        dataMap.put("period", period);
        dataMap.put("fundId", fundId);
        
        dataMap.put("P", this.getPData(fundId, period));
        dataMap.put("P300", this.getP300Data(fundId, period));
        
        String xmlStr = FreeMarkerUtils.processTemplateToString(dataMap, Constants.EXPORT_TEMPLATE_FOLDER_PATH, Constants.EXPORT_TEMPLATE_FILE_NAME_P);
        FileExportUtils.writeFileToHttpResponse(request, response, Constants.EXPORT_AIM_FILE_NAME_P, xmlStr);
        
        return true;
    }
	
	/**
     * 处理sheet页P的数据
     * @author Dai Zong 2017年9月19日
     * 
     * @param fundId
     * @param period
     * @return
     * @throws Exception
     */
    private Map<String,Object> getPData(String fundId, Long period) throws Exception{
        Map<String, Object> queryMap = this.createBaseQueryMap(fundId, period);
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
     * 处理sheet页P的数据
     * @author Dai Zong 2017年9月19日
     * 
     * @param fundId
     * @param period
     * @return
     * @throws Exception
     */
    private Map<String,Object> getP300Data(String fundId, Long period) throws Exception{
        Map<String, Object> queryMap = this.createBaseQueryMap(fundId, period);
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
}