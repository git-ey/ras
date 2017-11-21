package com.ey.service.wp.output.impl;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
        
//        dataMap.put("E", this.getEData(fundId, periodStr));
//        dataMap.put("E300", this.getE300Data(fundId, periodStr));
//        dataMap.put("E400", this.getE400Data(fundId, periodStr));
//        dataMap.put("E410", this.getE410Data(fundId, periodStr));
//        dataMap.put("E41X", this.getE41XData(fundId, periodStr));
//        dataMap.put("E500", this.getE500Data(fundId, periodStr));
//        dataMap.put("E600", this.getE600Data(fundId, periodStr));

//        return FreeMarkerUtils.processTemplateToString(dataMap, Constants.EXPORT_TEMPLATE_FOLDER_PATH, Constants.EXPORT_TEMPLATE_FILE_NAME_T);
        
        return null;
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

}
