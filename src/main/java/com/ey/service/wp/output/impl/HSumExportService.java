package com.ey.service.wp.output.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import com.ey.service.wp.output.HSumExportManager;
import com.ey.util.fileexport.Constants;
import com.ey.util.fileexport.FileExportUtils;
import com.ey.util.fileexport.FreeMarkerUtils;

/**
 * @name HSumExportService
 * @description 底稿输出服务--H_SUM
 * @author Dai Zong	2017年12月6日
 */
@Service("hSumExportService")
public class HSumExportService extends BaseExportService implements HSumExportManager{

    /**
     * 生成文件内容
     * @author Dai Zong 2017年12月6日
     * 
     * @param firmCode
     * @param periodStr
     * @param companyInfo
     * @return
     * @throws Exception
     */
    private String generateFileContent(String firmCode, String periodStr, Map<String, String> companyInfo) throws Exception {
        Map<String, Object> dataMap = new HashMap<String, Object>();
        
        Long period = Long.parseLong(periodStr.substring(0, 4));
        Long month = Long.parseLong(periodStr.substring(4, 6));
        Long day = Long.parseLong(periodStr.substring(6, 8));
        
        dataMap.put("period", period);
        dataMap.put("month", month);
        dataMap.put("day", day);
        dataMap.put("companyInfo", companyInfo);
        
//        dataMap.put("G", this.getGData(fundId, periodStr));
//        dataMap.put("G300", this.getG300Data(fundId, periodStr));
//        dataMap.put("G10000", this.getG10000Data(fundId, periodStr));
        
        return FreeMarkerUtils.processTemplateToString(dataMap, Constants.EXPORT_TEMPLATE_FOLDER_PATH, Constants.EXPORT_TEMPLATE_FILE_NAME_H_SUM);
    }
    
    @Override
    protected Map<String, Object> createBaseQueryMap(String firmCode, String periodStr) {
        Map<String, Object> res = new HashMap<String,Object>();
        res.put("firmCode", firmCode);
        res.put("period", periodStr);
        return res;
    }
    
    /**
     * 根据公司Code获取公司信息
     * @author Dai Zong 2017年12月6日
     * 
     * @param firmCode 公司Code
     * @return 公司信息
     * @throws Exception 公司Code无效
     */
    private Map<String,String> selectCompanyInfo(String firmCode) throws Exception{
        Map<String, Object> query = new HashMap<String,Object>();
        query.put("companyCode", firmCode);
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> resMapList = (List<Map<String,Object>>)this.dao.findForList("MgrcompanyMapper.selectCompanyInfo", query);
        if(CollectionUtils.isEmpty(resMapList) || resMapList.size() != 1) {
            throw new Exception("管理公司Code " + firmCode + " 无效");
        }
        Map<String, String> res = new HashMap<String,String>();
        resMapList.get(0).forEach((k,v) -> {
            res.put(k, String.valueOf(v));
        });
        return res;
    }
    
	@Override
    public boolean doExport(HttpServletRequest request, HttpServletResponse response, String fundId, String periodStr) throws Exception {
	    /**
	     * 如果输入了fundInfo,则根据fundInfo反查到FirmCode之后处理。
	     */
	    Map<String, String> fundInfo = this.selectFundInfo(fundId);
        return this.doExport(fundInfo.get("firmCode"), periodStr, request, response);
    }
	
	@Override
    public boolean doExport(String folederName, String fileName, String fundId, String periodStr) throws Exception {
	    /**
         * 如果输入了fundInfo,则根据fundInfo反查到FirmCode之后处理。
         */
	    Map<String, String> fundInfo = this.selectFundInfo(fundId);
        return this.doExport(folederName, fileName, fundInfo.get("firmCode"), periodStr);
    }

    @Override
    public boolean doExport(String firmCode, String periodStr, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, String> companyInfo = this.selectCompanyInfo(firmCode);
        companyInfo.put("periodStr", periodStr);
        String xmlStr = this.generateFileContent(firmCode, periodStr, companyInfo);
        FileExportUtils.writeFileToHttpResponse(request, response, FreeMarkerUtils.simpleReplace(Constants.EXPORT_AIM_FILE_NAME_H_SUM, companyInfo), xmlStr);
        return true;
    }

    @Override
    public boolean doExport(String folederName, Object fileName, String firmCode, String periodStr) throws Exception {
        Map<String, String> companyInfo = this.selectCompanyInfo(firmCode);
        companyInfo.put("periodStr", periodStr);
        String xmlStr = this.generateFileContent(firmCode, periodStr, companyInfo);
        FileExportUtils.writeFileToDisk(folederName, FreeMarkerUtils.simpleReplace(String.valueOf(fileName), companyInfo), xmlStr);
        return true;
    }
    
}