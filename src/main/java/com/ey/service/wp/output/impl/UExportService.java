package com.ey.service.wp.output.impl;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Service;

import com.ey.service.wp.output.UExportManager;
import com.ey.util.fileexport.Constants;
import com.ey.util.fileexport.FileExportUtils;
import com.ey.util.fileexport.FreeMarkerUtils;

/**
 * @name UExportService
 * @description 底稿输出服务--U
 * @author Dai Zong	2017年11月1日
 */
@Service("uExportService")
public class UExportService implements UExportManager {
    
    /**
     * 生成文件内容
     * @author Dai Zong 2017年10月17日
     * 
     * @param fundId
     * @param period
     * @return
     * @throws Exception
     */
    private String generateFileContent(String fundId, Long period) throws Exception {
        Map<String, Object> dataMap = new HashMap<String, Object>();
        
        dataMap.put("period", period);
        dataMap.put("fundId", fundId);
        
//        dataMap.put("E", this.getEData(fundId, period));
//        dataMap.put("E300", this.getE300Data(fundId, period));
//        dataMap.put("E400", this.getE400Data(fundId, period));
//        dataMap.put("E410", this.getE410Data(fundId, period));
//        dataMap.put("E41X", this.getE41XData(fundId, period));
//        dataMap.put("E500", this.getE500Data(fundId, period));
//        dataMap.put("E600", this.getE600Data(fundId, period));

        return FreeMarkerUtils.processTemplateToString(dataMap, Constants.EXPORT_TEMPLATE_FOLDER_PATH, Constants.EXPORT_TEMPLATE_FILE_NAME_U);
    }

    @Override
    public boolean doExport(HttpServletRequest request, HttpServletResponse response, String fundId, Long period) throws Exception {
        String xmlStr = this.generateFileContent(fundId, period);
        FileExportUtils.writeFileToHttpResponse(request, response, Constants.EXPORT_AIM_FILE_NAME_U, xmlStr);
        return true;
    }

    @Override
    public boolean doExport(String folederName, String fileName, String fundId, Long period) throws Exception {
        String xmlStr = this.generateFileContent(fundId, period);
        FileExportUtils.writeFileToDisk(folederName, fileName, xmlStr);
        return true;
    }

}
