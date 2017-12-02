package com.ey.service.wp.output.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import com.ey.service.wp.output.TExportManager;
import com.ey.util.fileexport.Constants;
import com.ey.util.fileexport.FileExportUtils;
import com.ey.util.fileexport.FreeMarkerUtils;

/**
 * @name TExportService
 * @description 底稿输出服务--T
 * @author Dai Zong	2017年11月21日
 */
@Service("tExportService")
public class TExportService extends BaseExportService implements TExportManager{
    
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
        
        dataMap.put("T", this.getTData(fundId, periodStr));
//        dataMap.put("E300", this.getE300Data(fundId, periodStr));
//        dataMap.put("E400", this.getE400Data(fundId, periodStr));
//        dataMap.put("E410", this.getE410Data(fundId, periodStr));
//        dataMap.put("E41X", this.getE41XData(fundId, periodStr));
//        dataMap.put("E500", this.getE500Data(fundId, periodStr));
//        dataMap.put("E600", this.getE600Data(fundId, periodStr));

        return FreeMarkerUtils.processTemplateToString(dataMap, Constants.EXPORT_TEMPLATE_FOLDER_PATH, Constants.EXPORT_TEMPLATE_FILE_NAME_T);
    }

    @Override
    public boolean doExport(HttpServletRequest request, HttpServletResponse response, String fundId, String periodStr) throws Exception {
        Map<String, String> fundInfo = this.selectFundInfo(fundId);
        fundInfo.put("periodStr", periodStr);
        String xmlStr = this.generateFileContent(fundId, periodStr, fundInfo);
        FileExportUtils.writeFileToHttpResponse(request, response, FreeMarkerUtils.simpleReplace(Constants.EXPORT_AIM_FILE_NAME_T, fundInfo), xmlStr);
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
     * 处理sheet页T的数据
     * @author Dai Zong 2017年12月02日
     * 
     * @param fundId
     * @param periodStr
     * @return
     * @throws Exception
     */
    private Map<String,Object> getTData(String fundId, String periodStr) throws Exception{
        Map<String, Object> queryMap = this.createBaseQueryMap(fundId, periodStr);
        Map<String, Object> result = new HashMap<>();
        
        Map<String, Object> KM4001 = new HashMap<>();
        Map<String, Object> KM4104 = new HashMap<>();
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> metaDataList = (List<Map<String,Object>>)this.dao.findForList("TExportMapper.selectTData", queryMap);
        if(CollectionUtils.isEmpty(metaDataList)) {
            metaDataList = new ArrayList<>();
        }
        Map<String,Map<String,Object>> temp = new HashMap<>();
        metaDataList.forEach(map -> {
            temp.put(String.valueOf(map.get("accountNum")), map);
        });
        for(Entry<String, Map<String,Object>> entry : temp.entrySet()) {
            String key = entry.getKey();
            if("4001".equals(key)) {
                KM4001 = entry.getValue();
            }else if("4104".equals(key)) {
                KM4104 = entry.getValue();
            }
        }
        
        result.put("KM4001", KM4001);
        result.put("KM4104", KM4104);
        return result;
    }

}
