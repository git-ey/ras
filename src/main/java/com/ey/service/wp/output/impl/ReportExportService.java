package com.ey.service.wp.output.impl;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Service;

import com.ey.service.wp.output.ReportExportManager;
import com.ey.util.fileexport.Constants;
import com.ey.util.fileexport.FileExportUtils;
import com.ey.util.fileexport.FreeMarkerUtils;

/**
 * @name GExportService
 * @description 底稿输出服务--Report
 * @author andy.chen 2017年9月7日
 */
@Service("reportExportService")
public class ReportExportService implements ReportExportManager {

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

        return FreeMarkerUtils.processTemplateToString(dataMap, Constants.EXPORT_TEMPLATE_FOLDER_PATH, Constants.EXPORT_TEMPLATE_FILE_NAME_REPORT);
    }
    
	@Override
	public boolean doExport(HttpServletRequest request, HttpServletResponse response, String fundId, Long period) throws Exception {
		String fileStr = this.generateFileContent(fundId, period);
        FileExportUtils.writeFileToHttpResponse(request, response, Constants.EXPORT_AIM_FILE_NAME_REPORT, fileStr);
		return true;
	}

    @Override
    public boolean doExport(String folederName, String fileName, String fundId, Long period) throws Exception {
        String fileStr = this.generateFileContent(fundId, period);
        FileExportUtils.writeFileToDisk(folederName, fileName, fileStr);
        return true;
    }
    
}