package com.ey.service.wp.output.impl;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Service;

import com.ey.dao.DaoSupport;
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

	@Resource(name = "daoSupport")
	private DaoSupport dao;

	@Override
	public boolean doExport(HttpServletRequest request, HttpServletResponse response, String fundId, Long period)
			throws Exception {
		Map<String, Object> dataMap = new HashMap<String, Object>();

		dataMap.put("period", period);
		dataMap.put("fundId", fundId);

		String xmlStr = FreeMarkerUtils.processTemplateToString(dataMap, Constants.EXPORT_TEMPLATE_FOLDER_PATH, Constants.EXPORT_TEMPLATE_FILE_NAME_REPORT);
        FileExportUtils.writeFileToHttpResponse(request, response, fundId+".doc", xmlStr);
		return true;
	}

    @Override
    public boolean doExport(String folederName, String fileName, String fundId, Long period) throws Exception {
        Map<String, Object> dataMap = new HashMap<String, Object>();

        dataMap.put("period", period);
        dataMap.put("fundId", fundId);

        String xmlStr = FreeMarkerUtils.processTemplateToString(dataMap, Constants.EXPORT_TEMPLATE_FOLDER_PATH, Constants.EXPORT_TEMPLATE_FILE_NAME_REPORT);
        FileExportUtils.writeFileToDisk(folederName, fileName, new BufferedInputStream(new ByteArrayInputStream(xmlStr.getBytes("UTF-8")), 1024));
        return true;
    }

}